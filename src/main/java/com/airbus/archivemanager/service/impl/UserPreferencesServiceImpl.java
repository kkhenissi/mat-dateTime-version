package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.service.UserPreferencesService;
import com.airbus.archivemanager.domain.UserPreferences;
import com.airbus.archivemanager.repository.UserPreferencesRepository;
import com.airbus.archivemanager.service.dto.UserPreferencesDTO;
import com.airbus.archivemanager.service.mapper.UserPreferencesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link UserPreferences}.
 */
@Service
@Transactional
public class UserPreferencesServiceImpl implements UserPreferencesService {

    private final Logger log = LoggerFactory.getLogger(UserPreferencesServiceImpl.class);

    private final UserPreferencesRepository userPreferencesRepository;

    private final UserPreferencesMapper userPreferencesMapper;

    public UserPreferencesServiceImpl(UserPreferencesRepository userPreferencesRepository, UserPreferencesMapper userPreferencesMapper) {
        this.userPreferencesRepository = userPreferencesRepository;
        this.userPreferencesMapper = userPreferencesMapper;
    }

    /**
     * Save a userPreferences.
     *
     * @param userPreferencesDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public UserPreferencesDTO save(UserPreferencesDTO userPreferencesDTO) {
        log.debug("Request to save UserPreferences : {}", userPreferencesDTO);
        UserPreferences userPreferences = userPreferencesMapper.toEntity(userPreferencesDTO);
        userPreferences = userPreferencesRepository.save(userPreferences);
        return userPreferencesMapper.toDto(userPreferences);
    }

    /**
     * Get all the userPreferences.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserPreferencesDTO> findAll() {
        log.debug("Request to get all UserPreferences");
        return userPreferencesRepository.findAll().stream()
            .map(userPreferencesMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one userPreferences by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserPreferencesDTO> findOne(Long id) {
        log.debug("Request to get UserPreferences : {}", id);
        return userPreferencesRepository.findById(id)
            .map(userPreferencesMapper::toDto);
    }

    /**
     * Delete the userPreferences by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserPreferences : {}", id);
        userPreferencesRepository.deleteById(id);
    }
}
