package com.airbus.archivemanager.service;

import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.repository.RunRepository;
import com.airbus.archivemanager.service.dto.RunCriteria;
import com.airbus.archivemanager.service.dto.RunDTO;
import com.airbus.archivemanager.service.dto.filter.LocalQueryService;
import com.airbus.archivemanager.service.mapper.RunMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.util.List;

/**
 * Service for executing complex queries for {@link Run} entities in the database.
 * The main input is a {@link RunCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RunDTO} or a {@link Page} of {@link RunDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RunQueryService extends LocalQueryService<Run> {

    private final Logger log = LoggerFactory.getLogger(RunQueryService.class);

    private final RunRepository runRepository;

    private final RunMapper runMapper;

    public RunQueryService(RunRepository runRepository, RunMapper runMapper) {
        this.runRepository = runRepository;
        this.runMapper = runMapper;
    }

    /**
     * Return a {@link List} of {@link RunDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RunDTO> findByCriteria(RunCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Run> specification = createSpecification(criteria);
        return runMapper.toDto(runRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RunDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RunDTO> findByCriteria(RunCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Run> specification = createSpecification(criteria);
        return runRepository.findAll(specification, page)
            .map(runMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RunCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Run> specification = createSpecification(criteria);
        return runRepository.count(specification);
    }

    /**
     * Function to convert {@link RunCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Run> createSpecification(RunCriteria criteria) {
        Specification<Run> specification = Specification.where(null);
        if (criteria == null) {
            return specification;
        }
        if (criteria.getId() != null) {
            specification = specification.and(buildSpecification(criteria.getId(), Run_.id));
        }
        if (criteria.getStartDate() != null) {
            specification = specification.and(buildLocalRangeSpecification(criteria.getStartDate(), Run_.startDate));
        }
        if (criteria.getEndDate() != null) {
            specification = specification.and(buildLocalRangeSpecification(criteria.getEndDate(), Run_.endDate));
        }
        if (criteria.getStatus() != null) {
            specification = specification.and(buildSpecification(criteria.getStatus(), Run_.status));
        }
        if (criteria.getPlatformHardwareInfo() != null) {
            specification = specification.and(buildStringSpecification(criteria.getPlatformHardwareInfo(), Run_.platformHardwareInfo));
        }
        if (criteria.getDescription() != null) {
            specification = specification.and(buildStringSpecification(criteria.getDescription(), Run_.description));
        }
            /*if (criteria.getOutputFilesRelativePathInST() != null) {
            specification = specification.and(buildSpecification(criteria.getOutputFilesRelativePathInST(),
            root -> root.join(Run_.outputFiles, JoinType.LEFT).get(OutputFile_.relativePathInST)));
            }*/
        if (criteria.getToolVersionsId() != null) {
            specification = specification.and(buildSpecification(criteria.getToolVersionsId(),
                root -> root.join(Run_.toolVersions, JoinType.LEFT).get(ToolVersion_.id)));
        }
        if (criteria.getOwnerId() != null) {
            specification = specification.and(buildSpecification(criteria.getOwnerId(),
                root -> root.join(Run_.owner, JoinType.LEFT).get(User_.id)));
        }
        if (criteria.getScenarioId() != null) {
            specification = specification.and(buildSpecification(criteria.getScenarioId(),
                root -> root.join(Run_.scenario, JoinType.LEFT).get(Scenario_.id)));
        }

        return specification;
    }
}
