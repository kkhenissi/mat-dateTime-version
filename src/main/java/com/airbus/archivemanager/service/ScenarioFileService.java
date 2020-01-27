package com.airbus.archivemanager.service;

import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.airbus.archivemanager.domain.ScenarioFile}.
 */
public interface ScenarioFileService {

    /**
     * Save a scenarioFile.
     *
     * @param scenarioFileDTO the entity to save.
     * @return the persisted entity.
     */
    ScenarioFileDTO save(ScenarioFileDTO scenarioFileDTO);

    /**
     * Get all the scenarioFiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ScenarioFileDTO> findAll(Pageable pageable);

    /**
     * Get all the scenarioFiles with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ScenarioFileDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "relativePathInST" scenarioFile.
     *
     * @param relativePathInST the id of the entity.
     * @return the entity.
     */
    Optional<ScenarioFileDTO> findOne(String relativePathInST);

    /**
     * Delete the scenarioFile.
     * <p>
     * Only admin can delete an outputFile.
     *
     * @param scenarioFileDTO the entity.
     */
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    void delete(ScenarioFileDTO scenarioFileDTO);

    /**
     * Get all the scenarioFiles with same outputFile.
     *
     * @param outputFileDTO the relative path in STS of the outputFile.
     * @param pageable      the pagination information.
     * @return the list of entities.
     */
    Page<ScenarioFileDTO> findByOutputFileRelativePathInST(OutputFileDTO outputFileDTO, Pageable pageable);

    /**
     * Update a scenarioFile.
     *
     * @param scenarioFileDTO the entity to update.
     * @return the persisted entity.
     */
    ScenarioFileDTO update(ScenarioFileDTO scenarioFileDTO);

    Long countByOutputFileRelativePathInST(OutputFileDTO outputFileDTO);
}
