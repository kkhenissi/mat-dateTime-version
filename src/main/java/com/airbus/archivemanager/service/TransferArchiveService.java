package com.airbus.archivemanager.service;

import java.util.Optional;

import com.airbus.archivemanager.domain.TransferArchive;
import com.airbus.archivemanager.service.dto.TransferArchiveDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Service Interface for managing {@link com.airbus.archivemanager.domain.TransferArchive}.
 */
public interface TransferArchiveService {

    /**
     * Save a transfer.
     *
     * @param transferArchiveDTO the entity to save.
     * @return the persisted entity.
     */
    TransferArchiveDTO save(TransferArchiveDTO transferArchiveDTO);
    
   /**
     * Update a status transfer.
     *
     * @param transferArchive the entity to save.
     * @return the persisted entity.
     */
    TransferArchive updateStatus(TransferArchive transferArchive);

    /**
     * Update the successfully_transfered.
     *
     * @param transferArchive the entity to save.
     * @return the persisted entity.
     */
    TransferArchive incrementSuccess(TransferArchive transferArchive);

     /**
     * Update the  failed_transfered.
     *
     * @param transferArchive the entity to save.
     * @return the persisted entity.
     */
    TransferArchive incrementFailed(TransferArchive transferArchive);

    /**
     * Get all the transfers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransferArchiveDTO> findAll(Pageable pageable);


    /**
     * Get the "id" transfer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransferArchiveDTO> findOne(Long id);

    /**
     * Delete the "id" transfer.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
