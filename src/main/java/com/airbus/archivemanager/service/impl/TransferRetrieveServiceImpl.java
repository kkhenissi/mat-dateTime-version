package com.airbus.archivemanager.service.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;

import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.util.EnumSet;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import com.airbus.archivemanager.domain.TransferRetrieve;
import com.airbus.archivemanager.domain.User;
import com.airbus.archivemanager.domain.enumeration.TransferStatus;
import com.airbus.archivemanager.repository.TransferRetrieveRepository;
import com.airbus.archivemanager.repository.ITransferTmpRepository;
import com.airbus.archivemanager.service.ArchiveManagerGenericException;
import com.airbus.archivemanager.service.TransferRetrieveService;
import com.airbus.archivemanager.service.UserService;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;
import com.airbus.archivemanager.service.dto.TransferRetrieveDTO;
import com.airbus.archivemanager.service.mapper.TransferRetrieveMapper;
import com.airbus.archivemanager.service.util.FilesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.retry.annotation.Backoff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import io.undertow.util.Transfer;

/**
 * Service Implementation for managing {@link Transfer}.
 */
@Service
@Transactional
public class TransferRetrieveServiceImpl implements TransferRetrieveService {

    private static final String TMP_TRANSFER_RETRIEVE_OUT = "TMP_TRANSFER_RETRIEVE_OUT_";
    private static final String TMP_TRANSFER_RETRIEVE_IN = "TMP_TRANSFER_RETRIEVE_IN_";

    private final Logger log = LoggerFactory.getLogger(TransferRetrieveServiceImpl.class);

    private final TransferRetrieveRepository transferRetrieveRepository;

    private final TransferRetrieveMapper transferRetrieveMapper;

    private final UserService userService;

    @Autowired
    private ITransferTmpRepository transferTmpRepository;
    @Autowired
    private CheckCrc checkCrc;
    @Autowired
    private MessageSource messageSource;

    private ExecutorService executor;

    @PostConstruct
    void setGlobalSecuritycontext() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    public TransferRetrieveServiceImpl(TransferRetrieveRepository transferRetrieveRepository,
            TransferRetrieveMapper transferRetrieveMapper, UserService userService) {
        this.transferRetrieveRepository = transferRetrieveRepository;
        this.transferRetrieveMapper = transferRetrieveMapper;
        this.userService = userService;
    }

    /**
     * Save a transfer.
     *
     * @param transferRetrieveDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public TransferRetrieveDTO save(TransferRetrieveDTO transferRetrieveDTO) {

        log.debug("Request to save Transfer : {}", transferRetrieveDTO);
        User owner = userService.getOwner();
        transferRetrieveDTO.setOwnerId(owner.getId());
        if (transferRetrieveDTO.getRetrieveDate() == null) {
            transferRetrieveDTO.setRetrieveDate(LocalDateTime.now());
        }
        if (transferRetrieveDTO.getStatus() == null) {
            transferRetrieveDTO.setStatus(TransferStatus.IN_PROGRESS);
        }
        int nbFilesToTransfer = transferRetrieveDTO.getOutputFiles().size()
                + transferRetrieveDTO.getScenarioFiles().size();
        TransferRetrieveDTO cloneDTO = (TransferRetrieveDTO) transferRetrieveDTO.clone();
        cloneDTO.setFilesToTransfer(nbFilesToTransfer);
        cloneDTO.setFailedTransfered(0);
        cloneDTO.setSuccessfullyTransfered(0);
        TransferRetrieve transfer = transferRetrieveMapper.toEntity(cloneDTO);

        transfer = transferRetrieveRepository.saveAndFlush(transfer);

        executor = ExecutorServiceTransfer.getInstance().getExecutor();

        // Create a temporary table with OutputFiles
        final String tmpTableNameOut = TMP_TRANSFER_RETRIEVE_OUT + transfer.getId().toString();

        if (!transferRetrieveDTO.getOutputFiles().isEmpty()) {
            transferTmpRepository.createOutputTable(tmpTableNameOut);

            transferRetrieveDTO.getOutputFiles().forEach(f -> {
                f.setPathInLT(f.getRelativePathInST());
                f.setOwnerId(owner.getId());
                if (transferRetrieveDTO.getRetrieveDate() == null) {
                    transferRetrieveDTO.setRetrieveDate(LocalDateTime.now());
                }
                transferTmpRepository.saveOutputFile(tmpTableNameOut, f);
            });

            copyOutputFile(tmpTableNameOut, transfer);
        }

        // Create a temporary table with ScenarioFiles
        final String tmpTableNameIn = TMP_TRANSFER_RETRIEVE_IN + transfer.getId().toString();
        if (!transferRetrieveDTO.getScenarioFiles().isEmpty()) {
            transferTmpRepository.createInputTable(tmpTableNameIn);
            transferRetrieveDTO.getScenarioFiles().forEach(f -> {
                f.setPathInLT(f.getRelativePathInST());
                f.setOwnerId(owner.getId());
                transferTmpRepository.saveScenarioFile(tmpTableNameIn, f);
            });

            copyInputFile(tmpTableNameIn, transfer);
        }

        return transferRetrieveMapper.toDto(transfer);
    }

    /**
     * call the asynchronous copy file for each output file
     *
     * @param tmpTableName
     * @param transfer
     */
    private void copyOutputFile(String tmpTableName, TransferRetrieve transfer) {
        transferTmpRepository.findAllOutputFiles(tmpTableName)
                .forEach(f -> createAsynchronousChannel(tmpTableName, transfer, f));
    }

    /**
     * call the asynchronous copy file for each input file
     *
     * @param tmpTableName
     * @param transfer
     */
    private void copyInputFile(String tmpTableName, TransferRetrieve transfer) {
        transferTmpRepository.findAllScenarioFiles(tmpTableName)
                .forEach(f -> createAsynchronousChannel(tmpTableName, transfer, f));
    }

    private void createAsynchronousChannel(String tmpTableName, TransferRetrieve transfer, Object fileToTransfer) {
        String pathInST = null;
        String relativePathInST = null;
        String pathInLT = null;
        try {
            if (fileToTransfer instanceof OutputFileDTO) {
                pathInST = FilesUtil.createCompletePathInST(((OutputFileDTO) fileToTransfer).getRelativePathInST(),messageSource,log);
                relativePathInST = ((OutputFileDTO) fileToTransfer).getRelativePathInST();
                pathInLT = FilesUtil.createCompletePathInLT(((OutputFileDTO) fileToTransfer).getPathInLT(),messageSource,log);
            } else {
                pathInST = FilesUtil.createCompletePathInST(((ScenarioFileDTO) fileToTransfer).getRelativePathInST(),messageSource,log);
                relativePathInST = ((ScenarioFileDTO) fileToTransfer).getRelativePathInST();
                pathInLT = FilesUtil.createCompletePathInLT(((ScenarioFileDTO) fileToTransfer).getPathInLT(),messageSource,log);
            }
            // send a warning if path in STS already exists
            checkCrc.checkIfFileAlreadyExistsInSTSBeforeRetrieve(relativePathInST);

            // create the directory in STS if necessary
            final Set<PosixFilePermission> ownerWritable = PosixFilePermissions.fromString("rwxrwxr-x");
            final FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(ownerWritable);
            FilesUtil.createDirectory(pathInST, permissions);

            // create asynchronous file channels
            Set<StandardOpenOption> opts = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            final Set<PosixFilePermission> ownerWritableFile = PosixFilePermissions.fromString("rwxrwxr-x");
            final FileAttribute<?> permissionsFile = PosixFilePermissions.asFileAttribute(ownerWritableFile);
            AsynchronousFileChannel chDest;
            try {
                chDest = AsynchronousFileChannel.open(Paths.get(pathInST), opts, executor, permissionsFile);
            } catch (UnsupportedOperationException e) {
                chDest = AsynchronousFileChannel.open(Paths.get(pathInST), opts, executor);
            }
            final AsynchronousFileChannel chDestFinal = chDest;
            opts = EnumSet.of(StandardOpenOption.READ);
            AsynchronousFileChannel chSrc = AsynchronousFileChannel.open(Paths.get(pathInLT), opts, executor);
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
     * copy file from LTS to STS
     *
     * @param chSrc
     * @param chDest
     * @param fileToTransfer
     * @param tmpTableName
     * @param transfer
     */
    private void asynchronousCopyFile(AsynchronousFileChannel chSrc, AsynchronousFileChannel chDest,
            Object fileToTransfer, String tmpTableName, TransferRetrieve transfer, Logger log) {
        ByteBuffer buffer = ByteBuffer.allocate(100000);

        String relativePathInST;
        if (fileToTransfer instanceof OutputFileDTO) {
            relativePathInST = ((OutputFileDTO) fileToTransfer).getRelativePathInST();
        } else {
            relativePathInST = ((ScenarioFileDTO) fileToTransfer).getRelativePathInST();
        }

        class ReadWriteCompletionHandler implements CompletionHandler<Integer, Long> {
            @Override
            public void completed(Integer result, Long pos) {
                if (result == -1) {
                    try {
                        chSrc.close();
                        chDest.close();
                    } catch (IOException ex) {
                        log.error("I/O Error close canal" + ex.getCause());
                    }

                    log.debug("end of copy ", relativePathInST);

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
    public TransferRetrieve updateStatus(TransferRetrieve transfer) {
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                pause(200);
            }
            Optional<TransferRetrieve> transferRetrieve = transferRetrieveRepository.findById(transfer.getId());
            if (transferRetrieve.isPresent()) {
                transferRetrieveRepository.save(transfer);
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
    public TransferRetrieve incrementSuccess(TransferRetrieve transfer) {
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                pause(200);
            }
            Optional<TransferRetrieve> transferRetrieve = transferRetrieveRepository.findById(transfer.getId());
            if (transferRetrieve.isPresent()) {
                transfer.setSuccessfullyTransfered(transfer.getSuccessfullyTransfered() + 1);
                transferRetrieveRepository.save(transfer);
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
    public TransferRetrieve incrementFailed(TransferRetrieve transfer) {
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                pause(200);
            }
            Optional<TransferRetrieve> transferRetrieve = transferRetrieveRepository.findById(transfer.getId());
            if (transferRetrieve.isPresent()) {
                transfer.setStatus(TransferStatus.FAILED);
                transfer.setFailedTransfered(transfer.getFailedTransfered() + 1);
                transferRetrieveRepository.save(transfer);
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
    public Page<TransferRetrieveDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Transfers");
        return transferRetrieveRepository.findAll(pageable).map(transferRetrieveMapper::toDto);
    }

    /**
     * Get one transfer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TransferRetrieveDTO> findOne(Long id) {
        log.debug("Request to get Transfer : {}", id);
        return transferRetrieveRepository.findById(id).map(transferRetrieveMapper::toDto);
    }

    /**
     * Delete the transfer by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Transfer : {}", id);
        transferRetrieveRepository.deleteById(id);
        transferTmpRepository.dropTable(TMP_TRANSFER_RETRIEVE_OUT + id);
        transferTmpRepository.dropTable(TMP_TRANSFER_RETRIEVE_IN + id);
    }

    private void pause(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
