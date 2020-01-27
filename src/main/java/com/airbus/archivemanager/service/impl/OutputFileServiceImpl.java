package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.domain.OutputFile;
import com.airbus.archivemanager.domain.User;
import com.airbus.archivemanager.domain.enumeration.SecurityLevel;
import com.airbus.archivemanager.repository.OutputFileRepository;
import com.airbus.archivemanager.service.ArchiveManagerGenericException;
import com.airbus.archivemanager.service.OutputFileService;
import com.airbus.archivemanager.service.UserService;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.mapper.OutputFileMapper;
import com.airbus.archivemanager.service.util.FilesUtil;
import com.airbus.archivemanager.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

/**
 * Service Implementation for managing {@link OutputFile}.
 */
@Service
@Transactional
public class OutputFileServiceImpl implements OutputFileService {

    private final Logger log = LoggerFactory.getLogger(OutputFileServiceImpl.class);

    private final OutputFileRepository outputFileRepository;

    private final OutputFileMapper outputFileMapper;

    private final UserService userService;

    private final MessageSource messageSource;

    private final RunServiceImpl runServiceImpl;

    public OutputFileServiceImpl(OutputFileRepository outputFileRepository, OutputFileMapper outputFileMapper, UserService userService, MessageSource messageSource, RunServiceImpl runServiceImpl) {
        this.outputFileRepository = outputFileRepository;
        this.outputFileMapper = outputFileMapper;
        this.userService = userService;
        this.messageSource = messageSource;
        this.runServiceImpl = runServiceImpl;
    }

    /**
     * Save a outputFile.
     *
     * @param outputFileDTO the entity to save.
     * @return the persisted entity.
     * @throws BadRequestAlertException if the relative path in STS is null.
     * @throws BadRequestAlertException if RunId is null or equals to 0.
     */
    @Override
    public OutputFileDTO save(OutputFileDTO outputFileDTO) {
        log.debug("Request to save OutputFile : {}", outputFileDTO);
        if (outputFileDTO.getRelativePathInST() == null) {
            log.error("relativePathInST is null");
            String message = messageSource.getMessage("error.outputFile.InvalidRelativePathInST", null, Locale.ENGLISH);
            throw new ArchiveManagerGenericException(message);
        }
        if (outputFileDTO.getRunId() == null || !(runServiceImpl.findOne(outputFileDTO.getRunId()).isPresent())) {
            log.error("runId is null or unknown");
            String message = messageSource.getMessage("error.outputFile.runNullOrUnknown", null, Locale.ENGLISH);
            throw new ArchiveManagerGenericException(message);
        }
        if (outputFileDTO.getSecurityLevel() == null) {
            outputFileDTO.setSecurityLevel(SecurityLevel.NORMAL);
        }
        if (outputFileDTO.getlTInsertionDate() == null) {
            outputFileDTO.setlTInsertionDate(LocalDateTime.now());
        }
        if (outputFileDTO.getOwnerId() == null) {
            User owner = userService.getOwner();
            outputFileDTO.setOwnerId(owner.getId());
        }
        OutputFile outputFile = outputFileMapper.toEntity(outputFileDTO);
        outputFile = outputFileRepository.save(outputFile);
        return outputFileMapper.toDto(outputFile);
    }

    /**
     * Get all the outputFiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OutputFileDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OutputFiles");
        return outputFileRepository.findAll(pageable)
            .map(outputFileMapper::toDto);
    }


    /**
     * Get one outputFile by relativePathInST.
     *
     * @param relativePathInST the relativePathInST of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<OutputFileDTO> findOne(String relativePathInST) {
        log.debug("Request to get OutputFile : {}", relativePathInST);
        return outputFileRepository.findById(relativePathInST)
            .map(outputFileMapper::toDto);
    }

    /**
     * Delete the outputFile by outputFileDTO.
     *
     * @param outputFileDTO the entity.
     */
    @Override
    public void delete(OutputFileDTO outputFileDTO) {
        log.debug("Request to delete OutputFile : {}", outputFileDTO.getRelativePathInST());
        outputFileRepository.deleteById(outputFileDTO.getRelativePathInST());
        if (outputFileRepository.existsById(outputFileDTO.getRelativePathInST())) {
            String[] variableArray = new String[]{String.valueOf(outputFileDTO.getRelativePathInST())};
            String message = messageSource.getMessage("error.outputFile.outputFileNotDeletedInDB", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
        FilesUtil.deleteFileInLTS(outputFileDTO.getPathInLT(), messageSource, log);
    }

    /**
     * Update a outputFile.
     *
     * @param outputFileDTO the entity to update.
     * @return the persisted entity.
     * @throws BadRequestAlertException if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException if RunId is null or equals to 0.
     */
    @Override
    public OutputFileDTO update(OutputFileDTO outputFileDTO) {
        log.debug("Request to update OutputFile : {}", outputFileDTO);
        if (outputFileDTO.getRunId() == null || !(runServiceImpl.findOne(outputFileDTO.getRunId()).isPresent())) {
            log.error("runId is null or unknown");
            String message = messageSource.getMessage("error.outputFile.runNullOrUnknown", null, Locale.ENGLISH);
            throw new ArchiveManagerGenericException(message);
        }
        OutputFile outputFile = outputFileMapper.toEntity(outputFileDTO);
        outputFile = outputFileRepository.save(outputFile);
        return outputFileMapper.toDto(outputFile);
    }
}
