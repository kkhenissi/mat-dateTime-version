package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.ArchivemanagerApp;
import com.airbus.archivemanager.domain.ConfigDataSet;
import com.airbus.archivemanager.repository.ConfigDataSetRepository;
import com.airbus.archivemanager.service.ConfigDataSetQueryService;
import com.airbus.archivemanager.service.ConfigDataSetService;
import com.airbus.archivemanager.service.dto.ConfigDataSetDTO;
import com.airbus.archivemanager.service.mapper.ConfigDataSetMapper;
import com.airbus.archivemanager.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
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
 * Integration tests for the {@link ConfigDataSetResource} REST controller.
 */
@SpringBootTest(classes = ArchivemanagerApp.class)
public class ConfigDataSetResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private ConfigDataSetRepository configDataSetRepository;

    @Autowired
    private ConfigDataSetMapper configDataSetMapper;

    @Autowired
    private ConfigDataSetService configDataSetService;

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

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ConfigDataSetQueryService configDataSetQueryService;

    private MockMvc restConfigDataSetMockMvc;

    private ConfigDataSet configDataSet;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ConfigDataSetResource configDataSetResource = new ConfigDataSetResource(configDataSetService, configDataSetQueryService, messageSource);
        this.restConfigDataSetMockMvc = MockMvcBuilders.standaloneSetup(configDataSetResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConfigDataSet createEntity(EntityManager em) {
        ConfigDataSet configDataSet = new ConfigDataSet()
            .name(DEFAULT_NAME);
        return configDataSet;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConfigDataSet createUpdatedEntity(EntityManager em) {
        ConfigDataSet configDataSet = new ConfigDataSet()
            .name(UPDATED_NAME);
        return configDataSet;
    }

    @BeforeEach
    public void initTest() {
        configDataSet = createEntity(em);
    }

    @Test
    @Transactional
    public void createConfigDataSet() throws Exception {
        int databaseSizeBeforeCreate = configDataSetRepository.findAll().size();

        // Create the ConfigDataSet
        ConfigDataSetDTO configDataSetDTO = configDataSetMapper.toDto(configDataSet);
        restConfigDataSetMockMvc.perform(post("/api/config-data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(configDataSetDTO)))
            .andExpect(status().isCreated());

        // Validate the ConfigDataSet in the database
        List<ConfigDataSet> configDataSetList = configDataSetRepository.findAll();
        assertThat(configDataSetList).hasSize(databaseSizeBeforeCreate + 1);
        ConfigDataSet testConfigDataSet = configDataSetList.get(configDataSetList.size() - 1);
        assertThat(testConfigDataSet.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createConfigDataSetWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = configDataSetRepository.findAll().size();

        // Create the ConfigDataSet with an existing ID
        configDataSet.setId(1L);
        ConfigDataSetDTO configDataSetDTO = configDataSetMapper.toDto(configDataSet);

        // An entity with an existing ID cannot be created, so this API call must fail
        restConfigDataSetMockMvc.perform(post("/api/config-data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(configDataSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ConfigDataSet in the database
        List<ConfigDataSet> configDataSetList = configDataSetRepository.findAll();
        assertThat(configDataSetList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = configDataSetRepository.findAll().size();
        // set the field null
        configDataSet.setName(null);

        // Create the ConfigDataSet, which fails.
        ConfigDataSetDTO configDataSetDTO = configDataSetMapper.toDto(configDataSet);

        restConfigDataSetMockMvc.perform(post("/api/config-data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(configDataSetDTO)))
            .andExpect(status().isBadRequest());

        List<ConfigDataSet> configDataSetList = configDataSetRepository.findAll();
        assertThat(configDataSetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllConfigDataSets() throws Exception {
        // Initialize the database
        configDataSetRepository.saveAndFlush(configDataSet);

        // Get all the configDataSetList
        restConfigDataSetMockMvc.perform(get("/api/config-data-sets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(configDataSet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getConfigDataSet() throws Exception {
        // Initialize the database
        configDataSetRepository.saveAndFlush(configDataSet);

        // Get the configDataSet
        restConfigDataSetMockMvc.perform(get("/api/config-data-sets/{id}", configDataSet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(configDataSet.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingConfigDataSet() throws Exception {
        // Get the configDataSet
        restConfigDataSetMockMvc.perform(get("/api/config-data-sets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateConfigDataSet() throws Exception {
        // Initialize the database
        configDataSetRepository.saveAndFlush(configDataSet);

        int databaseSizeBeforeUpdate = configDataSetRepository.findAll().size();

        // Update the configDataSet
        ConfigDataSet updatedConfigDataSet = configDataSetRepository.findById(configDataSet.getId()).get();
        // Disconnect from session so that the updates on updatedConfigDataSet are not directly saved in db
        em.detach(updatedConfigDataSet);
        updatedConfigDataSet
            .name(UPDATED_NAME);
        ConfigDataSetDTO configDataSetDTO = configDataSetMapper.toDto(updatedConfigDataSet);

        restConfigDataSetMockMvc.perform(put("/api/config-data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(configDataSetDTO)))
            .andExpect(status().isOk());

        // Validate the ConfigDataSet in the database
        List<ConfigDataSet> configDataSetList = configDataSetRepository.findAll();
        assertThat(configDataSetList).hasSize(databaseSizeBeforeUpdate);
        ConfigDataSet testConfigDataSet = configDataSetList.get(configDataSetList.size() - 1);
        assertThat(testConfigDataSet.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingConfigDataSet() throws Exception {
        int databaseSizeBeforeUpdate = configDataSetRepository.findAll().size();

        // Create the ConfigDataSet
        ConfigDataSetDTO configDataSetDTO = configDataSetMapper.toDto(configDataSet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConfigDataSetMockMvc.perform(put("/api/config-data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(configDataSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ConfigDataSet in the database
        List<ConfigDataSet> configDataSetList = configDataSetRepository.findAll();
        assertThat(configDataSetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteConfigDataSet() throws Exception {
        // Initialize the database
        configDataSetRepository.saveAndFlush(configDataSet);

        int databaseSizeBeforeDelete = configDataSetRepository.findAll().size();

        // Delete the configDataSet
        restConfigDataSetMockMvc.perform(delete("/api/config-data-sets/{id}", configDataSet.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ConfigDataSet> configDataSetList = configDataSetRepository.findAll();
        assertThat(configDataSetList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConfigDataSet.class);
        ConfigDataSet configDataSet1 = new ConfigDataSet();
        configDataSet1.setId(1L);
        ConfigDataSet configDataSet2 = new ConfigDataSet();
        configDataSet2.setId(configDataSet1.getId());
        assertThat(configDataSet1).isEqualTo(configDataSet2);
        configDataSet2.setId(2L);
        assertThat(configDataSet1).isNotEqualTo(configDataSet2);
        configDataSet1.setId(null);
        assertThat(configDataSet1).isNotEqualTo(configDataSet2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConfigDataSetDTO.class);
        ConfigDataSetDTO configDataSetDTO1 = new ConfigDataSetDTO();
        configDataSetDTO1.setId(1L);
        ConfigDataSetDTO configDataSetDTO2 = new ConfigDataSetDTO();
        assertThat(configDataSetDTO1).isNotEqualTo(configDataSetDTO2);
        configDataSetDTO2.setId(configDataSetDTO1.getId());
        assertThat(configDataSetDTO1).isEqualTo(configDataSetDTO2);
        configDataSetDTO2.setId(2L);
        assertThat(configDataSetDTO1).isNotEqualTo(configDataSetDTO2);
        configDataSetDTO1.setId(null);
        assertThat(configDataSetDTO1).isNotEqualTo(configDataSetDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(configDataSetMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(configDataSetMapper.fromId(null)).isNull();
    }

    @Test
    @Transactional
    public void getAllConfigDataSetsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        configDataSetRepository.saveAndFlush(configDataSet);

        // Get all the scenarioList where name equals to DEFAULT_NAME
        defaultConfigDataSetShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the scenarioList where name equals to UPDATED_NAME
        defaultConfigDataSetShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllConfigDataSetsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        configDataSetRepository.saveAndFlush(configDataSet);

        // Get all the scenarioList where name in DEFAULT_NAME or UPDATED_NAME
        defaultConfigDataSetShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the scenarioList where name equals to UPDATED_NAME
        defaultConfigDataSetShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllConfigDataSetsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        configDataSetRepository.saveAndFlush(configDataSet);

        // Get all the scenarioList where name is not null
        defaultConfigDataSetShouldBeFound("name.specified=true");

        // Get all the scenarioList where name is null
        defaultConfigDataSetShouldNotBeFound("name.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultConfigDataSetShouldBeFound(String filter) throws Exception {
        restConfigDataSetMockMvc.perform(get("/api/config-data-sets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(configDataSet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restConfigDataSetMockMvc.perform(get("/api/config-data-sets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultConfigDataSetShouldNotBeFound(String filter) throws Exception {
        restConfigDataSetMockMvc.perform(get("/api/config-data-sets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restConfigDataSetMockMvc.perform(get("/api/config-data-sets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void createConfigDataSetWithNameAlreadyExistsInOtherDataSet() throws Exception {
        // initialize database
        configDataSetRepository.saveAndFlush(configDataSet);
        int databaseSizeBeforeCreate = configDataSetRepository.findAll().size();

        // Create the dataSet which fails
        ConfigDataSetDTO configDataSetDTO = new ConfigDataSetDTO();
        configDataSetDTO.setName(configDataSet.getName());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConfigDataSetMockMvc.perform(post("/api/config-data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(configDataSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DataSet in the database
        List<ConfigDataSet> configDataSetList = configDataSetRepository.findAll();
        assertThat(configDataSetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateDataSetWithNameAlreadyExistsInOtherDataSet() throws Exception {
        // initialize database with 2 rows
        configDataSetRepository.saveAndFlush(configDataSet);
        configDataSetRepository.saveAndFlush(createUpdatedEntity(em));
        int databaseSizeBeforeUpdate = configDataSetRepository.findAll().size();

        // Update the dataSet
        ConfigDataSet updatedCOnfigDataSet = configDataSetRepository.findById(configDataSet.getId()).get();
        // Disconnect from session so that the updates on updatedDataSet are not directly saved in db
        em.detach(updatedCOnfigDataSet);
        updatedCOnfigDataSet
            .name(UPDATED_NAME);
        ConfigDataSetDTO configDataSetDTO = configDataSetMapper.toDto(updatedCOnfigDataSet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConfigDataSetMockMvc.perform(put("/api/config-data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(configDataSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DataSet in the database
        List<ConfigDataSet> configDataSetList = configDataSetRepository.findAll();
        assertThat(configDataSetList).hasSize(databaseSizeBeforeUpdate);
    }
}
