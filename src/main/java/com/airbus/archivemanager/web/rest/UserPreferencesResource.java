package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.service.UserPreferencesService;
import com.airbus.archivemanager.web.rest.errors.BadRequestAlertException;
import com.airbus.archivemanager.service.dto.UserPreferencesDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.airbus.archivemanager.domain.UserPreferences}.
 */
@RestController
@RequestMapping("/api")
public class UserPreferencesResource {

    private final Logger log = LoggerFactory.getLogger(UserPreferencesResource.class);

    private static final String ENTITY_NAME = "userPreferences";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserPreferencesService userPreferencesService;

    public UserPreferencesResource(UserPreferencesService userPreferencesService) {
        this.userPreferencesService = userPreferencesService;
    }

    /**
     * {@code POST  /user-preferences} : Create a new userPreferences.
     *
     * @param userPreferencesDTO the userPreferencesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userPreferencesDTO, or with status {@code 400 (Bad Request)} if the userPreferences has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-preferences")
    public ResponseEntity<UserPreferencesDTO> createUserPreferences(@RequestBody UserPreferencesDTO userPreferencesDTO) throws URISyntaxException {
        log.info("REST request to save UserPreferences with userId : {}", userPreferencesDTO.getUserId());
        if (userPreferencesDTO.getId() != null) {
            throw new BadRequestAlertException("A new userPreferences cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserPreferencesDTO result = userPreferencesService.save(userPreferencesDTO);
        return ResponseEntity.created(new URI("/api/user-preferences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-preferences} : Updates an existing userPreferences.
     *
     * @param userPreferencesDTO the userPreferencesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userPreferencesDTO,
     * or with status {@code 400 (Bad Request)} if the userPreferencesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userPreferencesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-preferences")
    public ResponseEntity<UserPreferencesDTO> updateUserPreferences(@RequestBody UserPreferencesDTO userPreferencesDTO) throws URISyntaxException {
        log.info("REST request to update UserPreferences with id : {}", userPreferencesDTO.getId());
        if (userPreferencesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserPreferencesDTO result = userPreferencesService.save(userPreferencesDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userPreferencesDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /user-preferences} : get all the userPreferences.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userPreferences in body.
     */
    @GetMapping("/user-preferences")
    public List<UserPreferencesDTO> getAllUserPreferences() {
        log.info("REST request to get all UserPreferences");
        return userPreferencesService.findAll();
    }

    /**
     * {@code GET  /user-preferences/:id} : get the "id" userPreferences.
     *
     * @param id the id of the userPreferencesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userPreferencesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-preferences/{id}")
    public ResponseEntity<UserPreferencesDTO> getUserPreferences(@PathVariable Long id) {
        log.info("REST request to get UserPreferences with id : {}", id);
        Optional<UserPreferencesDTO> userPreferencesDTO = userPreferencesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userPreferencesDTO);
    }

    /**
     * {@code DELETE  /user-preferences/:id} : delete the "id" userPreferences.
     *
     * @param id the id of the userPreferencesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-preferences/{id}")
    public ResponseEntity<Void> deleteUserPreferences(@PathVariable Long id) {
        log.info("REST request to delete UserPreferences with id : {}", id);
        userPreferencesService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
