package com.airbus.archivemanager.service;

import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.service.dto.ScenarioDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.airbus.archivemanager.domain.Scenario}.
 */
public interface ScenarioService {

    /**
     * Save a scenario.
     *
     * @param scenarioDTO the entity to save.
     * @return the persisted entity.
     */
    ScenarioDTO save(ScenarioDTO scenarioDTO);

    /**
     * Get all the scenarios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ScenarioDTO> findAll(Pageable pageable);


    /**
     * Get the "id" scenario.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScenarioDTO> findOne(Long id);

    /**
     * Delete the "id" scenario.
     *
     * Only admin can delete a scenario.
     * @param id the id of the entity.
     */
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    void delete(Long id);

    /**
     * Update a scenario.
     *
     * @param scenarioDTO the entity to update.
     * @return the persisted entity.
     */
    ScenarioDTO update(ScenarioDTO scenarioDTO);
}
