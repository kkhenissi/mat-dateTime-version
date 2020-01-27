package com.airbus.archivemanager.service;


import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.repository.OutputFileRepository;
import com.airbus.archivemanager.service.dto.OutputFileCriteria;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.dto.filter.LocalQueryService;
import com.airbus.archivemanager.service.mapper.OutputFileMapper;
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
 * Service for executing complex queries for {@link OutputFile} entities in the database.
 * The main input is a {@link OutputFileCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OutputFileDTO} or a {@link Page} of {@link OutputFileDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OutputFileQueryService extends LocalQueryService<OutputFile> {

    private final Logger log = LoggerFactory.getLogger(OutputFileQueryService.class);

    private final OutputFileRepository outputFileRepository;

    private final OutputFileMapper outputFileMapper;

    public OutputFileQueryService(OutputFileRepository outputFileRepository, OutputFileMapper outputFileMapper) {
        this.outputFileRepository = outputFileRepository;
        this.outputFileMapper = outputFileMapper;
    }

    /**
     * Return a {@link List} of {@link OutputFileDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OutputFileDTO> findByCriteria(OutputFileCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OutputFile> specification = createSpecification(criteria);
        return outputFileMapper.toDto(outputFileRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OutputFileDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OutputFileDTO> findByCriteria(OutputFileCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OutputFile> specification = createSpecification(criteria);
        Page<OutputFileDTO> outputFileDTOPage = outputFileRepository.findAll(specification, page)
            .map(outputFileMapper::toDto);
        if (criteria.getRelativePathInST() == null) {
            return outputFileDTOPage;
        }
        return outputFileDTOPage;
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OutputFileCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OutputFile> specification = createSpecification(criteria);
        return outputFileRepository.count(specification);
    }

    /**
     * Function to convert {@link OutputFileCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OutputFile> createSpecification(OutputFileCriteria criteria) {
        Specification<OutputFile> specification = Specification.where(null);
        if (criteria == null) {
            return specification;
        }
        if (criteria.getRelativePathInST() != null) {
            specification = specification.and(buildStringSpecification(criteria.getRelativePathInST(), OutputFile_.relativePathInST));
        }
        if (criteria.getlTInsertionDate() != null) {
            specification = specification.and(buildLocalRangeSpecification(criteria.getlTInsertionDate(), OutputFile_.lTInsertionDate));
        }
        if (criteria.getPathInLT() != null) {
            specification = specification.and(buildStringSpecification(criteria.getPathInLT(), OutputFile_.pathInLT));
        }
        if (criteria.getFileType() != null) {
            specification = specification.and(buildStringSpecification(criteria.getFileType(), OutputFile_.fileType));
        }
        if (criteria.getFormat() != null) {
            specification = specification.and(buildStringSpecification(criteria.getFormat(), OutputFile_.format));
        }
        if (criteria.getSubSystemAtOriginOfData() != null) {
            specification = specification.and(buildStringSpecification(criteria.getSubSystemAtOriginOfData(), OutputFile_.subSystemAtOriginOfData));
        }
        if (criteria.getSecurityLevel() != null) {
            specification = specification.and(buildLocalSpecification(criteria.getSecurityLevel(), OutputFile_.securityLevel));
        }
        if (criteria.getCrc() != null) {
            specification = specification.and(buildStringSpecification(criteria.getCrc(), OutputFile_.crc));
        }
        if (criteria.getOwnerId() != null) {
            specification = specification.and(buildSpecification(criteria.getOwnerId(),
                root -> root.join(OutputFile_.owner, JoinType.LEFT).get(User_.id)));
        }
        if (criteria.getRunId() != null) {
            specification = specification.and(buildSpecification(criteria.getRunId(),
                root -> root.join(OutputFile_.run, JoinType.LEFT).get(Run_.id)));
        }

        return specification;
    }
}
