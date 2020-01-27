package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.domain.ScenarioFile;
import com.airbus.archivemanager.domain.User;
import com.airbus.archivemanager.domain.enumeration.FileType;
import com.airbus.archivemanager.domain.enumeration.SecurityLevel;
import com.airbus.archivemanager.repository.ScenarioFileRepository;
import com.airbus.archivemanager.service.*;
import com.airbus.archivemanager.service.dto.*;
import com.airbus.archivemanager.service.mapper.ScenarioFileMapper;
import com.airbus.archivemanager.service.util.FilesUtil;
import io.github.jhipster.service.filter.LongFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service Implementation for managing {@link ScenarioFile}.
 */
@Service
@Transactional
public class ScenarioFileServiceImpl implements ScenarioFileService {

    private final Logger log = LoggerFactory.getLogger(ScenarioFileServiceImpl.class);

    private final ScenarioFileRepository scenarioFileRepository;

    private final ScenarioFileMapper scenarioFileMapper;

    private final UserService userService;

    private final MessageSource messageSource;

    @Autowired
    private OutputFileService outputFileService;
    @Autowired
    private RunService runService;
    @Autowired
    private ScenarioService scenarioService;
    @Autowired
    private ScenarioFileQueryService scenarioFileQueryService;
    @Autowired
    private ScenarioQueryService scenarioQueryService;
    @Autowired
    private ScenarioFileService scenarioFileService;

    public ScenarioFileServiceImpl(ScenarioFileRepository scenarioFileRepository, ScenarioFileMapper scenarioFileMapper, UserService userService, MessageSource messageSource) {
        this.scenarioFileRepository = scenarioFileRepository;
        this.scenarioFileMapper = scenarioFileMapper;
        this.messageSource = messageSource;
        this.userService = userService;
    }

    /**
     * Save a scenarioFile.
     *
     * @param scenarioFileDTO the entity to save.
     * @return the persisted entity.
     * @throws ArchiveManagerGenericException if RelativePathInST is null.
     * @throws ArchiveManagerGenericException if a CONFIG file is linked with a dataSet.
     * @throws ArchiveManagerGenericException if a INPUT file is linked with a configDataSet.
     */
    @Override
    public ScenarioFileDTO save(ScenarioFileDTO scenarioFileDTO) {
        log.debug("Request to save ScenarioFile : {}", scenarioFileDTO);
        if (scenarioFileDTO.getRelativePathInST() == null) {
            log.error("Invalid relative path in STS (null)");
            String message = messageSource.getMessage("error.scenarioFile.relativePathInSTNull", null, Locale.ENGLISH);
            throw new ArchiveManagerGenericException(message);
        }
        if (scenarioFileDTO.getSecurityLevel() == null) {
            scenarioFileDTO.setSecurityLevel(SecurityLevel.NORMAL);
        }

        //Verify if an INPUTFile has not been associated with ConfigDataSet or a CONFIGFile has not been associated with DataSet
        checkInputTypeWithSet(scenarioFileDTO);

        //Verify if ScenarioFile has not been associated with unknown Scenario
        checkScenarios(scenarioFileDTO);

        if (scenarioFileDTO.getlTInsertionDate() == null) {
            scenarioFileDTO.setlTInsertionDate(LocalDateTime.now());
        }
        if (scenarioFileDTO.getOwnerId() == null) {
            User owner = userService.getOwner();
            scenarioFileDTO.setOwnerId(owner.getId());
        }
        ScenarioFile scenarioFile = scenarioFileMapper.toEntity(scenarioFileDTO);
        scenarioFile = scenarioFileRepository.save(scenarioFile);
        return scenarioFileMapper.toDto(scenarioFile);
    }

    /**
     * Get all the scenarioFiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ScenarioFileDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ScenarioFiles");
        return scenarioFileRepository.findAll(pageable)
            .map(scenarioFileMapper::toDto);
    }

    /**
     * Get all the scenarioFiles with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ScenarioFileDTO> findAllWithEagerRelationships(Pageable pageable) {
        return scenarioFileRepository.findAllWithEagerRelationships(pageable).map(scenarioFileMapper::toDto);
    }


    /**
     * Get one scenarioFile by relativePathInST.
     *
     * @param relativePathInST the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ScenarioFileDTO> findOne(String relativePathInST) {
        log.debug("Request to get ScenarioFile : {}", relativePathInST);
        return scenarioFileRepository.findOneWithEagerRelationships(relativePathInST)
            .map(scenarioFileMapper::toDto);
    }

    /**
     * Delete the scenarioFile by id.
     *
     * @param scenarioFileDTO the entity.
     */
    @Override
    public void delete(ScenarioFileDTO scenarioFileDTO) {
        log.debug("Request to delete ScenarioFile : {}", scenarioFileDTO.getRelativePathInST());
        scenarioFileRepository.deleteById(scenarioFileDTO.getRelativePathInST());
        if (scenarioFileRepository.existsById(scenarioFileDTO.getRelativePathInST())) {
            String[] variableArray = new String[]{String.valueOf(scenarioFileDTO.getRelativePathInST())};
            String message = messageSource.getMessage("error.outputFile.scenarioFileNotDeletedInDB", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
        FilesUtil.deleteFileInLTS(scenarioFileDTO.getPathInLT(), messageSource, log);
    }

    @Override
    public Page<ScenarioFileDTO> findByOutputFileRelativePathInST(OutputFileDTO outputFileDTO, Pageable pageable) {
        log.debug("Request to get ScenarioFiles by outputFile relativePathInST");
        return scenarioFileQueryService.findByCriteria(scenarioFileCriteriaFromOutputFile(outputFileDTO), pageable);
    }

    /**
     * Update a scenarioFile.
     *
     * @param scenarioFileDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ScenarioFileDTO update(ScenarioFileDTO scenarioFileDTO) {
        log.debug("Request to update ScenarioFile : {}", scenarioFileDTO);
        //Verify if an INPUTFile has not been associated with ConfigDataSet or a CONFIGFile has not been associated with DataSet
        checkInputTypeWithSet(scenarioFileDTO);

        //Verify if ScenarioFileDTO has not been associated with unknown Scenario
        checkScenarios(scenarioFileDTO);

        ScenarioFile scenarioFile = scenarioFileMapper.toEntity(scenarioFileDTO);
        scenarioFile = scenarioFileRepository.save(scenarioFile);
        return scenarioFileMapper.toDto(scenarioFile);
    }

    private void checkInputTypeWithSet(ScenarioFileDTO scenarioFileDTO) {
        if (scenarioFileDTO.getInputType() == null) {
            log.error("InputType must not be null");
            String message = messageSource.getMessage("error.scenarioFile.inputTypeMustNotBeNull", null, Locale.ENGLISH);
            throw new ArchiveManagerGenericException(message);
        }
        if (scenarioFileDTO.getInputType().equals(FileType.CONFIG) && scenarioFileDTO.getDatasetId() != null) {
            log.error("A CONFIG file cannot be linked with a dataSet");
            String message = messageSource.getMessage("error.scenarioFile.configFileCantBeLinkedWithDataSet", null, Locale.ENGLISH);
            throw new ArchiveManagerGenericException(message);
        }
        if (scenarioFileDTO.getInputType().equals(FileType.INPUT) && scenarioFileDTO.getConfigDatasetId() != null) {
            log.error("An INPUT file cannot be linked with a configDataSet");
            String message = messageSource.getMessage("error.scenarioFile.inputFileCantBeLinkedWithConfigDataSet", null, Locale.ENGLISH);
            throw new ArchiveManagerGenericException(message);
        }
    }

    /**
     * check if Scenarios in scenarioFileDTO are valid in database.
     *
     * @param scenarioFileDTO the scenarioFileDTO to check.
     *                        throw status {@code 400 (Bad Request)} if one ScenarioDTO is not valid
     */
    private void checkScenarios(ScenarioFileDTO scenarioFileDTO) {
        List<Long> idScenariosList = new ArrayList<>();
        if (scenarioFileDTO.getScenarios() != null) {
            Set<ScenarioDTO> scenarioSet = scenarioFileDTO.getScenarios();
            for (ScenarioDTO scenarioDTO : scenarioSet) {
                idScenariosList.add(scenarioDTO.getId());
            }
        }
        List<String> wrongIdScenarioList = new ArrayList<>();
        for (Long idScenario : idScenariosList) {
            Optional<ScenarioDTO> scenarioDTOOptional = scenarioService.findOne(idScenario);
            if (!scenarioDTOOptional.isPresent()) {
                wrongIdScenarioList.add(idScenario.toString());
            }
        }

        if (!wrongIdScenarioList.isEmpty()) {
            String message = messageSource.getMessage("error.scenarioFile.scenarioNotValid",
                new Object[]{
                    wrongIdScenarioList.toString(),
                    scenarioFileDTO.getRelativePathInST()
                }, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
    }

    public Long countByOutputFileRelativePathInST(OutputFileDTO outputFileDTO) {
        log.debug("Request to get count of ScenarioFiles by outputFile relativePathInST");
        return scenarioFileQueryService.countByCriteria(scenarioFileCriteriaFromOutputFile(outputFileDTO));
    }

    private ScenarioFileCriteria scenarioFileCriteriaFromOutputFile(OutputFileDTO outputFileDTO) {
        Optional<RunDTO> optionalRunDTO = runService.findOne(outputFileDTO.getRunId());
        if (!optionalRunDTO.isPresent()) {
            String message = messageSource.getMessage("error.run.runNotFound", null, Locale.ENGLISH);
            throw new ArchiveManagerGenericException(message);
        }
        RunDTO runDTO = optionalRunDTO.get();

        Optional<ScenarioDTO> optionalScenarioDTO = scenarioService.findOne(runDTO.getScenarioId());
        if (!optionalScenarioDTO.isPresent()) {
            String message = messageSource.getMessage("error.scenario.scenarioNotFound", null, Locale.ENGLISH);
            throw new ArchiveManagerGenericException(message);
        }
        ScenarioDTO scenarioDTO = optionalScenarioDTO.get();

        LongFilter longFilter = new LongFilter();
        longFilter.setEquals(scenarioDTO.getId());
        ScenarioFileCriteria scenarioFileCriteria = new ScenarioFileCriteria();
        if (scenarioFileCriteria.getScenariosId() != null) {
            String message = messageSource.getMessage("warning.scenarioIdFilterNotWorking", null, Locale.ENGLISH);
            log.warn(message);
        }
        scenarioFileCriteria.setScenariosId(longFilter);
        return scenarioFileCriteria;
    }
}
