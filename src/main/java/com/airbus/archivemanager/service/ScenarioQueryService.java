package com.airbus.archivemanager.service;

import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.repository.ScenarioRepository;
import com.airbus.archivemanager.service.dto.ScenarioCriteria;
import com.airbus.archivemanager.service.dto.ScenarioDTO;
import com.airbus.archivemanager.service.dto.filter.LocalQueryService;
import com.airbus.archivemanager.service.mapper.ScenarioMapper;
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
 * Service for executing complex queries for {@link Scenario} entities in the database.
 * The main input is a {@link ScenarioCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScenarioDTO} or a {@link Page} of {@link ScenarioDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScenarioQueryService extends LocalQueryService<Scenario> {

    private final Logger log = LoggerFactory.getLogger(ScenarioQueryService.class);

    private final ScenarioRepository scenarioRepository;

    private final ScenarioMapper scenarioMapper;

    public ScenarioQueryService(ScenarioRepository scenarioRepository, ScenarioMapper scenarioMapper) {
        this.scenarioRepository = scenarioRepository;
        this.scenarioMapper = scenarioMapper;
    }

    /**
     * Return a {@link List} of {@link ScenarioDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScenarioDTO> findByCriteria(ScenarioCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Scenario> specification = createSpecification(criteria);
        return scenarioMapper.toDto(scenarioRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ScenarioDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ScenarioDTO> findByCriteria(ScenarioCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Scenario> specification = createSpecification(criteria);
        return scenarioRepository.findAll(specification, page)
            .map(scenarioMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScenarioCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Scenario> specification = createSpecification(criteria);
        return scenarioRepository.count(specification);
    }

    /**
     * Function to convert {@link ScenarioCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Scenario> createSpecification(ScenarioCriteria criteria) {
        Specification<Scenario> specification = Specification.where(null);
        if (criteria == null) {
            return specification;
        }
        if (criteria.getId() != null) {
            specification = specification.and(buildSpecification(criteria.getId(), Scenario_.id));
        }
        if (criteria.getName() != null) {
            specification = specification.and(buildStringSpecification(criteria.getName(), Scenario_.name));
        }
        if (criteria.getCreationDate() != null) {
            specification = specification.and(buildLocalRangeSpecification(criteria.getCreationDate(), Scenario_.creationDate));
        }
        if (criteria.getSimulationMode() != null) {
            specification = specification.and(buildLocalSpecification(criteria.getSimulationMode(), Scenario_.simulationMode));
        }
        if (criteria.getStartSimulatedDate() != null) {
            specification = specification.and(buildLocalRangeSpecification(criteria.getStartSimulatedDate(), Scenario_.startSimulatedDate));
        }
        if (criteria.getEndSimulatedDate() != null) {
            specification = specification.and(buildLocalRangeSpecification(criteria.getEndSimulatedDate(), Scenario_.simulation));
        }
        if (criteria.getDescription() != null) {
            specification = specification.and(buildStringSpecification(criteria.getDescription(), Scenario_.description));
        }
        if (criteria.getRunsId() != null) {
            specification = specification.and(buildSpecification(criteria.getRunsId(),
                root -> root.join(Scenario_.runs, JoinType.LEFT).get(Run_.id)));
        }
        if (criteria.getOwnerId() != null) {
            specification = specification.and(buildSpecification(criteria.getOwnerId(),
                root -> root.join(Scenario_.owner, JoinType.LEFT).get(User_.id)));
        }
        if (criteria.getScenarioFilesRelativePathInST() != null) {
            specification = specification.and(buildSpecification(criteria.getScenarioFilesRelativePathInST(),
                root -> root.join(Scenario_.scenarioFiles, JoinType.LEFT).get(ScenarioFile_.relativePathInST)));
        }

        return specification;
    }
}
