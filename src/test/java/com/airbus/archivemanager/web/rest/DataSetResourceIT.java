package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.ArchivemanagerApp;
import com.airbus.archivemanager.domain.DataSet;
import com.airbus.archivemanager.repository.DataSetRepository;
import com.airbus.archivemanager.service.DataSetQueryService;
import com.airbus.archivemanager.service.DataSetService;
import com.airbus.archivemanager.service.dto.DataSetDTO;
import com.airbus.archivemanager.service.mapper.DataSetMapper;
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
 * Integration tests for the {@link DataSetResource} REST controller.
 */
@SpringBootTest(classes = ArchivemanagerApp.class)
public class DataSetResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private DataSetRepository dataSetRepository;

    @Autowired
    private DataSetMapper dataSetMapper;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private DataSetQueryService dataSetQueryService;

    @Autowired
    private MessageSource messageSource;

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

    private MockMvc restDataSetMockMvc;

    private DataSet dataSet;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DataSetResource dataSetResource = new DataSetResource(dataSetService, messageSource, dataSetQueryService);
        this.restDataSetMockMvc = MockMvcBuilders.standaloneSetup(dataSetResource)
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
    public static DataSet createEntity(EntityManager em) {
        DataSet dataSet = new DataSet()
            .name(DEFAULT_NAME);
        return dataSet;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DataSet createUpdatedEntity(EntityManager em) {
        DataSet dataSet = new DataSet()
            .name(UPDATED_NAME);
        return dataSet;
    }

    @BeforeEach
    public void initTest() {
        dataSet = createEntity(em);
    }

    @Test
    @Transactional
    public void createDataSet() throws Exception {
        int databaseSizeBeforeCreate = dataSetRepository.findAll().size();

        // Create the DataSet
        DataSetDTO dataSetDTO = dataSetMapper.toDto(dataSet);
        restDataSetMockMvc.perform(post("/api/data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSetDTO)))
            .andExpect(status().isCreated());

        // Validate the DataSet in the database
        List<DataSet> dataSetList = dataSetRepository.findAll();
        assertThat(dataSetList).hasSize(databaseSizeBeforeCreate + 1);
        DataSet testDataSet = dataSetList.get(dataSetList.size() - 1);
        assertThat(testDataSet.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createDataSetWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dataSetRepository.findAll().size();

        // Create the DataSet with an existing ID
        dataSet.setId(1L);
        DataSetDTO dataSetDTO = dataSetMapper.toDto(dataSet);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDataSetMockMvc.perform(post("/api/data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DataSet in the database
        List<DataSet> dataSetList = dataSetRepository.findAll();
        assertThat(dataSetList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dataSetRepository.findAll().size();
        // set the field null
        dataSet.setName(null);

        // Create the DataSet, which fails.
        DataSetDTO dataSetDTO = dataSetMapper.toDto(dataSet);

        restDataSetMockMvc.perform(post("/api/data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSetDTO)))
            .andExpect(status().isBadRequest());

        List<DataSet> dataSetList = dataSetRepository.findAll();
        assertThat(dataSetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDataSets() throws Exception {
        // Initialize the database
        dataSetRepository.saveAndFlush(dataSet);

        // Get all the dataSetList
        restDataSetMockMvc.perform(get("/api/data-sets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dataSet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getDataSet() throws Exception {
        // Initialize the database
        dataSetRepository.saveAndFlush(dataSet);

        // Get the dataSet
        restDataSetMockMvc.perform(get("/api/data-sets/{id}", dataSet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(dataSet.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDataSet() throws Exception {
        // Get the dataSet
        restDataSetMockMvc.perform(get("/api/data-sets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDataSet() throws Exception {
        // Initialize the database
        dataSetRepository.saveAndFlush(dataSet);

        int databaseSizeBeforeUpdate = dataSetRepository.findAll().size();

        // Update the dataSet
        DataSet updatedDataSet = dataSetRepository.findById(dataSet.getId()).get();
        // Disconnect from session so that the updates on updatedDataSet are not directly saved in db
        em.detach(updatedDataSet);
        updatedDataSet
            .name(UPDATED_NAME);
        DataSetDTO dataSetDTO = dataSetMapper.toDto(updatedDataSet);

        restDataSetMockMvc.perform(put("/api/data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSetDTO)))
            .andExpect(status().isOk());

        // Validate the DataSet in the database
        List<DataSet> dataSetList = dataSetRepository.findAll();
        assertThat(dataSetList).hasSize(databaseSizeBeforeUpdate);
        DataSet testDataSet = dataSetList.get(dataSetList.size() - 1);
        assertThat(testDataSet.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingDataSet() throws Exception {
        int databaseSizeBeforeUpdate = dataSetRepository.findAll().size();

        // Create the DataSet
        DataSetDTO dataSetDTO = dataSetMapper.toDto(dataSet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDataSetMockMvc.perform(put("/api/data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DataSet in the database
        List<DataSet> dataSetList = dataSetRepository.findAll();
        assertThat(dataSetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteDataSet() throws Exception {
        // Initialize the database
        dataSetRepository.saveAndFlush(dataSet);

        int databaseSizeBeforeDelete = dataSetRepository.findAll().size();

        // Delete the dataSet
        restDataSetMockMvc.perform(delete("/api/data-sets/{id}", dataSet.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DataSet> dataSetList = dataSetRepository.findAll();
        assertThat(dataSetList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DataSet.class);
        DataSet dataSet1 = new DataSet();
        dataSet1.setId(1L);
        DataSet dataSet2 = new DataSet();
        dataSet2.setId(dataSet1.getId());
        assertThat(dataSet1).isEqualTo(dataSet2);
        dataSet2.setId(2L);
        assertThat(dataSet1).isNotEqualTo(dataSet2);
        dataSet1.setId(null);
        assertThat(dataSet1).isNotEqualTo(dataSet2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DataSetDTO.class);
        DataSetDTO dataSetDTO1 = new DataSetDTO();
        dataSetDTO1.setId(1L);
        DataSetDTO dataSetDTO2 = new DataSetDTO();
        assertThat(dataSetDTO1).isNotEqualTo(dataSetDTO2);
        dataSetDTO2.setId(dataSetDTO1.getId());
        assertThat(dataSetDTO1).isEqualTo(dataSetDTO2);
        dataSetDTO2.setId(2L);
        assertThat(dataSetDTO1).isNotEqualTo(dataSetDTO2);
        dataSetDTO1.setId(null);
        assertThat(dataSetDTO1).isNotEqualTo(dataSetDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(dataSetMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(dataSetMapper.fromId(null)).isNull();
    }

    @Test
    @Transactional
    public void getAllDataSetByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        dataSetRepository.saveAndFlush(dataSet);

        // Get all the scenarioList where name equals to DEFAULT_NAME
        defaultDataSetShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the scenarioList where name equals to UPDATED_NAME
        defaultDataSetShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllDataSetByNameIsInShouldWork() throws Exception {
        // Initialize the database
        dataSetRepository.saveAndFlush(dataSet);

        // Get all the scenarioList where name in DEFAULT_NAME or UPDATED_NAME
        defaultDataSetShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the scenarioList where name equals to UPDATED_NAME
        defaultDataSetShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllDataSetByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        dataSetRepository.saveAndFlush(dataSet);

        // Get all the scenarioList where name is not null
        defaultDataSetShouldBeFound("name.specified=true");

        // Get all the scenarioList where name is null
        defaultDataSetShouldNotBeFound("name.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDataSetShouldBeFound(String filter) throws Exception {
        restDataSetMockMvc.perform(get("/api/data-sets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dataSet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restDataSetMockMvc.perform(get("/api/data-sets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDataSetShouldNotBeFound(String filter) throws Exception {
        restDataSetMockMvc.perform(get("/api/data-sets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDataSetMockMvc.perform(get("/api/data-sets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void createDataSetWithNameAlreadyExistsInOtherDataSet() throws Exception {
        // initialize database
        dataSetRepository.saveAndFlush(dataSet);
        int databaseSizeBeforeCreate = dataSetRepository.findAll().size();

        // Create the dataSet which fails
        DataSetDTO dataSetDTO = new DataSetDTO();
        dataSetDTO.setName(dataSet.getName());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDataSetMockMvc.perform(post("/api/data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DataSet in the database
        List<DataSet> dataSetList = dataSetRepository.findAll();
        assertThat(dataSetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateDataSetWithNameAlreadyExistsInOtherDataSet() throws Exception {
        // initialize database with 2 rows
        dataSetRepository.saveAndFlush(dataSet);
        dataSetRepository.saveAndFlush(createUpdatedEntity(em));
        int databaseSizeBeforeUpdate = dataSetRepository.findAll().size();

        // Update the dataSet
        DataSet updatedDataSet = dataSetRepository.findById(dataSet.getId()).get();
        // Disconnect from session so that the updates on updatedDataSet are not directly saved in db
        em.detach(updatedDataSet);
        updatedDataSet
            .name(UPDATED_NAME);
        DataSetDTO dataSetDTO = dataSetMapper.toDto(updatedDataSet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDataSetMockMvc.perform(put("/api/data-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DataSet in the database
        List<DataSet> dataSetList = dataSetRepository.findAll();
        assertThat(dataSetList).hasSize(databaseSizeBeforeUpdate);
    }
}
