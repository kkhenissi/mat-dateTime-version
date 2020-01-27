package com.airbus.archivemanager.service;

import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.service.dto.RunDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.airbus.archivemanager.domain.Run}.
 */
public interface RunService {

    /**
     * Save a run.
     *
     * @param runDTO the entity to save.
     * @return the persisted entity.
     */
    RunDTO save(RunDTO runDTO);

    /**
     * Get all the runs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RunDTO> findAll(Pageable pageable);


    /**
     * Get the "id" run.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RunDTO> findOne(Long id);

    /**
     * Delete the "id" run.
     *
     * Only admin can delete a run.
     * @param id the id of the entity.
     */
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    void delete(Long id);

    /**
     * Update a run.
     *
     * @param runDTO the entity to update.
     * @return the persisted entity.
     */
    RunDTO update(RunDTO runDTO);
}
