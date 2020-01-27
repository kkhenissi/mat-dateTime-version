package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.domain.DataSet;
import com.airbus.archivemanager.repository.DataSetRepository;
import com.airbus.archivemanager.service.DataSetService;
import com.airbus.archivemanager.service.dto.DataSetDTO;
import com.airbus.archivemanager.service.mapper.DataSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link DataSet}.
 */
@Service
@Transactional
public class DataSetServiceImpl implements DataSetService {

    private final Logger log = LoggerFactory.getLogger(DataSetServiceImpl.class);

    private final DataSetRepository dataSetRepository;

    private final DataSetMapper dataSetMapper;

    public DataSetServiceImpl(DataSetRepository dataSetRepository, DataSetMapper dataSetMapper) {
        this.dataSetRepository = dataSetRepository;
        this.dataSetMapper = dataSetMapper;
    }

    /**
     * Save a dataSet.
     *
     * @param dataSetDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public DataSetDTO save(DataSetDTO dataSetDTO) {
        log.debug("Request to save DataSet : {}", dataSetDTO);
        DataSet dataSet = dataSetMapper.toEntity(dataSetDTO);
        dataSet = dataSetRepository.save(dataSet);
        return dataSetMapper.toDto(dataSet);
    }

    /**
     * Get all the dataSets.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DataSetDTO> findAll() {
        log.debug("Request to get all DataSets");
        return dataSetRepository.findAll().stream()
            .map(dataSetMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one dataSet by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<DataSetDTO> findOne(Long id) {
        log.debug("Request to get DataSet : {}", id);
        return dataSetRepository.findById(id)
            .map(dataSetMapper::toDto);
    }

    /**
     * Delete the dataSet by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete DataSet : {}", id);
        dataSetRepository.deleteById(id);
    }
}
