package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.security.SecurityUtils;
import com.airbus.archivemanager.service.OutputFileQueryService;
import com.airbus.archivemanager.service.OutputFileService;
import com.airbus.archivemanager.service.dto.OutputFileCriteria;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.web.rest.errors.BadRequestAlertException;
import com.airbus.archivemanager.web.rest.errors.ForbiddenAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
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
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * REST controller for managing {@link com.airbus.archivemanager.domain.OutputFile}.
 */
@RestController
@RequestMapping("/api")
public class OutputFileResource {

    private static final String ENTITY_NAME = "outputFile";
    private final Logger log = LoggerFactory.getLogger(OutputFileResource.class);
    private final MessageSource messageSource;
    private final OutputFileService outputFileService;
    private final OutputFileQueryService outputFileQueryService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public OutputFileResource(OutputFileService outputFileService, OutputFileQueryService outputFileQueryService, MessageSource messageSource) {
        this.outputFileService = outputFileService;
        this.outputFileQueryService = outputFileQueryService;
        this.messageSource = messageSource;
    }

    /**
     * {@code PUT  /output-files} : Updates an existing outputFile.
     *
     * @param outputFileDTO the outputFileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated outputFileDTO,
     * or with status {@code 400 (Bad Request)} if the outputFileDTO is not valid,
     * or with status {@code 403 (Forbidden)} if the user hasn't admin authority,
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/output-files")
    public ResponseEntity<OutputFileDTO> updateOutputFile(@Valid @RequestBody OutputFileDTO outputFileDTO) throws URISyntaxException {
        log.info("REST request to update OutputFile with relativePathInSt : {}", outputFileDTO.getRelativePathInST());
        if (!(outputFileService.findOne(outputFileDTO.getRelativePathInST()).isPresent())) {
            log.error("Invalid relative path in STS");
            String message = messageSource.getMessage("error.outputFile.InvalidRelativePathInST", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "InvalidRelativePathInST");
        }
        //Check that only admin can update metadata
        Optional<OutputFileDTO> outputFileDTOOptional = outputFileService.findOne(outputFileDTO.getRelativePathInST());
        if (outputFileDTOOptional.isPresent() && !(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN))) {
            log.error("Only Admin can modify metadata");
            String message = messageSource.getMessage("error.outputFile.adminOnlyCanModifyMetadata", null, Locale.ENGLISH);
            throw new ForbiddenAlertException(message, ENTITY_NAME, "adminOnlyCanModifyMetadata");
        }
        OutputFileDTO result = outputFileService.update(outputFileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, outputFileDTO.getRelativePathInST()))
            .body(result);
    }

    /**
     * {@code GET  /output-files} : get all the outputFiles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of outputFiles in body.
     */
    @GetMapping("/output-files")
    public ResponseEntity<List<OutputFileDTO>> getAllOutputFiles(OutputFileCriteria criteria, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.info("REST request to get OutputFiles by criteria: {}", criteria);
        Page<OutputFileDTO> page = outputFileQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /output-files/count} : count all the outputFiles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/output-files/count")
    public ResponseEntity<Long> countOutputFiles(OutputFileCriteria criteria) {
        log.info("REST request to count OutputFiles by criteria: {}", criteria);
        return ResponseEntity.ok().body(outputFileQueryService.countByCriteria(criteria));
    }

    /**
     * {@code DELETE  /output-files/:id} : delete the "id" outputFile.
     *
     * @param outputFileDTO the outputFileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/output-files/")
    public ResponseEntity<Void> deleteOutputFile(@RequestBody OutputFileDTO outputFileDTO) {
        log.info("REST request to delete OutputFile with relativePathInSt : {}", outputFileDTO.getRelativePathInST());
        Optional<OutputFileDTO> optionalOutputFileDTO = outputFileService.findOne(outputFileDTO.getRelativePathInST());
        if (optionalOutputFileDTO.isPresent()) {
            outputFileDTO = optionalOutputFileDTO.get();
            outputFileService.delete(outputFileDTO);
        }
        else {
            String message = messageSource.getMessage("error.outputFile.outputFileNotFound", null, Locale.ENGLISH);
            log.error(message);
            throw new BadRequestAlertException(message, ENTITY_NAME, "outputFileNotFound");
        }
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, outputFileDTO.getRelativePathInST())).build();
    }
}
