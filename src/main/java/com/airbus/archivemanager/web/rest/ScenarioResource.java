package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.domain.enumeration.SimulationModeType;
import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.security.SecurityUtils;
import com.airbus.archivemanager.service.ScenarioQueryService;
import com.airbus.archivemanager.service.ScenarioService;
import com.airbus.archivemanager.service.dto.ScenarioCriteria;
import com.airbus.archivemanager.service.dto.ScenarioDTO;
import com.airbus.archivemanager.web.rest.errors.BadRequestAlertException;
import com.airbus.archivemanager.web.rest.errors.ForbiddenAlertException;
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
 * REST controller for managing {@link com.airbus.archivemanager.domain.Scenario}.
 */
@RestController
@RequestMapping("/api")
public class ScenarioResource {

    private final Logger log = LoggerFactory.getLogger(ScenarioResource.class);
    private static final String ENTITY_NAME = "scenario";
    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    private final ScenarioService scenarioService;
    private final ScenarioQueryService scenarioQueryService;
    private final MessageSource messageSource;

    public ScenarioResource(ScenarioService scenarioService, ScenarioQueryService scenarioQueryService, MessageSource messageSource) {
        this.scenarioService = scenarioService;
        this.scenarioQueryService = scenarioQueryService;
        this.messageSource = messageSource;
    }

    /**
     * {@code POST  /scenarios} : Create a new scenario.
     *
     * @param scenarioDTO the scenarioDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scenarioDTO,
     * or with status {@code 400 (Bad Request)} if the scenario has already an ID,
     * or with status {@code 400 (Bad Request)} if the name is already used,.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/scenarios")
    public ResponseEntity<ScenarioDTO> createScenario(@Valid @RequestBody ScenarioDTO scenarioDTO) throws URISyntaxException {
        log.info("REST request to save Scenario with description : {}", scenarioDTO.getDescription());
        if (scenarioDTO.getId() != null) {
            String message = messageSource.getMessage("error.scenario.idAlreadyExists", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "idexists");
        }
        if (scenarioDTO.getEndSimulatedDate() != null &&
            scenarioDTO.getStartSimulatedDate() != null &&
            !scenarioDTO.getEndSimulatedDate().isAfter(scenarioDTO.getStartSimulatedDate()) &&
            !scenarioDTO.getEndSimulatedDate().equals(scenarioDTO.getStartSimulatedDate())) {
            log.error("end simulated date is before start simulated date");
            String message = messageSource.getMessage("error.scenario.simulationEarlierThanStartSimulatedDate", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "simulationEarlierThanStartSimulatedDate");
        }
        if (scenarioDTO.getSimulationMode() == null) {
            scenarioDTO.setSimulationMode(SimulationModeType.UNDEFINED);
        }
        StringFilter nameDTO = new StringFilter();
        nameDTO.setEquals(scenarioDTO.getName());
        ScenarioCriteria criteria = new ScenarioCriteria();
        criteria.setName(nameDTO);
        if (!scenarioQueryService.findByCriteria(criteria).isEmpty()) {
            String message = messageSource.getMessage("error.scenario.nameAlreadyUsed", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "nameAlreadyUsed");
        }
        ScenarioDTO result = scenarioService.save(scenarioDTO);
        return ResponseEntity.created(new URI("/api/scenarios/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /scenarios} : Updates an existing scenario.
     *
     * @param scenarioDTO the scenarioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scenarioDTO,
     * or with status {@code 400 (Bad Request)} if the scenarioDTO is null or not valid,
     * or with status {@code 403 (Forbidden Error)} if user without admin authority tries to modify metadata.
     * or with status {@code 400 (Bad Request)} if the scenario has a end date earlier than start date.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/scenarios")
    public ResponseEntity<ScenarioDTO> updateScenario(@Valid @RequestBody ScenarioDTO scenarioDTO) throws URISyntaxException {
        log.info("REST request to update Scenario with id : {}", scenarioDTO.getId());
        if (scenarioDTO.getId() == null || !(scenarioService.findOne(scenarioDTO.getId()).isPresent())) {
            log.error("scenarioId is null or not exists");
            String message = messageSource.getMessage("error.scenario.scenarioNotFound", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "scenarioNotFound");
        }
        Optional<ScenarioDTO> scenarioDTOOptional = scenarioService.findOne(scenarioDTO.getId());
        if (scenarioDTOOptional.isPresent() && !(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN))) {
            log.error("Only Admin can modify metadata");
            String message = messageSource.getMessage("error.scenario.adminOnlyCanModifyMetadata", null, Locale.ENGLISH);
            throw new ForbiddenAlertException(message, ENTITY_NAME, "adminOnlyCanModifyMetadata");
        }
        if (scenarioDTO.getEndSimulatedDate() != null &&
            scenarioDTO.getStartSimulatedDate() != null &&
            !scenarioDTO.getEndSimulatedDate().isAfter(scenarioDTO.getStartSimulatedDate()) &&
            !scenarioDTO.getEndSimulatedDate().equals(scenarioDTO.getStartSimulatedDate())) {
            log.error("end simulated date is before start simulated date");
            String message = messageSource.getMessage("error.scenario.simulationEarlierThanStartSimulatedDate", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "simulationEarlierThanStartSimulatedDate");
        }
        StringFilter nameDTO = new StringFilter();
        nameDTO.setEquals(scenarioDTO.getName());
        ScenarioCriteria criteria = new ScenarioCriteria();
        criteria.setName(nameDTO);
        List<ScenarioDTO> listWithSameName = scenarioQueryService.findByCriteria(criteria);
        for (ScenarioDTO scenarioDTOFromList : listWithSameName) {
            if (!scenarioDTOFromList.getId().equals(scenarioDTO.getId())) {
                String message = messageSource.getMessage("error.scenario.nameAlreadyUsed", null, Locale.ENGLISH);
                throw new BadRequestAlertException(message, ENTITY_NAME, "nameAlreadyUsed");
            }
        }
        ScenarioDTO result = scenarioService.update(scenarioDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, scenarioDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /scenarios} : get all the scenarios.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scenarios in body.
     */
    @GetMapping("/scenarios")
    public ResponseEntity<List<ScenarioDTO>> getAllScenarios(ScenarioCriteria criteria, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.info("REST request to get Scenarios by criteria: {}", criteria);
        Page<ScenarioDTO> page = scenarioQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /scenarios/count} : count all the scenarios.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/scenarios/count")
    public ResponseEntity<Long> countScenarios(ScenarioCriteria criteria) {
        log.info("REST request to count Scenarios by criteria: {}", criteria);
        return ResponseEntity.ok().body(scenarioQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /scenarios/:id} : get the "id" scenario.
     *
     * @param id the id of the scenarioDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scenarioDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/scenarios/{id}")
    public ResponseEntity<ScenarioDTO> getScenario(@PathVariable Long id) {
        log.info("REST request to get Scenario with id : {}", id);
        Optional<ScenarioDTO> scenarioDTO = scenarioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scenarioDTO);
    }

    /**
     * {@code DELETE  /scenarios/:id} : delete the "id" scenario.
     *
     * @param id the id of the scenarioDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/scenarios/{id}")
    public ResponseEntity<Void> deleteScenario(@PathVariable Long id) {
        log.info("REST request to delete Scenario with id : {}", id);
        scenarioService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
