package com.airbus.archivemanager.service;

import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.repository.ConfigDataSetRepository;
import com.airbus.archivemanager.service.dto.ConfigDataSetCriteria;
import com.airbus.archivemanager.service.dto.ConfigDataSetDTO;
import com.airbus.archivemanager.service.dto.filter.LocalQueryService;
import com.airbus.archivemanager.service.mapper.ConfigDataSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for executing complex queries for {@link ConfigDataSet} entities in the database.
 * The main input is a {@link ConfigDataSetCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ConfigDataSetDTO} or a {@link Page} of {@link ConfigDataSetDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ConfigDataSetQueryService extends LocalQueryService<ConfigDataSet> {

    private final Logger log = LoggerFactory.getLogger(ConfigDataSetQueryService.class);

    private final ConfigDataSetRepository configDataSetRepository;

    private final ConfigDataSetMapper configDataSetMapper;

    public ConfigDataSetQueryService(ConfigDataSetRepository configDataSetRepository, ConfigDataSetMapper configDataSetMapper) {
        this.configDataSetRepository = configDataSetRepository;
        this.configDataSetMapper = configDataSetMapper;
    }

    /**
     * Return a {@link List} of {@link ConfigDataSetDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ConfigDataSetDTO> findByCriteria(ConfigDataSetCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ConfigDataSet> specification = createSpecification(criteria);
        return configDataSetMapper.toDto(configDataSetRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ConfigDataSetDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ConfigDataSetDTO> findByCriteria(ConfigDataSetCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ConfigDataSet> specification = createSpecification(criteria);
        return configDataSetRepository.findAll(specification, page)
            .map(configDataSetMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ConfigDataSetCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ConfigDataSet> specification = createSpecification(criteria);
        return configDataSetRepository.count(specification);
    }

    /**
     * Function to convert {@link ConfigDataSetCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ConfigDataSet> createSpecification(ConfigDataSetCriteria criteria) {
        Specification<ConfigDataSet> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ConfigDataSet_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), ConfigDataSet_.name));
            }
        }
        return specification;
    }
}
