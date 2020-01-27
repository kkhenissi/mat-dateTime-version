package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.domain.TransferArchive;
import com.airbus.archivemanager.domain.User;
import com.airbus.archivemanager.domain.enumeration.TransferStatus;
import com.airbus.archivemanager.repository.ITransferTmpRepository;
import com.airbus.archivemanager.repository.TransferArchiveRepository;
import com.airbus.archivemanager.service.*;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;
import com.airbus.archivemanager.service.dto.TransferArchiveDTO;
import com.airbus.archivemanager.service.mapper.TransferArchiveMapper;
import com.airbus.archivemanager.service.util.FilesUtil;
import com.airbus.archivemanager.service.util.Sha256Crc;
import io.undertow.util.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.annotation.Backoff;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.concurrent.ExecutorService;

/**
 * Service Implementation for managing {@link Transfer}.
 */
@Service
@Transactional
public class TransferArchiveServiceImpl implements TransferArchiveService {

    private static final String TMP_TRANSFER_OUT = "TMP_TRANSFER_OUT_";
    private static final String TMP_TRANSFER_IN = "TMP_TRANSFER_IN_";

    private final Logger log = LoggerFactory.getLogger(TransferArchiveServiceImpl.class);

    private final TransferArchiveRepository transferArchiveRepository;

    private final TransferArchiveMapper transferArchiveMapper;

    @Autowired
    private OutputFileService outputFileservice;

    @Autowired
    private ScenarioFileService scenarioFileservice;

    private final UserService userService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CheckCrc checkCrc;

    @Autowired
    private ITransferTmpRepository transferTmpRepository;

    private ExecutorService executor;

    private boolean errorFound = false;

    @PostConstruct
    void setGlobalSecuritycontext() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    public TransferArchiveServiceImpl(TransferArchiveRepository transferArchiveRepository,
            TransferArchiveMapper transferArchiveMapper, UserService userService) {
        this.transferArchiveRepository = transferArchiveRepository;
        this.transferArchiveMapper = transferArchiveMapper;
        this.userService = userService;
    }

    /**
     * Save a transfer.
     *
     * @param transferArchiveDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public TransferArchiveDTO save(TransferArchiveDTO transferArchiveDTO) {

        log.debug("Request to save Transfer : {}", transferArchiveDTO);
        User owner = userService.getOwner();
        transferArchiveDTO.setOwnerId(owner.getId());
        if (transferArchiveDTO.getArchiveDate() == null) {
            transferArchiveDTO.setArchiveDate(LocalDateTime.now());
        }
        if (transferArchiveDTO.getStatus() == null) {
            transferArchiveDTO.setStatus(TransferStatus.IN_PROGRESS);
        }
        int nbFilesToTransfer = transferArchiveDTO.getOutputFiles().size()
                + transferArchiveDTO.getScenarioFiles().size();

        TransferArchiveDTO cloneDTO = (TransferArchiveDTO) transferArchiveDTO.clone();

        cloneDTO.setFilesToTransfer(nbFilesToTransfer);
        cloneDTO.setFailedTransfered(0);
        cloneDTO.setSuccessfullyTransfered(0);

        TransferArchive transfer = transferArchiveMapper.toEntity(cloneDTO);

        transfer = transferArchiveRepository.saveAndFlush(transfer);

        executor = ExecutorServiceTransfer.getInstance().getExecutor();

        // Create a temporary table with OutputFiles
        final String tmpTableNameOut = TMP_TRANSFER_OUT + transfer.getId().toString();

        if (!transferArchiveDTO.getOutputFiles().isEmpty()) {
            transferTmpRepository.createOutputTable(tmpTableNameOut);
            transferArchiveDTO.getOutputFiles().forEach(f -> {
                f.setPathInLT(f.getRelativePathInST());
                f.setOwnerId(owner.getId());
                transferTmpRepository.saveOutputFile(tmpTableNameOut, f);
            });

            copyOutputFile(tmpTableNameOut, transfer);

        }

        // Create a temporary table with ScenarioFiles
        final String tmpTableNameIn = TMP_TRANSFER_IN + transfer.getId().toString();
        if (!transferArchiveDTO.getScenarioFiles().isEmpty()) {
            transferTmpRepository.createInputTable(tmpTableNameIn);
            transferArchiveDTO.getScenarioFiles().forEach(f -> {
                if (f.getScenarios() != null && f.getScenarios().size() > 1) {
                    log.error(f.getRelativePathInST() + " cannot be archived,it should only be linked to one scenario");
                    errorFound = true;
                } else {
                    f.setPathInLT(f.getRelativePathInST());
                    f.setOwnerId(owner.getId());
                    transferTmpRepository.saveScenarioFile(tmpTableNameIn, f);
                }

            });
            if (errorFound) {
                nbFilesToTransfer--;
            }

            copyInputFile(tmpTableNameIn, transfer);
        }

        return transferArchiveMapper.toDto(transfer);
    }

    /**
     * call the asynchronous copy file for each output file
     *
     * @param tmpTableName
     * @param transfer
     */
    private void copyOutputFile(String tmpTableName, TransferArchive transfer) {
        transferTmpRepository.findAllOutputFiles(tmpTableName).forEach(f -> {
            createAsynchronousChannel(tmpTableName, transfer, f);
        });
    }

    /**
     * call the asynchronous copy file for each input file
     *
     * @param tmpTableName
     * @param transfer
     */
    private void copyInputFile(String tmpTableName, TransferArchive transfer) {
        transferTmpRepository.findAllScenarioFiles(tmpTableName).forEach(f -> {
            createAsynchronousChannel(tmpTableName, transfer, f);
        });
    }

    private void createAsynchronousChannel(String tmpTableName, TransferArchive transfer, Object fileToTransfer) {
        String pathInST = null;
        String pathInLT = null;
        try {
            if (fileToTransfer instanceof OutputFileDTO) {
                pathInST = FilesUtil.createCompletePathInST(((OutputFileDTO) fileToTransfer).getRelativePathInST(),messageSource,log);
                pathInLT = FilesUtil.createCompletePathInLT(((OutputFileDTO) fileToTransfer).getPathInLT(),messageSource,log);
            } else {
                pathInST = FilesUtil.createCompletePathInST(((ScenarioFileDTO) fileToTransfer).getRelativePathInST(),messageSource,log);
                pathInLT = FilesUtil.createCompletePathInLT(((ScenarioFileDTO) fileToTransfer).getPathInLT(),messageSource,log);
            }
            // create the directory in LTS if necessary
            final Set<PosixFilePermission> ownerWritable = PosixFilePermissions.fromString("rwxr-xr-x");
            final FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(ownerWritable);
            FilesUtil.createDirectory(pathInLT, permissions);

            // create asynchronous file channels
            Set<StandardOpenOption> opts = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            final Set<PosixFilePermission> ownerWritableFile = PosixFilePermissions.fromString("rwxr-xr-x");
            final FileAttribute<?> permissionsFile = PosixFilePermissions.asFileAttribute(ownerWritableFile);
            AsynchronousFileChannel chDest;
            try {
                chDest = AsynchronousFileChannel.open(Paths.get(pathInLT), opts, executor, permissionsFile);
            } catch (UnsupportedOperationException e) {
                chDest = AsynchronousFileChannel.open(Paths.get(pathInLT), opts, executor);
            }
            final AsynchronousFileChannel chDestFinal = chDest;
            opts = EnumSet.of(StandardOpenOption.READ);
            final AsynchronousFileChannel chSrc = AsynchronousFileChannel.open(Paths.get(pathInST), opts, executor);
            // copy files
            executor.execute(() -> {
                try {
                    asynchronousCopyFile(chSrc, chDestFinal, fileToTransfer, tmpTableName, transfer, log);
                } catch (ArchiveManagerGenericException e) {
                    log.error("An unexpected error has occured during copy asynchronous file", e);
                }
            });
        } catch (IOException | ArchiveManagerGenericException ex) {
            log.error("An unexpected error has occured during copy asynchronous file", ex);
            transfer.setStatus(TransferStatus.FAILED);
            updateStatus(transfer);

        }
    }

    /**
     * copy file from STS to LTS
     *
     * @param chSrc
     * @param chDest
     * @param fileToTransfer
     * @param tmpTableName
     * @param transfer
     */
    private void asynchronousCopyFile(AsynchronousFileChannel chSrc, AsynchronousFileChannel chDest,
            Object fileToTransfer, String tmpTableName, TransferArchive transfer, Logger log)
            throws ArchiveManagerGenericException {
        ByteBuffer buffer = ByteBuffer.allocate(100000);

        String relativePathInST;
        try {
            if (fileToTransfer instanceof OutputFileDTO) {
                relativePathInST = ((OutputFileDTO) fileToTransfer).getRelativePathInST();
                ((OutputFileDTO) fileToTransfer)
                        .setCrc(Sha256Crc.generateSha(FilesUtil.createCompletePathInST(relativePathInST,messageSource,log)));
                checkCrc.checkIfOutputFileAlreadyExists((OutputFileDTO) fileToTransfer);

            } else {
                relativePathInST = ((ScenarioFileDTO) fileToTransfer).getRelativePathInST();
                ((ScenarioFileDTO) fileToTransfer)
                        .setCrc(Sha256Crc.generateSha(FilesUtil.createCompletePathInST(relativePathInST,messageSource,log)));
                checkCrc.checkIfScenarioFileAlreadyExists((ScenarioFileDTO) fileToTransfer);
            }
        } catch (NoSuchAlgorithmException | IOException | ArchiveManagerGenericException e) {
            incrementFailed(transfer);
            throw new ArchiveManagerGenericException(e.getMessage());
        }

        class ReadWriteCompletionHandler implements CompletionHandler<Integer, Long> {
            @Override
            public void completed(Integer result, Long pos) {
                if (result == -1) {
                    try {
                        chSrc.close();
                        chDest.close();
                    } catch (IOException ex) {
                        throw new ArchiveManagerGenericException("I/O Error close canal" + ex.getCause());
                    }

                    log.debug("End of copy " + relativePathInST);
                    try {
                        if (fileToTransfer instanceof OutputFileDTO) {
                            outputFileservice.save((OutputFileDTO) fileToTransfer);

                        } else {
                            scenarioFileservice.save((ScenarioFileDTO) fileToTransfer);
                        }
                    } catch (Exception ex) {
                        log.error("Transfer File Failed: " + ex.getMessage(), ex);

                        incrementFailed(transfer);

                        FilesUtil.deleteFileInLTS(fileToTransfer, scenarioFileservice, outputFileservice, messageSource,
                                log);
                        throw new ArchiveManagerGenericException(ex.getMessage());
                    }

                    incrementSuccess(transfer);

                    if (transfer.getSuccessfullyTransfered() == transfer.getFilesToTransfer()) {
                        transfer.setStatus(TransferStatus.DONE);
                        updateStatus(transfer);
                        try {
                            transferTmpRepository.dropTable(tmpTableName);
                        } catch (Exception e) {
                            log.warn("Unable to drop temporary table");
                        }

                    }

                    Thread.currentThread().interrupt();
                    return;
                }

                buffer.flip();
                chDest.write(buffer, pos, pos + result, new CompletionHandler<Integer, Long>() {
                    @Override
                    public void completed(Integer result, Long newPos) {
                        buffer.compact();
                        chSrc.read(buffer, newPos, newPos, ReadWriteCompletionHandler.this);
                    }

                    @Override
                    public void failed(Throwable t, Long pos) {
                        log.error("Transfer File Failed: " + t.getMessage(), t);

                        incrementFailed(transfer);
                        Thread.currentThread().interrupt();

                    }
                });
            }

            @Override
            public void failed(Throwable t, Long pos) {
                log.error("Transfer File Failed: " + t.getMessage(), t);
                incrementFailed(transfer);
                Thread.currentThread().interrupt();
            }
        }

        chSrc.read(buffer, 0, 0L, new ReadWriteCompletionHandler());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransferArchive updateStatus(TransferArchive transfer) {
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                pause(200);
            }
            Optional<TransferArchive> transferArchive = transferArchiveRepository.findById(transfer.getId());
            if (transferArchive.isPresent()) {
                transferArchiveRepository.save(transfer);
                return transfer;
            } else {
                retry++;
                delay = true;
            }
        } while (retry < 10);
        return transfer;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransferArchive incrementSuccess(TransferArchive transfer) {
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                pause(200);
            }
            Optional<TransferArchive> transferArchive = transferArchiveRepository.findById(transfer.getId());
            if (transferArchive.isPresent()) {
                transfer.setSuccessfullyTransfered(transfer.getSuccessfullyTransfered() + 1);
                transferArchiveRepository.save(transfer);
                return transfer;
            } else {
                retry++;
                delay = true;
            }
        } while (retry < 10);
        return transfer;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransferArchive incrementFailed(TransferArchive transfer) {
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                pause(200);
            }
            Optional<TransferArchive> transferArchive = transferArchiveRepository.findById(transfer.getId());
            if (transferArchive.isPresent()) {
                transfer.setStatus(TransferStatus.FAILED);
                transfer.setFailedTransfered(transfer.getFailedTransfered() + 1);
                transferArchiveRepository.save(transfer);
                return transfer;
            } else {
                retry++;
                delay = true;
            }
        } while (retry < 10);
        return transfer;
    }

    /**
     * Get all the transfers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransferArchiveDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Transfers");
        return transferArchiveRepository.findAll(pageable).map(transferArchiveMapper::toDto);
    }

    /**
     * Get one transfer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TransferArchiveDTO> findOne(Long id) {
        log.debug("Request to get Transfer : {}", id);
        return transferArchiveRepository.findById(id).map(transferArchiveMapper::toDto);
    }

    /**
     * Delete the transfer by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Transfer : {}", id);
        transferArchiveRepository.deleteById(id);
        transferTmpRepository.dropTable(TMP_TRANSFER_OUT + id);
        transferTmpRepository.dropTable(TMP_TRANSFER_IN + id);
    }

    private void pause(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
