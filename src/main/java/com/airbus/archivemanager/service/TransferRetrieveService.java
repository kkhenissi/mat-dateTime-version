package com.airbus.archivemanager.service;

import java.util.Optional;

import com.airbus.archivemanager.domain.TransferRetrieve;
import com.airbus.archivemanager.service.dto.TransferRetrieveDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.airbus.archivemanager.domain.TransferRetrieve}.
 */
public interface TransferRetrieveService {

    /**
     * Save a transfer.
     *
     * @param transferRetrieveDTO the entity to save.
     * @return the persisted entity.
     */
    TransferRetrieveDTO save(TransferRetrieveDTO transferRetrieveDTO);
    
    /**
     * Update a status transfer.
     *
     * @param transferRetrieve the entity to save.
     * @return the persisted entity.
     */
    TransferRetrieve updateStatus(TransferRetrieve transferRetrieve);

    /**
     * Update the successfully_transfered.
     *
     * @param transferRetrieve the entity to save.
     * @return the persisted entity.
     */
    TransferRetrieve incrementSuccess(TransferRetrieve transferRetrieve);

     /**
     * Update the  failed_transfered.
     *
     * @param transferRetrieve the entity to save.
     * @return the persisted entity.
     */
    TransferRetrieve incrementFailed(TransferRetrieve transferRetrieve);


    /**
     * Get all the transfers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransferRetrieveDTO> findAll(Pageable pageable);


    /**
     * Get the "id" transfer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransferRetrieveDTO> findOne(Long id);

    /**
     * Delete the "id" transfer.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
