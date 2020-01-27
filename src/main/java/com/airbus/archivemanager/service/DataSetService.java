package com.airbus.archivemanager.service;

import com.airbus.archivemanager.service.dto.DataSetDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.airbus.archivemanager.domain.DataSet}.
 */
public interface DataSetService {

    /**
     * Save a dataSet.
     *
     * @param dataSetDTO the entity to save.
     * @return the persisted entity.
     */
    DataSetDTO save(DataSetDTO dataSetDTO);

    /**
     * Get all the dataSets.
     *
     * @return the list of entities.
     */
    List<DataSetDTO> findAll();


    /**
     * Get the "id" dataSet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DataSetDTO> findOne(Long id);

    /**
     * Delete the "id" dataSet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
