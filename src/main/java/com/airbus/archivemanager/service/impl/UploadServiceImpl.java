package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.config.ApplicationProperties;
import com.airbus.archivemanager.domain.Upload;
import com.airbus.archivemanager.domain.User;
import com.airbus.archivemanager.domain.enumeration.FileType;
import com.airbus.archivemanager.repository.UploadRepository;
import com.airbus.archivemanager.service.*;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;
import com.airbus.archivemanager.service.dto.UploadDTO;
import com.airbus.archivemanager.service.mapper.UploadMapper;
import com.airbus.archivemanager.service.util.FilesUtil;
import com.airbus.archivemanager.service.util.Sha256Crc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UploadServiceImpl implements UploadService {

    private final Logger log = LoggerFactory.getLogger(UploadServiceImpl.class);
    private final ScenarioFileService scenarioFileService;
    private final OutputFileService outputFileService;
    private final MessageSource messageSource;
    private final UploadMapper uploadMapper;
    private final UploadRepository uploadRepository;
    private final UserService userService;

    @Autowired
    private UploadService uploadService;

    public UploadServiceImpl(ScenarioFileService scenarioFileService, OutputFileService outputFileService, MessageSource messageSource, UploadMapper uploadMapper, UploadRepository uploadRepository, UserService userService) {
        this.scenarioFileService = scenarioFileService;
        this.outputFileService = outputFileService;
        this.messageSource = messageSource;
        this.uploadMapper = uploadMapper;
        this.uploadRepository = uploadRepository;
        this.userService = userService;
    }

    /**
     * copy file from local to LTS
     *
     * @param uploadDTO DTO to transfer in LTS
     * @return UploadDTO saved in DB
     */
    @Override
    public UploadDTO uploadFile(UploadDTO uploadDTO) {
        // Check if relativePathInST already exists in ScenarioFile table before transfer
        if (scenarioFileService.findOne(uploadDTO.getRelativePathInST()).isPresent()) {
            String message = messageSource.getMessage("error.scenarioFile.scenarioFileAlreadyExists", null, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
        // Check if relativePathInST already exists in OutputFile table before transfer
        if (outputFileService.findOne(uploadDTO.getRelativePathInST()).isPresent()) {
            String message = messageSource.getMessage("error.outputFile.outputFileAlreadyExists", null, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
        //File copy from local to LTS
        fileTransfer(uploadDTO);
        // Compare local CRC with LTS CRC
        try {
            if (Sha256Crc.generateSha(uploadDTO.getLocalPath()).equals(Sha256Crc.generateSha(uploadDTO.getlTSPath()))) {
                uploadDTO.setCrc(Sha256Crc.generateSha(uploadDTO.getlTSPath()));
            } else {
                FilesUtil.deleteFileInLTS(uploadDTO.getlTSPath(), messageSource, log);
                String message = messageSource.getMessage("error.CrcDifferent", null, Locale.ENGLISH);
                log.error(message);
                throw new ArchiveManagerGenericException(message);
            }
        } catch (NoSuchAlgorithmException | IOException ex) {
            FilesUtil.deleteFileInLTS(uploadDTO.getlTSPath(), messageSource, log);
            throw new ArchiveManagerGenericException(ex.getMessage());
        }
        // erased root path in LTSPath from uploadDTO
        uploadDTO.setlTSPath(uploadDTO.getlTSPath().substring(ApplicationProperties.getRootPathInLT().length()));
        // Transform uploadDTO into ScenarioFile or OutputFile if metadata are correctly filled
        try {
            if (uploadDTO.getInputType() != null && (uploadDTO.getInputType().equals(FileType.CONFIG.toString()) ||
                uploadDTO.getInputType().equals(FileType.INPUT.toString()))) {
                scenarioFileService.save(convertUploadDTOToScenarioFileDTO(uploadDTO));
            } else if (uploadDTO.getRunId() != null) {
                outputFileService.save(convertUploadDTOToOutputFileDTO(uploadDTO));
            } else {
                FilesUtil.deleteFileInLTS(uploadDTO.getlTSPath(), messageSource, log);
                String message = messageSource.getMessage("error.inconsistencyData", null, Locale.ENGLISH);
                log.error(message);
                throw new ArchiveManagerGenericException(message);
            }
        } catch (Exception e) {
            Path path = Paths.get(FilesUtil.createCompletePathInLT(uploadDTO.getlTSPath(), messageSource, log));
            if (path.toFile().exists()) {
                FilesUtil.deleteFileInLTS(uploadDTO.getlTSPath(), messageSource, log);
            }
            throw new ArchiveManagerGenericException(e.getMessage());
        }
        User owner = userService.getOwner();
        uploadDTO.setOwnerId(owner.getId());
        if (uploadDTO.getlTInsertionDate() == null) {
            uploadDTO.setlTInsertionDate(LocalDateTime.now());
        }
        Upload upload = uploadMapper.toEntity(uploadDTO);
        upload = uploadRepository.save(upload);
        return uploadMapper.toDto(upload);
    }

    /**
     * copy  on disk file from local to LTS
     *
     * @param uploadDTO DTO to copy in LTS
     */
    private void fileTransfer(UploadDTO uploadDTO) {


        try (FileChannel inChannel = new FileInputStream(uploadDTO.getLocalPath()).getChannel();
             FileChannel outChannel = new FileOutputStream(uploadDTO.getlTSPath()).getChannel()) {
            int maxCount = (64 * 1024 * 1024) - (32 * 1024);
            long size = inChannel.size();
            long position = 0;
            while (position < size) {
                position += inChannel.transferTo(position, maxCount, outChannel);
            }
        } catch (IOException e) {
            FilesUtil.deleteFileInLTS(uploadDTO.getlTSPath(), messageSource, log);
            throw new ArchiveManagerGenericException(e.getMessage());
        }
    }

    /**
     * convert UploadDTO into ScenarioFileDTO
     *
     * @param uploadDTO DTO to convert
     * @return scenarioFileDTO
     */
    private ScenarioFileDTO convertUploadDTOToScenarioFileDTO(UploadDTO uploadDTO) {
        ScenarioFileDTO scenarioFileDTO = new ScenarioFileDTO();
        scenarioFileDTO.setFileType(uploadDTO.getFileType());
        scenarioFileDTO.setRelativePathInST(uploadDTO.getRelativePathInST());
        scenarioFileDTO.setlTInsertionDate(uploadDTO.getlTInsertionDate());
        scenarioFileDTO.setPathInLT(uploadDTO.getlTSPath());
        scenarioFileDTO.setFormat(uploadDTO.getFormat());
        scenarioFileDTO.setSubSystemAtOriginOfData(uploadDTO.getSubSystemAtOriginOfData());
        scenarioFileDTO.setTimeOfData(uploadDTO.getTimeOfData());
        scenarioFileDTO.setSecurityLevel(uploadDTO.getSecurityLevel());
        scenarioFileDTO.setCrc(uploadDTO.getCrc());
        scenarioFileDTO.setOwnerId(uploadDTO.getOwnerId());
        scenarioFileDTO.setScenarios(uploadDTO.getScenarios());
        scenarioFileDTO.setConfigDatasetId(uploadDTO.getConfigDatasetId());
        scenarioFileDTO.setDatasetId(uploadDTO.getDatasetId());
        if (uploadDTO.getInputType().equals(FileType.INPUT.toString())) {
            scenarioFileDTO.setInputType(FileType.INPUT);
        } else {
            scenarioFileDTO.setInputType(FileType.CONFIG);
        }
        return scenarioFileDTO;
    }

    /**
     * convert UploadDTO into OutputFileDTO
     *
     * @param uploadDTO DTO to convert
     * @return outputFileDTO
     */
    private OutputFileDTO convertUploadDTOToOutputFileDTO(UploadDTO uploadDTO) {
        OutputFileDTO outputFileDTO = new OutputFileDTO();
        outputFileDTO.setRelativePathInST(uploadDTO.getRelativePathInST());
        outputFileDTO.setlTInsertionDate(uploadDTO.getlTInsertionDate());
        outputFileDTO.setPathInLT(uploadDTO.getlTSPath());
        outputFileDTO.setFileType(uploadDTO.getFileType());
        outputFileDTO.setFormat(uploadDTO.getFormat());
        outputFileDTO.setSubSystemAtOriginOfData(uploadDTO.getSubSystemAtOriginOfData());
        outputFileDTO.setSecurityLevel(uploadDTO.getSecurityLevel());
        outputFileDTO.setCrc(uploadDTO.getCrc());
        outputFileDTO.setOwnerId(uploadDTO.getOwnerId());
        outputFileDTO.setRunId(uploadDTO.getRunId());
        return outputFileDTO;
    }

    /**
     * Get all the uploads.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UploadDTO> findAll() {
        log.debug("Request to get all uploads");
        return uploadRepository.findAll().stream()
            .map(uploadMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one upload by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UploadDTO> findOne(Long id) {
        log.debug("Request to get upload : {}", id);
        return uploadRepository.findById(id)
            .map(uploadMapper::toDto);
    }

    /**
     * Delete the upload by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete upload : {}", id);
        Optional<UploadDTO> uploadDTOOptional = uploadService.findOne(id);
        if (!uploadDTOOptional.isPresent()) {
            String[] variableArray = new String[]{String.valueOf(id)};
            String message = messageSource.getMessage("error.outputFile.outputFileNotDeletedInDB", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
        UploadDTO uploadDTO = uploadDTOOptional.get();
        if (uploadDTO.getRunId() != null) {
            OutputFileDTO outputFileDTO = new OutputFileDTO();
            outputFileDTO.setRelativePathInST(uploadDTO.getRelativePathInST());
            outputFileService.delete(outputFileDTO);
        } else if (uploadDTO.getInputType() != null) {
            ScenarioFileDTO scenarioFileDTO = new ScenarioFileDTO();
            scenarioFileDTO.setRelativePathInST(uploadDTO.getRelativePathInST());
            scenarioFileService.delete(scenarioFileDTO);
        } else {
            String message = messageSource.getMessage("error.inconsistencyData", null, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
        uploadRepository.deleteById(id);
    }
}
