package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.domain.Scenario;
import com.airbus.archivemanager.domain.User;
import com.airbus.archivemanager.repository.ScenarioRepository;
import com.airbus.archivemanager.service.ScenarioService;
import com.airbus.archivemanager.service.UserService;
import com.airbus.archivemanager.service.dto.ScenarioDTO;
import com.airbus.archivemanager.service.mapper.ScenarioMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Scenario}.
 */
@Service
@Transactional
public class ScenarioServiceImpl implements ScenarioService {

    private final Logger log = LoggerFactory.getLogger(ScenarioServiceImpl.class);

    private final ScenarioRepository scenarioRepository;

    private final ScenarioMapper scenarioMapper;

    private final UserService userService;


    public ScenarioServiceImpl(ScenarioRepository scenarioRepository, ScenarioMapper scenarioMapper, UserService userService) {
        this.scenarioRepository = scenarioRepository;
        this.scenarioMapper = scenarioMapper;
        this.userService = userService;
    }

    /**
     * Save a scenario.
     *
     * @param scenarioDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ScenarioDTO save(ScenarioDTO scenarioDTO) {
        log.debug("Request to save Scenario : {}", scenarioDTO);
        User owner = userService.getOwner();
        scenarioDTO.setOwnerId(owner.getId());
        if (scenarioDTO.getCreationDate() == null) {
            scenarioDTO.setCreationDate(LocalDateTime.now());
        }
        Scenario scenario = scenarioMapper.toEntity(scenarioDTO);
        scenario = scenarioRepository.save(scenario);
        return scenarioMapper.toDto(scenario);
    }

    /**
     * Get all the scenarios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ScenarioDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Scenarios");
        return scenarioRepository.findAll(pageable)
            .map(scenarioMapper::toDto);
    }


    /**
     * Get one scenario by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ScenarioDTO> findOne(Long id) {
        log.debug("Request to get Scenario : {}", id);
        return scenarioRepository.findById(id)
            .map(scenarioMapper::toDto);
    }

    /**
     * Delete the scenario by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Scenario : {}", id);
        scenarioRepository.deleteById(id);
    }

    /**
     * Update a scenario.
     *
     * @param scenarioDTO the entity to update.
     * @return the persisted entity.
     */
    @Override
    public ScenarioDTO update(ScenarioDTO scenarioDTO) {
        log.debug("Request to update Scenario : {}", scenarioDTO);
        Scenario scenario = scenarioMapper.toEntity(scenarioDTO);
        scenario = scenarioRepository.save(scenario);
        return scenarioMapper.toDto(scenario);
    }
}
