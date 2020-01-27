package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.service.ToolVersionQueryService;
import com.airbus.archivemanager.service.ToolVersionService;
import com.airbus.archivemanager.service.dto.ToolVersionCriteria;
import com.airbus.archivemanager.service.dto.ToolVersionDTO;
import com.airbus.archivemanager.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Optional;

/**
 * REST controller for managing {@link com.airbus.archivemanager.domain.ToolVersion}.
 */
@RestController
@RequestMapping("/api")
public class ToolVersionResource {

    private final Logger log = LoggerFactory.getLogger(ToolVersionResource.class);

    private static final String ENTITY_NAME = "toolVersion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ToolVersionService toolVersionService;

    private final ToolVersionQueryService toolVersionQueryService;

    public ToolVersionResource(ToolVersionService toolVersionService, ToolVersionQueryService toolVersionQueryService) {
        this.toolVersionService = toolVersionService;
        this.toolVersionQueryService = toolVersionQueryService;
    }

    /**
     * {@code POST  /tool-versions} : Create a new toolVersion.
     *
     * @param toolVersionDTO the toolVersionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new toolVersionDTO, or with status {@code 400 (Bad Request)} if the toolVersion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tool-versions")
    public ResponseEntity<ToolVersionDTO> createToolVersion(@Valid @RequestBody ToolVersionDTO toolVersionDTO) throws URISyntaxException {
        log.info("REST request to save ToolVersion with name : {}", toolVersionDTO.getName());
        if (toolVersionDTO.getId() != null) {
            throw new BadRequestAlertException("A new toolVersion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ToolVersionDTO result = toolVersionService.save(toolVersionDTO);
        return ResponseEntity.created(new URI("/api/tool-versions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tool-versions} : Updates an existing toolVersion.
     *
     * @param toolVersionDTO the toolVersionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated toolVersionDTO,
     * or with status {@code 400 (Bad Request)} if the toolVersionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the toolVersionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tool-versions")
    public ResponseEntity<ToolVersionDTO> updateToolVersion(@Valid @RequestBody ToolVersionDTO toolVersionDTO) throws URISyntaxException {
        log.info("REST request to update ToolVersion with id : {}", toolVersionDTO.getId());
        if (toolVersionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ToolVersionDTO result = toolVersionService.save(toolVersionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, toolVersionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /tool-versions} : get all the toolVersions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of toolVersions in body.
     */
    @GetMapping("/tool-versions")
    public ResponseEntity<List<ToolVersionDTO>> getAllToolVersions(ToolVersionCriteria criteria, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.info("REST request to get ToolVersions by criteria: {}", criteria);
        Page<ToolVersionDTO> page = toolVersionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tool-versions/count} : count all the toolVersions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/tool-versions/count")
    public ResponseEntity<Long> countToolVersions(ToolVersionCriteria criteria) {
        log.info("REST request to count ToolVersions by criteria: {}", criteria);
        return ResponseEntity.ok().body(toolVersionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tool-versions/:id} : get the "id" toolVersion.
     *
     * @param id the id of the toolVersionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the toolVersionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tool-versions/{id}")
    public ResponseEntity<ToolVersionDTO> getToolVersion(@PathVariable Long id) {
        log.info("REST request to get ToolVersion with id : {}", id);
        Optional<ToolVersionDTO> toolVersionDTO = toolVersionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(toolVersionDTO);
    }

    /**
     * {@code DELETE  /tool-versions/:id} : delete the "id" toolVersion.
     *
     * @param id the id of the toolVersionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tool-versions/{id}")
    public ResponseEntity<Void> deleteToolVersion(@PathVariable Long id) {
        log.info("REST request to delete ToolVersion with id : {}", id);
        toolVersionService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
