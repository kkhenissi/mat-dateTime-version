package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.ArchivemanagerApp;
import com.airbus.archivemanager.domain.UserPreferences;
import com.airbus.archivemanager.repository.UserPreferencesRepository;
import com.airbus.archivemanager.service.UserPreferencesService;
import com.airbus.archivemanager.service.dto.UserPreferencesDTO;
import com.airbus.archivemanager.service.mapper.UserPreferencesMapper;
import com.airbus.archivemanager.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.airbus.archivemanager.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link UserPreferencesResource} REST controller.
 */
@SpringBootTest(classes = ArchivemanagerApp.class)
public class UserPreferencesResourceIT {

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private UserPreferencesMapper userPreferencesMapper;

    @Autowired
    private UserPreferencesService userPreferencesService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restUserPreferencesMockMvc;

    private UserPreferences userPreferences;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserPreferencesResource userPreferencesResource = new UserPreferencesResource(userPreferencesService);
        this.restUserPreferencesMockMvc = MockMvcBuilders.standaloneSetup(userPreferencesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserPreferences createEntity(EntityManager em) {
        UserPreferences userPreferences = new UserPreferences();
        return userPreferences;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserPreferences createUpdatedEntity(EntityManager em) {
        UserPreferences userPreferences = new UserPreferences();
        return userPreferences;
    }

    @BeforeEach
    public void initTest() {
        userPreferences = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserPreferences() throws Exception {
        int databaseSizeBeforeCreate = userPreferencesRepository.findAll().size();

        // Create the UserPreferences
        UserPreferencesDTO userPreferencesDTO = userPreferencesMapper.toDto(userPreferences);
        restUserPreferencesMockMvc.perform(post("/api/user-preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userPreferencesDTO)))
            .andExpect(status().isCreated());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeCreate + 1);
        UserPreferences testUserPreferences = userPreferencesList.get(userPreferencesList.size() - 1);
    }

    @Test
    @Transactional
    public void createUserPreferencesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userPreferencesRepository.findAll().size();

        // Create the UserPreferences with an existing ID
        userPreferences.setId(1L);
        UserPreferencesDTO userPreferencesDTO = userPreferencesMapper.toDto(userPreferences);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserPreferencesMockMvc.perform(post("/api/user-preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userPreferencesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllUserPreferences() throws Exception {
        // Initialize the database
        userPreferencesRepository.saveAndFlush(userPreferences);

        // Get all the userPreferencesList
        restUserPreferencesMockMvc.perform(get("/api/user-preferences?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userPreferences.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getUserPreferences() throws Exception {
        // Initialize the database
        userPreferencesRepository.saveAndFlush(userPreferences);

        // Get the userPreferences
        restUserPreferencesMockMvc.perform(get("/api/user-preferences/{id}", userPreferences.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userPreferences.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingUserPreferences() throws Exception {
        // Get the userPreferences
        restUserPreferencesMockMvc.perform(get("/api/user-preferences/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserPreferences() throws Exception {
        // Initialize the database
        userPreferencesRepository.saveAndFlush(userPreferences);

        int databaseSizeBeforeUpdate = userPreferencesRepository.findAll().size();

        // Update the userPreferences
        UserPreferences updatedUserPreferences = userPreferencesRepository.findById(userPreferences.getId()).get();
        // Disconnect from session so that the updates on updatedUserPreferences are not directly saved in db
        em.detach(updatedUserPreferences);
        UserPreferencesDTO userPreferencesDTO = userPreferencesMapper.toDto(updatedUserPreferences);

        restUserPreferencesMockMvc.perform(put("/api/user-preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userPreferencesDTO)))
            .andExpect(status().isOk());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeUpdate);
        UserPreferences testUserPreferences = userPreferencesList.get(userPreferencesList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingUserPreferences() throws Exception {
        int databaseSizeBeforeUpdate = userPreferencesRepository.findAll().size();

        // Create the UserPreferences
        UserPreferencesDTO userPreferencesDTO = userPreferencesMapper.toDto(userPreferences);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserPreferencesMockMvc.perform(put("/api/user-preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userPreferencesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUserPreferences() throws Exception {
        // Initialize the database
        userPreferencesRepository.saveAndFlush(userPreferences);

        int databaseSizeBeforeDelete = userPreferencesRepository.findAll().size();

        // Delete the userPreferences
        restUserPreferencesMockMvc.perform(delete("/api/user-preferences/{id}", userPreferences.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserPreferences.class);
        UserPreferences userPreferences1 = new UserPreferences();
        userPreferences1.setId(1L);
        UserPreferences userPreferences2 = new UserPreferences();
        userPreferences2.setId(userPreferences1.getId());
        assertThat(userPreferences1).isEqualTo(userPreferences2);
        userPreferences2.setId(2L);
        assertThat(userPreferences1).isNotEqualTo(userPreferences2);
        userPreferences1.setId(null);
        assertThat(userPreferences1).isNotEqualTo(userPreferences2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserPreferencesDTO.class);
        UserPreferencesDTO userPreferencesDTO1 = new UserPreferencesDTO();
        userPreferencesDTO1.setId(1L);
        UserPreferencesDTO userPreferencesDTO2 = new UserPreferencesDTO();
        assertThat(userPreferencesDTO1).isNotEqualTo(userPreferencesDTO2);
        userPreferencesDTO2.setId(userPreferencesDTO1.getId());
        assertThat(userPreferencesDTO1).isEqualTo(userPreferencesDTO2);
        userPreferencesDTO2.setId(2L);
        assertThat(userPreferencesDTO1).isNotEqualTo(userPreferencesDTO2);
        userPreferencesDTO1.setId(null);
        assertThat(userPreferencesDTO1).isNotEqualTo(userPreferencesDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(userPreferencesMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(userPreferencesMapper.fromId(null)).isNull();
    }
}
