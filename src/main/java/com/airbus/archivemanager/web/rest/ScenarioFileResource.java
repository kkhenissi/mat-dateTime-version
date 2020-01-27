package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.repository.AuthorityRepository;
import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.security.SecurityUtils;
import com.airbus.archivemanager.service.OutputFileService;
import com.airbus.archivemanager.service.ScenarioFileQueryService;
import com.airbus.archivemanager.service.ScenarioFileService;
import com.airbus.archivemanager.service.UserService;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.dto.ScenarioFileCriteria;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;
import com.airbus.archivemanager.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * REST controller for managing {@link com.airbus.archivemanager.domain.ScenarioFile}.
 */
@RestController
@RequestMapping("/api")
public class ScenarioFileResource {

    private static final String ENTITY_NAME = "scenarioFile";
    private final Logger log = LoggerFactory.getLogger(ScenarioFileResource.class);
    private final ScenarioFileService scenarioFileService;
    private final ScenarioFileQueryService scenarioFileQueryService;
    private final MessageSource messageSource;
    @Autowired
    OutputFileService outputFileService;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    UserService userService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public ScenarioFileResource(ScenarioFileService scenarioFileService, ScenarioFileQueryService scenarioFileQueryService, MessageSource messageSource) {
        this.scenarioFileService = scenarioFileService;
        this.scenarioFileQueryService = scenarioFileQueryService;
        this.messageSource = messageSource;
    }

    /**
     * {@code PUT  /scenario-files} : Updates an existing scenarioFile.
     *
     * @param scenarioFileDTO the scenarioFileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scenarioFileDTO,
     * or with status {@code 400 (Bad Request)} if the scenarioFileDTO is not valid,
     * or with status {@code 403 (Forbidden Error)} if user without admin authority tries to modify metadata.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/scenario-files")
    public ResponseEntity<ScenarioFileDTO> updateScenarioFile(@Valid @RequestBody ScenarioFileDTO scenarioFileDTO) throws URISyntaxException {
        log.info("REST request to update ScenarioFile with relativePathInSt : {}", scenarioFileDTO.getRelativePathInST());
        //check if scenarioFileDTO get a relative path in ST
        if (!(scenarioFileService.findOne(scenarioFileDTO.getRelativePathInST()).isPresent())) {
            log.error("Invalid relative path in STS");
            String message = messageSource.getMessage("error.scenarioFile.scenarioFileNotFound", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "scenarioFileNotFound");
        }
        /** Check that only admin can update metadata
        * User can overall modify link with scenarios
        */
        Optional<ScenarioFileDTO> scenarioFileDTOOptional = scenarioFileService.findOne(scenarioFileDTO.getRelativePathInST());
        if (scenarioFileDTOOptional.isPresent() && !(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN))) {
            ScenarioFileDTO scenarioFileDTOToUpdate = scenarioFileDTOOptional.get();
            scenarioFileDTOToUpdate.setScenarios(scenarioFileDTO.getScenarios());
            scenarioFileDTO = scenarioFileDTOToUpdate;
        }
        ScenarioFileDTO result = scenarioFileService.update(scenarioFileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, scenarioFileDTO.getRelativePathInST()))
            .body(result);
    }

    /**
     * {@code GET  /scenario-files} : get all the scenarioFiles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scenarioFiles in body.
     */
    @GetMapping("/scenario-files")
    public ResponseEntity<List<ScenarioFileDTO>> getAllScenarioFiles(ScenarioFileCriteria criteria, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.info("REST request to get ScenarioFiles by criteria: {}", criteria);
        Page<ScenarioFileDTO> page = scenarioFileQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /scenario-files/count} : count all the scenarioFiles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/scenario-files/count")
    public ResponseEntity<Long> countScenarioFiles(ScenarioFileCriteria criteria) {
        log.info("REST request to count ScenarioFiles by criteria: {}", criteria);
        return ResponseEntity.ok().body(scenarioFileQueryService.countByCriteria(criteria));
    }

    /**
     * {@code DELETE  /scenario-files/:relativePathInST} : delete the "id" scenarioFile.
     *
     * @param scenarioFileDTO the scenarioFileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/scenario-files/")
    public ResponseEntity<Void> deleteScenarioFile(@RequestBody ScenarioFileDTO scenarioFileDTO) {
        log.info("REST request to delete ScenarioFile with relativePathInSt : {}", scenarioFileDTO.getRelativePathInST());
        Optional<ScenarioFileDTO> optionalScenarioFileDTO = scenarioFileService.findOne(scenarioFileDTO.getRelativePathInST());
        if (optionalScenarioFileDTO.isPresent()) {
            scenarioFileDTO = optionalScenarioFileDTO.get();
            scenarioFileService.delete(scenarioFileDTO);
        }
        else {
            String message = messageSource.getMessage("error.scenarioFile.scenarioFileNotFound", null, Locale.ENGLISH);
            log.error(message);
            throw new BadRequestAlertException(message, ENTITY_NAME, "InvalidRelativePathInST");
        }
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, scenarioFileDTO.getRelativePathInST())).build();
    }

    /**
     * {@code GET  /scenario-files-by-outputfile/} : get the all scenarioFiles from one outputFile.
     *
     * @param outputFileDTO the outputFileDTO which is linked to scenarioFiles.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scenarioFileDTO, or with status {@code 404 (Not Found)}.
     */
    @ApiOperation(value = "Return all scenarioFiles from an outputFile")
    @PostMapping("/scenario-files-by-outputfile/")
    public ResponseEntity<List<ScenarioFileDTO>> getScenarioFilesByOutputFileRelativePathInST(@RequestBody OutputFileDTO outputFileDTO, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        log.info("REST request to get ScenarioFiles by outputFile with relativePathInSt: {}", outputFileDTO.getRelativePathInST());
        Optional<OutputFileDTO> optionalOutputFileDTO = outputFileService.findOne(outputFileDTO.getRelativePathInST());
        if (!optionalOutputFileDTO.isPresent()) {
            log.error("Unknown outputFile");
            String message = messageSource.getMessage("error.scenarioFile.outputFileNotFound", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "outputFileNotFound");
        }
        OutputFileDTO outputFileDTOComplete = optionalOutputFileDTO.get();
        Page<ScenarioFileDTO> page = scenarioFileService.findByOutputFileRelativePathInST(outputFileDTOComplete, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /scenario-files-by-outputfile/count/:outputFileRelativePathInST} : count all the scenarioFiles with same outputFile.
     *
     * @param outputFileDTO the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @ApiOperation(value = "Return count of scenarioFiles from an outputFile")
    @PostMapping("/scenario-files-by-outputfile/count/")
    public ResponseEntity<Long> countScenarioFilesByOutputFileRelativePathInST(@RequestBody OutputFileDTO outputFileDTO) {
        log.info("REST request to count ScenarioFiles by outputFileDTO with relativePathInSt: {}", outputFileDTO.getRelativePathInST());
        Optional<OutputFileDTO> optionalOutputFileDTO = outputFileService.findOne(outputFileDTO.getRelativePathInST());
        if (!optionalOutputFileDTO.isPresent()) {
            log.error("Unknown outputFile");
            String message = messageSource.getMessage("error.scenarioFile.outputFileNotFound", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "outputFileNotFound");
        }
        OutputFileDTO outputFileDTOComplete = optionalOutputFileDTO.get();
        return ResponseEntity.ok().body(scenarioFileService.countByOutputFileRelativePathInST(outputFileDTOComplete));
    }
}
