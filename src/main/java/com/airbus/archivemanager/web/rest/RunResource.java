package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.domain.enumeration.RunStatus;
import com.airbus.archivemanager.service.RunQueryService;
import com.airbus.archivemanager.service.RunService;
import com.airbus.archivemanager.service.ScenarioService;
import com.airbus.archivemanager.service.ToolVersionService;
import com.airbus.archivemanager.service.dto.RunCriteria;
import com.airbus.archivemanager.service.dto.RunDTO;
import com.airbus.archivemanager.service.dto.ToolVersionDTO;
import com.airbus.archivemanager.web.rest.errors.BadRequestAlertException;
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
import java.util.*;

/**
 * REST controller for managing {@link com.airbus.archivemanager.domain.Run}.
 */
@RestController
@RequestMapping("/api")
public class RunResource {

    private static final String ENTITY_NAME = "run";
    private final Logger log = LoggerFactory.getLogger(RunResource.class);
    private final RunService runService;
    private final RunQueryService runQueryService;
    private final MessageSource messageSource;
    private final ScenarioService scenarioService;
    private final ToolVersionService toolVersionService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public RunResource(RunService runService, RunQueryService runQueryService, MessageSource messageSource, ScenarioService scenarioService, ToolVersionService toolVersionService) {
        this.runService = runService;
        this.runQueryService = runQueryService;
        this.messageSource = messageSource;
        this.scenarioService = scenarioService;
        this.toolVersionService = toolVersionService;
    }

    /**
     * {@code POST  /runs} : Create a new run.
     *
     * @param runDTO the runDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new runDTO,
     * or with status {@code 400 (Bad Request)} if the run has already an ID,
     * or with status {@code 400 (Bad Request)} if the run has a end date earlier than start date.
     * or with status {@code 400 (Bad Request)} if the run has a null or unknown scenario ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/runs")
    public ResponseEntity<RunDTO> createRun(@Valid @RequestBody RunDTO runDTO) throws URISyntaxException {
        log.info("REST request to save Run with description : {}", runDTO.getDescription());
        if (runDTO.getId() != null) {
            String message = messageSource.getMessage("error.run.idNotNull", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "idNotNull");
        }

        checkDates(runDTO);
        checkScenarios(runDTO);
        checkToolVersion(runDTO);

        if (runDTO.getStatus() == null) {
            runDTO.setStatus(RunStatus.UNKNOWN);
        }
        RunDTO result = runService.save(runDTO);
        return ResponseEntity.created(new URI("/api/runs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /runs} : Updates an existing run.
     *
     * @param runDTO the runDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated runDTO,
     * or with status {@code 400 (Bad Request)} if the runDTO is not valid,
     * or with status {@code 400 (Bad Request)} if the endDate earlier than startDate.
     * or with status {@code 400 (Bad Request)} if the runDTO hasn't a scenario ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/runs")
    public ResponseEntity<RunDTO> updateRun(@Valid @RequestBody RunDTO runDTO) throws URISyntaxException {
        log.info("REST request to update Run with id : {}", runDTO.getId());
        if (runDTO.getId() == null || !(runService.findOne(runDTO.getId()).isPresent())) {
            String message = messageSource.getMessage("error.run.invalidID", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "invalidID");
        }

        checkDates(runDTO);
        checkScenarios(runDTO);
        checkToolVersion(runDTO);

        RunDTO result = runService.update(runDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, runDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /runs} : get all the runs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of runs in body.
     */
    @GetMapping("/runs")
    public ResponseEntity<List<RunDTO>> getAllRuns(RunCriteria criteria, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.info("REST request to get Runs by criteria: {}", criteria);
        Page<RunDTO> page = runQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /runs/count} : count all the runs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/runs/count")
    public ResponseEntity<Long> countRuns(RunCriteria criteria) {
        log.info("REST request to count Runs by criteria: {}", criteria);
        return ResponseEntity.ok().body(runQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /runs/:id} : get the "id" run.
     *
     * @param id the id of the runDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the runDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/runs/{id}")
    public ResponseEntity<RunDTO> getRun(@PathVariable Long id) {
        log.info("REST request to get Run with id : {}", id);
        Optional<RunDTO> runDTO = runService.findOne(id);
        return ResponseUtil.wrapOrNotFound(runDTO);
    }

    /**
     * {@code DELETE  /runs/:id} : delete the "id" run.
     *
     * @param id the id of the runDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/runs/{id}")
    public ResponseEntity<Void> deleteRun(@PathVariable Long id) {
        log.info("REST request to delete Run with id : {}", id);
        runService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * check if ToolVersions in runDTO are valid in database.
     *
     * @param runDTO the runDTO to check.
     *               throw status {@code 400 (Bad Request)} if one ToolVersionDTO is not valid
     */
    private void checkToolVersion(RunDTO runDTO) {
        List<Long> idToolVersionDTOList = new ArrayList<>();
        if (runDTO.getToolVersions() != null) {
            Set<ToolVersionDTO> toolVersionDTOSet = runDTO.getToolVersions();
            for (ToolVersionDTO toolVersionDTO : toolVersionDTOSet) {
                idToolVersionDTOList.add(toolVersionDTO.getId());
            }
        }
        List<String> wrongIdToolVersionList = new ArrayList<>();
        for (Long idTools : idToolVersionDTOList) {
            Optional<ToolVersionDTO> toolVersionDTOOptional = toolVersionService.findOne(idTools);
            if (!toolVersionDTOOptional.isPresent()) {
                wrongIdToolVersionList.add(idTools.toString());
            }
        }

        if (!wrongIdToolVersionList.isEmpty()) {
            List[] variableArray = new List[]{wrongIdToolVersionList};
            String message = messageSource.getMessage("error.run.toolVersionNotValid", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new BadRequestAlertException(message, ENTITY_NAME, "toolVersionNotExisted");
        }
    }

    /**
     * check if Scenarios in runDTO are valid in database.
     *
     * @param runDTO the runDTO to check.
     *               throw status {@code 400 (Bad Request)} if one ScenarioDTO is not valid
     */
    private void checkScenarios(RunDTO runDTO) {
        if (runDTO.getScenarioId() == null || !(scenarioService.findOne(runDTO.getScenarioId()).isPresent())) {
            String[] variableArray = new String[]{runDTO.getScenarioId().toString()};
            String message = messageSource.getMessage("error.run.runMustHaveValidScenarioId", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new BadRequestAlertException(message, ENTITY_NAME, "runMustHaveValidScenarioId");
        }
    }

    /**
     * check if endDate is after or equals than startDate in runDTO.
     *
     * @param runDTO the runDTO to check.
     *               throw status {@code 400 (Bad Request)} if endDate is before than startDate
     */
    private void checkDates(RunDTO runDTO) {
        if (runDTO.getEndDate() != null &&
            runDTO.getStartDate() != null &&
            !runDTO.getEndDate().isAfter(runDTO.getStartDate()) &&
            !runDTO.getEndDate().equals(runDTO.getStartDate())) {
            log.error("end date is before start date");
            String message = messageSource.getMessage("error.run.endDateEarlierThanStartDate", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "endDateEarlierThanStartDate");
        }
    }
}
