package com.airbus.archivemanager.service;

import com.airbus.archivemanager.service.dto.UploadDTO;

import java.util.List;
import java.util.Optional;

public interface UploadService {

    /**
     * Post an upload.
     *
     * @param uploadDTO contains the relative path in LTS and local path.
     * @return the entity saved
     */
    UploadDTO uploadFile(UploadDTO uploadDTO);

    /**
     * Get all the uploads.
     *
     * @return the list of entities.
     */
    List<UploadDTO> findAll();


    /**
     * Get the "id" upload.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UploadDTO> findOne(Long id);

    /**
     * Delete the "id" upload.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
