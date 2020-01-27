package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.service.UploadQueryService;
import com.airbus.archivemanager.service.UploadService;
import com.airbus.archivemanager.service.dto.UploadCriteria;
import com.airbus.archivemanager.service.dto.UploadDTO;
import com.airbus.archivemanager.service.util.FilesUtil;
import com.airbus.archivemanager.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for managing {@link UploadDTO}.
 */
@RestController
@RequestMapping("/api")
public class UploadResource {

    private static final String ENTITY_NAME = "uploadResource";
    private final Logger log = LoggerFactory.getLogger(UploadResource.class);
    private final MessageSource messageSource;
    private final UploadService uploadService;
    private final UploadQueryService uploadQueryService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public UploadResource(MessageSource messageSource, UploadService uploadService, UploadQueryService uploadQueryService) {
        this.messageSource = messageSource;
        this.uploadService = uploadService;
        this.uploadQueryService = uploadQueryService;
    }

    /**
     * {@code POST  /uploads} : Create a new upload.
     *
     * @param uploadDTO the uploadDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uploadDTO,
     * or with status {@code 400 (Bad Request)} if the RelativePathInST is null,
     * or with status {@code 400 (Bad Request)} if the file already exists in LTS.
     */
    @ApiOperation(value = "copy a file from local to LTS")
    @PostMapping("/upload")
    public ResponseEntity<UploadDTO> upload(@RequestBody UploadDTO uploadDTO) {
        log.info("REST request to storage a file from local to LTS from a local path: {}", uploadDTO.getLocalPath());
        if (uploadDTO.getRelativePathInST() == null) {
            log.error("RelativePathInST must not be null");
            String message = messageSource.getMessage("error.relativePathInSTNull", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "relativePathInSTNull");
        }
        if (uploadDTO.getInputType() != null  && uploadDTO.getRunId() != null) {
            String message = messageSource.getMessage("error.inconsistencyData", null, Locale.ENGLISH);
            log.error(message);
            throw new BadRequestAlertException(message, ENTITY_NAME, "inconsistencyData");
        }
        if (uploadDTO.getInputType() != null && !uploadDTO.getInputType().equals("CONFIG") && !uploadDTO.getInputType().equals("INPUT") && uploadDTO.getRunId() == null) {
            String message = messageSource.getMessage("error.onlyCONFIGOrINPUT", null, Locale.ENGLISH);
            log.error(message);
            throw new BadRequestAlertException(message, ENTITY_NAME, "onlyCONFIGOrINPUT");
        }
        String lTSPathString = FilesUtil.createCompletePathInLT(uploadDTO.getRelativePathInST(), messageSource, log);
        //check if folders need to be created
        final Set<PosixFilePermission> ownerWritable = PosixFilePermissions.fromString("rwxrwxr-x");
        final FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(ownerWritable);
        try {
            FilesUtil.createDirectory(lTSPathString, permissions);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        String fileName = Paths.get(uploadDTO.getLocalPath()).getFileName().toString();
        File file = new File(Paths.get(lTSPathString).getParent().toString(), fileName);
        FilesUtil.checkIfFileAlreadyExistsInLTS(file, messageSource, log);
        uploadDTO.setlTSPath(lTSPathString);
        return ResponseEntity.ok().body(uploadService.uploadFile(uploadDTO));
    }

    /**
     * {@code GET  /upload} : get all the uploads.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dataSets in body.
     */
    @GetMapping("/upload")
    public ResponseEntity<List<UploadDTO>> getAllUploads(UploadCriteria criteria, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.info("REST request to get Uploads by criteria: {}", criteria);
        Page<UploadDTO> page = uploadQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /upload/count} : count all the Uploads.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/upload/count")
    public ResponseEntity<Long> countUpload(UploadCriteria criteria) {
        log.info("REST request to count Uploads by criteria: {}", criteria);
        return ResponseEntity.ok().body(uploadQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /upload/:id} : get the "id" upload.
     *
     * @param id the id of the uploadDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uploadDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/upload/{id}")
    public ResponseEntity<UploadDTO> getUpload(@PathVariable Long id) {
        log.info("REST request to get Upload with id : {}", id);
        Optional<UploadDTO> uploadDTO = uploadService.findOne(id);
        return ResponseUtil.wrapOrNotFound(uploadDTO);
    }

    /**
     * {@code DELETE  /upload/:id} : delete the "id" upload.
     *
     * @param id the id of the uploadDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/upload/{id}")
    public ResponseEntity<Void> deleteUpload(@PathVariable Long id) {
        log.info("REST request to delete Upload with id : {}", id);
        uploadService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
