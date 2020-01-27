package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.service.ConfigDataSetService;
import com.airbus.archivemanager.domain.ConfigDataSet;
import com.airbus.archivemanager.repository.ConfigDataSetRepository;
import com.airbus.archivemanager.service.dto.ConfigDataSetDTO;
import com.airbus.archivemanager.service.mapper.ConfigDataSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link ConfigDataSet}.
 */
@Service
@Transactional
public class ConfigDataSetServiceImpl implements ConfigDataSetService {

    private final Logger log = LoggerFactory.getLogger(ConfigDataSetServiceImpl.class);

    private final ConfigDataSetRepository configDataSetRepository;

    private final ConfigDataSetMapper configDataSetMapper;

    public ConfigDataSetServiceImpl(ConfigDataSetRepository configDataSetRepository, ConfigDataSetMapper configDataSetMapper) {
        this.configDataSetRepository = configDataSetRepository;
        this.configDataSetMapper = configDataSetMapper;
    }

    /**
     * Save a configDataSet.
     *
     * @param configDataSetDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ConfigDataSetDTO save(ConfigDataSetDTO configDataSetDTO) {
        log.debug("Request to save ConfigDataSet : {}", configDataSetDTO);
        ConfigDataSet configDataSet = configDataSetMapper.toEntity(configDataSetDTO);
        configDataSet = configDataSetRepository.save(configDataSet);
        return configDataSetMapper.toDto(configDataSet);
    }

    /**
     * Get all the configDataSets.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ConfigDataSetDTO> findAll() {
        log.debug("Request to get all ConfigDataSets");
        return configDataSetRepository.findAll().stream()
            .map(configDataSetMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one configDataSet by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ConfigDataSetDTO> findOne(Long id) {
        log.debug("Request to get ConfigDataSet : {}", id);
        return configDataSetRepository.findById(id)
            .map(configDataSetMapper::toDto);
    }

    /**
     * Delete the configDataSet by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ConfigDataSet : {}", id);
        configDataSetRepository.deleteById(id);
    }
}
