package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.service.ToolVersionService;
import com.airbus.archivemanager.domain.ToolVersion;
import com.airbus.archivemanager.repository.ToolVersionRepository;
import com.airbus.archivemanager.service.dto.ToolVersionDTO;
import com.airbus.archivemanager.service.mapper.ToolVersionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link ToolVersion}.
 */
@Service
@Transactional
public class ToolVersionServiceImpl implements ToolVersionService {

    private final Logger log = LoggerFactory.getLogger(ToolVersionServiceImpl.class);

    private final ToolVersionRepository toolVersionRepository;

    private final ToolVersionMapper toolVersionMapper;

    public ToolVersionServiceImpl(ToolVersionRepository toolVersionRepository, ToolVersionMapper toolVersionMapper) {
        this.toolVersionRepository = toolVersionRepository;
        this.toolVersionMapper = toolVersionMapper;
    }

    /**
     * Save a toolVersion.
     *
     * @param toolVersionDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ToolVersionDTO save(ToolVersionDTO toolVersionDTO) {
        log.debug("Request to save ToolVersion : {}", toolVersionDTO);
        ToolVersion toolVersion = toolVersionMapper.toEntity(toolVersionDTO);
        toolVersion = toolVersionRepository.save(toolVersion);
        return toolVersionMapper.toDto(toolVersion);
    }

    /**
     * Get all the toolVersions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ToolVersionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ToolVersions");
        return toolVersionRepository.findAll(pageable)
            .map(toolVersionMapper::toDto);
    }


    /**
     * Get one toolVersion by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ToolVersionDTO> findOne(Long id) {
        log.debug("Request to get ToolVersion : {}", id);
        return toolVersionRepository.findById(id)
            .map(toolVersionMapper::toDto);
    }

    /**
     * Delete the toolVersion by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ToolVersion : {}", id);
        toolVersionRepository.deleteById(id);
    }
}
