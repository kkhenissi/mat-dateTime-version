package com.airbus.archivemanager.service;

import com.airbus.archivemanager.service.dto.ToolVersionDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.airbus.archivemanager.domain.ToolVersion}.
 */
public interface ToolVersionService {

    /**
     * Save a toolVersion.
     *
     * @param toolVersionDTO the entity to save.
     * @return the persisted entity.
     */
    ToolVersionDTO save(ToolVersionDTO toolVersionDTO);

    /**
     * Get all the toolVersions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ToolVersionDTO> findAll(Pageable pageable);


    /**
     * Get the "id" toolVersion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ToolVersionDTO> findOne(Long id);

    /**
     * Delete the "id" toolVersion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
