package com.airbus.archivemanager.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.airbus.archivemanager.domain.ToolVersion;
import com.airbus.archivemanager.domain.*; // for static metamodels
import com.airbus.archivemanager.repository.ToolVersionRepository;
import com.airbus.archivemanager.service.dto.ToolVersionCriteria;
import com.airbus.archivemanager.service.dto.ToolVersionDTO;
import com.airbus.archivemanager.service.mapper.ToolVersionMapper;

/**
 * Service for executing complex queries for {@link ToolVersion} entities in the database.
 * The main input is a {@link ToolVersionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ToolVersionDTO} or a {@link Page} of {@link ToolVersionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ToolVersionQueryService extends QueryService<ToolVersion> {

    private final Logger log = LoggerFactory.getLogger(ToolVersionQueryService.class);

    private final ToolVersionRepository toolVersionRepository;

    private final ToolVersionMapper toolVersionMapper;

    public ToolVersionQueryService(ToolVersionRepository toolVersionRepository, ToolVersionMapper toolVersionMapper) {
        this.toolVersionRepository = toolVersionRepository;
        this.toolVersionMapper = toolVersionMapper;
    }

    /**
     * Return a {@link List} of {@link ToolVersionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ToolVersionDTO> findByCriteria(ToolVersionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ToolVersion> specification = createSpecification(criteria);
        return toolVersionMapper.toDto(toolVersionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ToolVersionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ToolVersionDTO> findByCriteria(ToolVersionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ToolVersion> specification = createSpecification(criteria);
        return toolVersionRepository.findAll(specification, page)
            .map(toolVersionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ToolVersionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ToolVersion> specification = createSpecification(criteria);
        return toolVersionRepository.count(specification);
    }

    /**
     * Function to convert {@link ToolVersionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ToolVersion> createSpecification(ToolVersionCriteria criteria) {
        Specification<ToolVersion> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ToolVersion_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), ToolVersion_.name));
            }
            if (criteria.getVersion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVersion(), ToolVersion_.version));
            }
            if (criteria.getRunId() != null) {
                specification = specification.and(buildSpecification(criteria.getRunId(),
                    root -> root.join(ToolVersion_.runs, JoinType.LEFT).get(Run_.id)));
            }
        }
        return specification;
    }
}
