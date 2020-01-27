package com.airbus.archivemanager.service;


import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.repository.UploadRepository;
import com.airbus.archivemanager.service.dto.UploadCriteria;
import com.airbus.archivemanager.service.dto.UploadDTO;
import com.airbus.archivemanager.service.dto.filter.LocalQueryService;
import com.airbus.archivemanager.service.mapper.UploadMapper;
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
 * Service for executing complex queries for {@link Upload} entities in the database.
 * The main input is a {@link UploadCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UploadDTO} or a {@link Page} of {@link UploadDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UploadQueryService extends LocalQueryService<Upload> {

    private final Logger log = LoggerFactory.getLogger(UploadQueryService.class);

    private final UploadRepository uploadRepository;

    private final UploadMapper uploadMapper;

    public UploadQueryService(UploadRepository uploadRepository, UploadMapper uploadMapper) {
        this.uploadRepository = uploadRepository;
        this.uploadMapper = uploadMapper;
    }

    /**
     * Return a {@link List} of {@link UploadDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UploadDTO> findByCriteria(UploadCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Upload> specification = createSpecification(criteria);
        return uploadMapper.toDto(uploadRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UploadDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UploadDTO> findByCriteria(UploadCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Upload> specification = createSpecification(criteria);
        return uploadRepository.findAll(specification, page)
            .map(uploadMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UploadCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Upload> specification = createSpecification(criteria);
        return uploadRepository.count(specification);
    }

    /**
     * Function to convert {@link UploadCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Upload> createSpecification(UploadCriteria criteria) {
        Specification<Upload> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Upload_.id));
            }
            if (criteria.getRelativePathInST() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRelativePathInST(), Upload_.relativePathInST));
            }
            if (criteria.getPathInLT() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPathInLT(), Upload_.lTSPath));
            }
            if (criteria.getlTInsertionDate() != null) {
                specification = specification.and(buildLocalRangeSpecification(criteria.getlTInsertionDate(), Upload_.lTInsertionDate));
            }
            if (criteria.getInputType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getInputType(), Upload_.inputType));
            }
            if (criteria.getOwnerId() != null) {
                specification = specification.and(buildSpecification(criteria.getOwnerId(),
                    root -> root.join(Upload_.owner, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
