package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.service.ConfigDataSetQueryService;
import com.airbus.archivemanager.service.ConfigDataSetService;
import com.airbus.archivemanager.service.dto.ConfigDataSetCriteria;
import com.airbus.archivemanager.service.dto.ConfigDataSetDTO;
import com.airbus.archivemanager.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.airbus.archivemanager.domain.ConfigDataSet}.
 */
@RestController
@RequestMapping("/api")
public class ConfigDataSetResource {

    private final Logger log = LoggerFactory.getLogger(ConfigDataSetResource.class);

    private static final String ENTITY_NAME = "configDataSet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConfigDataSetService configDataSetService;
    private final ConfigDataSetQueryService configDataSetQueryService;
    private final MessageSource messageSource;

    public ConfigDataSetResource(ConfigDataSetService configDataSetService, ConfigDataSetQueryService configDataSetQueryService, MessageSource messageSource) {
        this.configDataSetService = configDataSetService;
        this.configDataSetQueryService = configDataSetQueryService;
        this.messageSource = messageSource;
    }

    /**
     * {@code POST  /config-data-sets} : Create a new configDataSet.
     *
     * @param configDataSetDTO the configDataSetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new configDataSetDTO, or with status {@code 400 (Bad Request)} if the configDataSet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/config-data-sets")
    public ResponseEntity<ConfigDataSetDTO> createConfigDataSet(@Valid @RequestBody ConfigDataSetDTO configDataSetDTO) throws URISyntaxException {
        log.info("REST request to save ConfigDataSet with name : {}", configDataSetDTO.getName());
        if (configDataSetDTO.getId() != null) {
            String message = messageSource.getMessage("error.configdataset.idAlreadyExists", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "idAlreadyExists");
        }
        StringFilter nameDTO = new StringFilter();
        nameDTO.setEquals(configDataSetDTO.getName());
        ConfigDataSetCriteria criteria = new ConfigDataSetCriteria();
        criteria.setName(nameDTO);
        if (!configDataSetQueryService.findByCriteria(criteria).isEmpty()) {
            String message = messageSource.getMessage("error.configdataset.nameAlreadyUsed", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "nameAlreadyUsed");
        }
        ConfigDataSetDTO result = configDataSetService.save(configDataSetDTO);
        return ResponseEntity.created(new URI("/api/config-data-sets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /config-data-sets} : Updates an existing configDataSet.
     *
     * @param configDataSetDTO the configDataSetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated configDataSetDTO,
     * or with status {@code 400 (Bad Request)} if the configDataSetDTO is not valid,
     * * or with status {@code 400 (Bad Request)} if the name is already used,
     * or with status {@code 500 (Internal Server Error)} if the configDataSetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/config-data-sets")
    public ResponseEntity<ConfigDataSetDTO> updateConfigDataSet(@Valid @RequestBody ConfigDataSetDTO configDataSetDTO) throws URISyntaxException {
        log.info("REST request to update ConfigDataSet with id : {}", configDataSetDTO.getId());
        if (configDataSetDTO.getId() == null) {
            String message = messageSource.getMessage("error.configdataset.configDataSetNotFound", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "dataSetNotFound");
        }
        StringFilter nameDTO = new StringFilter();
        nameDTO.setEquals(configDataSetDTO.getName());
        ConfigDataSetCriteria criteria = new ConfigDataSetCriteria();
        criteria.setName(nameDTO);
        if (!configDataSetQueryService.findByCriteria(criteria).isEmpty()) {
            String message = messageSource.getMessage("error.configdataset.nameAlreadyUsed", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "nameAlreadyUsed");
        }
        ConfigDataSetDTO result = configDataSetService.save(configDataSetDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, configDataSetDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /config-data-sets} : get all the ConfigDataSets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dataSets in body.
     */
    @GetMapping("/config-data-sets")
    public ResponseEntity<List<ConfigDataSetDTO>> getAllDataSets(ConfigDataSetCriteria criteria, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.info("REST request to get ConfigDataSets by criteria: {}", criteria);
        Page<ConfigDataSetDTO> page = configDataSetQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /config-data-sets/count} : count all the ConfigDataSets.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/config-data-sets/count")
    public ResponseEntity<Long> countDataSet(ConfigDataSetCriteria criteria) {
        log.info("REST request to count ConfigDataSets by criteria: {}", criteria);
        return ResponseEntity.ok().body(configDataSetQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /config-data-sets/:id} : get the "id" configDataSet.
     *
     * @param id the id of the configDataSetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the configDataSetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/config-data-sets/{id}")
    public ResponseEntity<ConfigDataSetDTO> getConfigDataSet(@PathVariable Long id) {
        log.info("REST request to get ConfigDataSet with id : {}", id);
        Optional<ConfigDataSetDTO> configDataSetDTO = configDataSetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(configDataSetDTO);
    }

    /**
     * {@code DELETE  /config-data-sets/:id} : delete the "id" configDataSet.
     *
     * @param id the id of the configDataSetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/config-data-sets/{id}")
    public ResponseEntity<Void> deleteConfigDataSet(@PathVariable Long id) {
        log.info("REST request to delete ConfigDataSet with id : {}", id);
        configDataSetService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
