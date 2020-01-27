package com.airbus.archivemanager.service;

import com.airbus.archivemanager.service.dto.UserPreferencesDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.airbus.archivemanager.domain.UserPreferences}.
 */
public interface UserPreferencesService {

    /**
     * Save a userPreferences.
     *
     * @param userPreferencesDTO the entity to save.
     * @return the persisted entity.
     */
    UserPreferencesDTO save(UserPreferencesDTO userPreferencesDTO);

    /**
     * Get all the userPreferences.
     *
     * @return the list of entities.
     */
    List<UserPreferencesDTO> findAll();


    /**
     * Get the "id" userPreferences.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserPreferencesDTO> findOne(Long id);

    /**
     * Delete the "id" userPreferences.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
