package com.airbus.archivemanager.service;

import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.repository.ScenarioFileRepository;
import com.airbus.archivemanager.service.dto.ScenarioFileCriteria;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;
import com.airbus.archivemanager.service.dto.filter.LocalQueryService;
import com.airbus.archivemanager.service.mapper.ScenarioFileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Service for executing complex queries for {@link ScenarioFile} entities in the database.
 * The main input is a {@link ScenarioFileCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScenarioFileDTO} or a {@link Page} of {@link ScenarioFileDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScenarioFileQueryService extends LocalQueryService<ScenarioFile> {

    //ROOT is the root path in STS
    private static final String ROOT = "C:/Users/Nicolas.RAOUX/projets/testUpload/inputs/";

    private final Logger log = LoggerFactory.getLogger(ScenarioFileQueryService.class);

    private final ScenarioFileRepository scenarioFileRepository;

    private final ScenarioFileMapper scenarioFileMapper;

    private final MessageSource messageSource;

    public ScenarioFileQueryService(ScenarioFileRepository scenarioFileRepository, ScenarioFileMapper scenarioFileMapper, MessageSource messageSource) {
        this.scenarioFileRepository = scenarioFileRepository;
        this.scenarioFileMapper = scenarioFileMapper;
        this.messageSource = messageSource;
    }

    /**
     * Return a {@link List} of {@link ScenarioFileDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScenarioFileDTO> findByCriteria(ScenarioFileCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ScenarioFile> specification = createSpecification(criteria);
        return scenarioFileMapper.toDto(scenarioFileRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ScenarioFileDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     *                 If some files already exist in STS then a warning information is stored in a log and files will be overwritten.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ScenarioFileDTO> findByCriteria(ScenarioFileCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ScenarioFile> specification = createSpecification(criteria);
        Page<ScenarioFileDTO> scenarioFileDTOPage = scenarioFileRepository.findAll(specification, page)
            .map(scenarioFileMapper::toDto);
        if (criteria.getRelativePathInST() == null) {
            return scenarioFileDTOPage;
        }
        List<ScenarioFileDTO> scenarioFileDTOList = scenarioFileDTOPage.getContent();
        for (ScenarioFileDTO scenarioFileDTO : scenarioFileDTOList) {
            File temp = new File(ROOT + scenarioFileDTO.getRelativePathInST());
            if (temp.exists()) {
                String[] variableArray = new String[]{temp.getAbsolutePath()};
                String message = messageSource.getMessage("warning.pathAlreadyExists", variableArray, Locale.ENGLISH);
                log.warn(message);
            }
        }
        return scenarioFileDTOPage;
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScenarioFileCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ScenarioFile> specification = createSpecification(criteria);
        return scenarioFileRepository.count(specification);
    }

    /**
     * Function to convert {@link ScenarioFileCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ScenarioFile> createSpecification(ScenarioFileCriteria criteria) {
        Specification<ScenarioFile> specification = Specification.where(null);
        if (criteria == null) {
            return specification;
        }
        if (criteria.getInputType() != null) {
            specification = specification.and(buildLocalSpecification(criteria.getInputType(), ScenarioFile_.inputType));
        }
        if (criteria.getRelativePathInST() != null) {
            specification = specification.and(buildStringSpecification(criteria.getRelativePathInST(), ScenarioFile_.relativePathInST));
        }
        if (criteria.getlTInsertionDate() != null) {
            specification = specification.and(buildLocalRangeSpecification(criteria.getlTInsertionDate(), ScenarioFile_.lTInsertionDate));
        }
        if (criteria.getPathInLT() != null) {
            specification = specification.and(buildStringSpecification(criteria.getPathInLT(), ScenarioFile_.pathInLT));
        }
        if (criteria.getFileType() != null) {
            specification = specification.and(buildStringSpecification(criteria.getFileType(), ScenarioFile_.fileType));
        }
        if (criteria.getFormat() != null) {
            specification = specification.and(buildStringSpecification(criteria.getFormat(), ScenarioFile_.format));
        }
        if (criteria.getSubSystemAtOriginOfData() != null) {
            specification = specification.and(buildStringSpecification(criteria.getSubSystemAtOriginOfData(), ScenarioFile_.subSystemAtOriginOfData));
        }
        if (criteria.getTimeOfData() != null) {
            specification = specification.and(buildLocalRangeSpecification(criteria.getTimeOfData(), ScenarioFile_.timeOfData));
        }
        if (criteria.getSecurityLevel() != null) {
            specification = specification.and(buildLocalSpecification(criteria.getSecurityLevel(), ScenarioFile_.securityLevel));
        }
        if (criteria.getCrc() != null) {
            specification = specification.and(buildStringSpecification(criteria.getCrc(), ScenarioFile_.crc));
        }
        if (criteria.getOwnerId() != null) {
            specification = specification.and(buildSpecification(criteria.getOwnerId(),
                root -> root.join(ScenarioFile_.owner, JoinType.LEFT).get(User_.id)));
        }
        if (criteria.getScenariosId() != null) {
            specification = specification.and(buildSpecification(criteria.getScenariosId(),
                root -> root.join(ScenarioFile_.scenarios, JoinType.LEFT).get(Scenario_.id)));
        }
        if (criteria.getDatasetId() != null) {
            specification = specification.and(buildSpecification(criteria.getDatasetId(),
                root -> root.join(ScenarioFile_.dataset, JoinType.LEFT).get(DataSet_.id)));
        }
        if (criteria.getConfigDatasetId() != null) {
            specification = specification.and(buildSpecification(criteria.getConfigDatasetId(),
                root -> root.join(ScenarioFile_.configDataset, JoinType.LEFT).get(ConfigDataSet_.id)));
        }
        return specification;
    }
}
