package com.airbus.archivemanager.service;

import com.airbus.archivemanager.service.dto.ConfigDataSetDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.airbus.archivemanager.domain.ConfigDataSet}.
 */
public interface ConfigDataSetService {

    /**
     * Save a configDataSet.
     *
     * @param configDataSetDTO the entity to save.
     * @return the persisted entity.
     */
    ConfigDataSetDTO save(ConfigDataSetDTO configDataSetDTO);

    /**
     * Get all the configDataSets.
     *
     * @return the list of entities.
     */
    List<ConfigDataSetDTO> findAll();


    /**
     * Get the "id" configDataSet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ConfigDataSetDTO> findOne(Long id);

    /**
     * Delete the "id" configDataSet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
