package com.airbus.archivemanager.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.service.TransferRetrieveService;
import com.airbus.archivemanager.service.dto.TransferRetrieveDTO;
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
 * REST controller for managing {@link com.airbus.archivemanager.domain.TransferRetrieve}.
 */
@RestController
@RequestMapping("/api")
public class TransferRetrieveResource {

    private final Logger log = LoggerFactory.getLogger(TransferRetrieveResource.class);
    private static final String ENTITY_NAME = "transferRetrieve";
    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    private final TransferRetrieveService transferRetrieveService;
    private final MessageSource messageSource;

    public TransferRetrieveResource(TransferRetrieveService transferRetrieveService, MessageSource messageSource) {
        this.transferRetrieveService = transferRetrieveService;
        this.messageSource = messageSource;
    }

    /**
     * {@code POST  /transfers} : Create a new transfer.
     *
     * @param transferRetrieveDTO the transferArchiveDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transferDTO, or with status {@code 400 (Bad Request)} if the transfer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transfer-retrieve")
     public ResponseEntity<TransferRetrieveDTO> createTransfer(@Valid @RequestBody TransferRetrieveDTO transferRetrieveDTO) throws URISyntaxException {

        log.info("REST request to save Transfer with name : {}", transferRetrieveDTO.getName());
        if (transferRetrieveDTO.getId() != null) {
             throw new BadRequestAlertException("A new transfer cannot already have an ID", ENTITY_NAME, "idexists");
        }

        TransferRetrieveDTO result = transferRetrieveService.save(transferRetrieveDTO);

        return ResponseEntity.created(new URI("/api/transfer-retrieve/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transfers} : Updates an existing transfer.
     *
     * @param transferRetrieveDTO the transferDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transferDTO,
     * or with status {@code 400 (Bad Request)} if the transferDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transferDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transfer-retrieve")
    public ResponseEntity<TransferRetrieveDTO> updateTransfer(@Valid @RequestBody TransferRetrieveDTO transferRetrieveDTO) throws URISyntaxException {
        log.info("REST request to update Transfer with id : {}", transferRetrieveDTO.getId());
        if (transferRetrieveDTO.getId() == null|| !(transferRetrieveService.findOne(transferRetrieveDTO.getId()).isPresent())) {
            String message = messageSource.getMessage("error.transferRetrieve.transferRetrieveNotFound", null, Locale.ENGLISH);
            throw new BadRequestAlertException(message, ENTITY_NAME, "transferRetrieveNotFound");
        }
        TransferRetrieveDTO result = transferRetrieveService.save(transferRetrieveDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transferRetrieveDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /transfers} : get all the transfers.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transfers in body.
     */
    @GetMapping("/transfer-retrieve")
    public ResponseEntity<List<TransferRetrieveDTO>> getAllTransfers(@PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.info("REST request to get a page of Transfers");
        Page<TransferRetrieveDTO> page = transferRetrieveService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transfers/:id} : get the "id" transfer.
     *
     * @param id the id of the transferDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transferDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transfer-retrieve/{id}")
    public ResponseEntity<TransferRetrieveDTO> getTransfer(@PathVariable Long id) {
        log.info("REST request to get Transfer with id : {}", id);
        Optional<TransferRetrieveDTO> transferRetrieveDTO = transferRetrieveService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transferRetrieveDTO);
    }

    /**
     * {@code DELETE  /transfers/:id} : delete the "id" transfer.
     *
     * @param id the id of the transferDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transfer-retrieve/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteTransfer(@PathVariable Long id) {
        log.info("REST request to delete Transfer with id : {}", id);
        transferRetrieveService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
