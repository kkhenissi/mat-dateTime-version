package com.airbus.archivemanager.service;

import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.airbus.archivemanager.domain.OutputFile}.
 */
public interface OutputFileService {

    /**
     * Save a outputFile.
     *
     * @param outputFileDTO the entity to save.
     * @return the persisted entity.
     */
    OutputFileDTO save(OutputFileDTO outputFileDTO);

    /**
     * Get all the outputFiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OutputFileDTO> findAll(Pageable pageable);


    /**
     * Get the "relativePathInST" outputFile.
     *
     * @param relativePathInST the id of the entity.
     * @return the entity.
     */
    Optional<OutputFileDTO> findOne(String relativePathInST);

    /**
     * Delete the "relativePathInST" outputFile.
     *
     * Only admin can delete an outputFile.
     * @param outputFileDTO the entity to delete.
     */
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    void delete(OutputFileDTO outputFileDTO);

    /**
     * Update a outputFile.
     *
     * @param outputFileDTO the entity to update.
     * @return the persisted entity.
     */
    OutputFileDTO update(OutputFileDTO outputFileDTO);
}
