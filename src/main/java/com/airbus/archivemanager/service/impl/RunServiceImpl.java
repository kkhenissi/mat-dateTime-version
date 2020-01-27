package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.domain.Run;
import com.airbus.archivemanager.domain.User;
import com.airbus.archivemanager.repository.RunRepository;
import com.airbus.archivemanager.service.RunService;
import com.airbus.archivemanager.service.UserService;
import com.airbus.archivemanager.service.dto.RunDTO;
import com.airbus.archivemanager.service.mapper.RunMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Run}.
 */
@Service
@Transactional
public class RunServiceImpl implements RunService {

    private final Logger log = LoggerFactory.getLogger(RunServiceImpl.class);

    private final RunRepository runRepository;

    private final RunMapper runMapper;

    private final UserService userService;

    public RunServiceImpl(RunRepository runRepository, RunMapper runMapper, UserService userService) {
        this.runRepository = runRepository;
        this.runMapper = runMapper;
        this.userService = userService;
    }

    /**
     * Save a run.
     *
     * @param runDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public RunDTO save(RunDTO runDTO) {
        log.debug("Request to save Run : {}", runDTO);
        User owner = userService.getOwner();
        runDTO.setOwnerId(owner.getId());
        Run run = runMapper.toEntity(runDTO);
        run = runRepository.save(run);
        return runMapper.toDto(run);
    }

    /**
     * Get all the runs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RunDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Runs");
        return runRepository.findAll(pageable)
            .map(runMapper::toDto);
    }

    /**
     * Get all the runs with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RunDTO> findAllWithEagerRelationships(Pageable pageable) {
        return runRepository.findAllWithEagerRelationships(pageable).map(runMapper::toDto);
    }


    /**
     * Get one run by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RunDTO> findOne(Long id) {
        log.debug("Request to get Run : {}", id);
        return runRepository.findById(id)
            .map(runMapper::toDto);
    }

    /**
     * Delete the run by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Run : {}", id);
        runRepository.deleteById(id);
    }

    /**
     * Update a run.
     *
     * @param runDTO the entity to update.
     * @return the persisted entity.
     */
    @Override
    public RunDTO update(RunDTO runDTO) {
        log.debug("Request to update Run : {}", runDTO);
        Run run = runMapper.toEntity(runDTO);
        run = runRepository.save(run);
        return runMapper.toDto(run);
    }
}
