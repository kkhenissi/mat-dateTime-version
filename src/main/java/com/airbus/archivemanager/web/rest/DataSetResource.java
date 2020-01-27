package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.service.DataSetQueryService;
import com.airbus.archivemanager.service.DataSetService;
import com.airbus.archivemanager.service.dto.DataSetCriteria;
import com.airbus.archivemanager.web.rest.errors.BadRequestAlertException;
import com.airbus.archivemanager.service.dto.DataSetDTO;

import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * REST controller for managing {@link com.airbus.archivemanager.domain.DataSet}.
 */
@RestController
@RequestMapping("/api")
public class DataSetResource {

    private final Logger log = LoggerFactory.getLogger(DataSetResource.class);
    private static final String ENTITY_NAME = "dataSet";
    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    private final DataSetService dataSetService;
    private final DataSetQueryService dataSetQueryService;
    private final MessageSource messageSource;

    public DataSetResource(DataSetService dataSetService, MessageSource messageSource, DataSetQueryService dataSetQueryService) {
        this.dataSetService = dataSetService;
        this.messageSource = messageSource;
        this.dataSetQueryService = dataSetQueryService;
    }

    /**
     * {@code POST  /data-sets} : Create a new dataSet.
     *
     * @param dataSetDTO the dataSetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dataSetDTO,
     * or with status {@code 400 (Bad Request)} if the dataSet has already an ID.
     * or with status {@code 400 (Bad Request)} if the name is already used,
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/data-sets")
    public ResponseEntity<DataSetDTO> createDataSet(@Valid @RequestBody DataSetDTO dataSetDTO) throws URISyntaxException {
        log.info("REST request to save DataSet with name : {}", dataSetDTO.getName());
        if (dataSetDTO.getId() != null) {
            String message = messageSource.getMessage("error.dataset.idAlreadyExists", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "idAlreadyExists");
        }
        StringFilter nameDTO = new StringFilter();
        nameDTO.setEquals(dataSetDTO.getName());
        DataSetCriteria criteria = new DataSetCriteria();
        criteria.setName(nameDTO);
        if(!dataSetQueryService.findByCriteria(criteria).isEmpty()){
            String message = messageSource.getMessage("error.dataset.nameAlreadyUsed", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "nameAlreadyUsed");
        }
        DataSetDTO result = dataSetService.save(dataSetDTO);
        return ResponseEntity.created(new URI("/api/data-sets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /data-sets} : Updates an existing dataSet.
     *
     * @param dataSetDTO the dataSetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dataSetDTO,
     * or with status {@code 400 (Bad Request)} if the dataSetDTO is not valid,
     * or with status {@code 400 (Bad Request)} if the name is already used,
     * or with status {@code 500 (Internal Server Error)} if the dataSetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/data-sets")
    public ResponseEntity<DataSetDTO> updateDataSet(@Valid @RequestBody DataSetDTO dataSetDTO) throws URISyntaxException {
        log.info("REST request to update DataSet with id : {}", dataSetDTO.getId());
        if (dataSetDTO.getId() == null || !(dataSetService.findOne(dataSetDTO.getId()).isPresent())) {
            String message = messageSource.getMessage("error.dataset.dataSetNotFound", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "dataSetNotFound");
        }
        StringFilter nameDTO = new StringFilter();
        nameDTO.setEquals(dataSetDTO.getName());
        DataSetCriteria criteria = new DataSetCriteria();
        criteria.setName(nameDTO);
        if(!dataSetQueryService.findByCriteria(criteria).isEmpty()){
            String message = messageSource.getMessage("error.dataset.nameAlreadyUsed", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "nameAlreadyUsed");
        }
        DataSetDTO result = dataSetService.save(dataSetDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, dataSetDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /data-sets} : get all the dataSets.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dataSets in body.
     */
    @GetMapping("/data-sets")
    public ResponseEntity<List<DataSetDTO>> getAllDataSets(DataSetCriteria criteria, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.info("REST request to get DataSets by criteria: {}", criteria);
        Page<DataSetDTO> page = dataSetQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /data-sets/count} : count all the dataSets.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/data-sets/count")
    public ResponseEntity<Long> countDataSet(DataSetCriteria criteria) {
        log.info("REST request to count DataSets by criteria: {}", criteria);
        return ResponseEntity.ok().body(dataSetQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /data-sets/:id} : get the "id" dataSet.
     *
     * @param id the id of the dataSetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dataSetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/data-sets/{id}")
    public ResponseEntity<DataSetDTO> getDataSet(@PathVariable Long id) {
        log.info("REST request to get DataSet with id : {}", id);
        Optional<DataSetDTO> dataSetDTO = dataSetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dataSetDTO);
    }

    /**
     * {@code DELETE  /data-sets/:id} : delete the "id" dataSet.
     *
     * @param id the id of the dataSetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/data-sets/{id}")
    public ResponseEntity<Void> deleteDataSet(@PathVariable Long id) {
        log.info("REST request to delete DataSet with id : {}", id);
        dataSetService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
