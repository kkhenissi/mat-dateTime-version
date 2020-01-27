package com.airbus.archivemanager.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.service.TransferArchiveService;
import com.airbus.archivemanager.service.dto.TransferArchiveDTO;
import com.airbus.archivemanager.web.rest.errors.BadRequestAlertException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.airbus.archivemanager.domain.TransferArchive}.
 */
@RestController
@RequestMapping("/api")
public class TransferArchiveResource {

    private final Logger log = LoggerFactory.getLogger(TransferArchiveResource.class);
    private static final String ENTITY_NAME = "transferArchive";
    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    private final TransferArchiveService transferArchiveService;
    private final MessageSource messageSource;

    public TransferArchiveResource(TransferArchiveService transferArchiveService, MessageSource messageSource) {
        this.transferArchiveService = transferArchiveService;
        this.messageSource = messageSource;
    }

    /**
     * {@code POST  /transfers} : Create a new transfer.
     *
     * @param transferArchiveDTO the transferArchiveDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transferDTO, or with status {@code 400 (Bad Request)} if the transfer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transfer-archive")
     public ResponseEntity<TransferArchiveDTO> createTransfer(@Valid @RequestBody TransferArchiveDTO transferArchiveDTO) throws URISyntaxException {

        log.info("REST request to save Transfer with name : {}", transferArchiveDTO.getName());
        if (transferArchiveDTO.getId() != null) {
             throw new BadRequestAlertException("A new transfer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        // transferArchiveDTO.setStatus(TransferStatus.IN_EDITION);
        TransferArchiveDTO result = transferArchiveService.save(transferArchiveDTO);

        return ResponseEntity.created(new URI("/api/transfer-archive/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transfers} : Updates an existing transfer.
     *
     * @param transferArchiveDTO the transferDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transferDTO,
     * or with status {@code 400 (Bad Request)} if the transferDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transferDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transfer-archive")
    public ResponseEntity<TransferArchiveDTO> updateTransfer(@Valid @RequestBody TransferArchiveDTO transferArchiveDTO) throws URISyntaxException {
        log.info("REST request to update Transfer with id : {}", transferArchiveDTO.getId());
        if (transferArchiveDTO.getId() == null|| !(transferArchiveService.findOne(transferArchiveDTO.getId()).isPresent())) {
            String message = messageSource.getMessage("error.transferArchive.transferArchiveNotFound", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "transferArchiveNotFound");
        }
        TransferArchiveDTO result = transferArchiveService.save(transferArchiveDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transferArchiveDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /transfers} : get all the transfers.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transfers in body.
     */
    @GetMapping("/transfer-archive")
    public ResponseEntity<List<TransferArchiveDTO>> getAllTransfers(@PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.info("REST request to get a page of Transfers");
        Page<TransferArchiveDTO> page = transferArchiveService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transfers/:id} : get the "id" transfer.
     *
     * @param id the id of the transferDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transferDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transfer-archive/{id}")
    public ResponseEntity<TransferArchiveDTO> getTransfer(@PathVariable Long id) {
        log.info("REST request to get Transfer with id : {}", id);
        Optional<TransferArchiveDTO> transferArchiveDTO = transferArchiveService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transferArchiveDTO);
    }

    /**
     * {@code DELETE  /transfers/:id} : delete the "id" transfer.
     *
     * @param id the id of the transferDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transfer-archive/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteTransfer(@PathVariable Long id) {
        log.info("REST request to delete Transfer with id : {}", id);
        transferArchiveService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
