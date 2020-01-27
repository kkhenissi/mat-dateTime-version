package com.airbus.archivemanager.service;

import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.repository.DataSetRepository;
import com.airbus.archivemanager.service.dto.DataSetCriteria;
import com.airbus.archivemanager.service.dto.DataSetDTO;
import com.airbus.archivemanager.service.dto.filter.LocalQueryService;
import com.airbus.archivemanager.service.mapper.DataSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for executing complex queries for {@link DataSet} entities in the database.
 * The main input is a {@link DataSetCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DataSetDTO} or a {@link Page} of {@link DataSetDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DataSetQueryService extends LocalQueryService<DataSet> {

    private final Logger log = LoggerFactory.getLogger(DataSetQueryService.class);

    private final DataSetRepository dataSetRepository;

    private final DataSetMapper dataSetMapper;

    public DataSetQueryService(DataSetRepository dataSetRepository, DataSetMapper dataSetMapper) {
        this.dataSetRepository = dataSetRepository;
        this.dataSetMapper = dataSetMapper;
    }

    /**
     * Return a {@link List} of {@link DataSetDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DataSetDTO> findByCriteria(DataSetCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<DataSet> specification = createSpecification(criteria);
        return dataSetMapper.toDto(dataSetRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DataSetDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DataSetDTO> findByCriteria(DataSetCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DataSet> specification = createSpecification(criteria);
        return dataSetRepository.findAll(specification, page)
            .map(dataSetMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DataSetCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<DataSet> specification = createSpecification(criteria);
        return dataSetRepository.count(specification);
    }

    /**
     * Function to convert {@link DataSetCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DataSet> createSpecification(DataSetCriteria criteria) {
        Specification<DataSet> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), DataSet_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), DataSet_.name));
            }
        }
        return specification;
    }
}
