package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.ArchivemanagerApp;
import com.airbus.archivemanager.domain.Run;
import com.airbus.archivemanager.domain.ToolVersion;
import com.airbus.archivemanager.domain.User;
import com.airbus.archivemanager.repository.ToolVersionRepository;
import com.airbus.archivemanager.repository.UserRepository;
import com.airbus.archivemanager.service.ToolVersionQueryService;
import com.airbus.archivemanager.service.ToolVersionService;
import com.airbus.archivemanager.service.UserService;
import com.airbus.archivemanager.service.dto.ToolVersionDTO;
import com.airbus.archivemanager.service.mapper.ToolVersionMapper;
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
 * Integration tests for the {@link ToolVersionResource} REST controller.
 */
@SpringBootTest(classes = ArchivemanagerApp.class)
public class ToolVersionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    @Autowired
    private ToolVersionRepository toolVersionRepository;

    @Autowired
    private ToolVersionMapper toolVersionMapper;

    @Autowired
    private ToolVersionService toolVersionService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ToolVersionQueryService toolVersionQueryService;

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

    private MockMvc restToolVersionMockMvc;

    private ToolVersion toolVersion;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ToolVersionResource toolVersionResource = new ToolVersionResource(toolVersionService, toolVersionQueryService);
        this.restToolVersionMockMvc = MockMvcBuilders.standaloneSetup(toolVersionResource)
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
    public static ToolVersion createEntity(EntityManager em) {
        ToolVersion toolVersion = new ToolVersion()
            .name(DEFAULT_NAME)
            .version(DEFAULT_VERSION);
        return toolVersion;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ToolVersion createUpdatedEntity(EntityManager em) {
        ToolVersion toolVersion = new ToolVersion()
            .name(UPDATED_NAME)
            .version(UPDATED_VERSION);
        return toolVersion;
    }

    @BeforeEach
    public void initTest() {
        toolVersion = createEntity(em);
    }

    @Test
    @Transactional
    public void createToolVersion() throws Exception {
        int databaseSizeBeforeCreate = toolVersionRepository.findAll().size();

        // Create the ToolVersion
        ToolVersionDTO toolVersionDTO = toolVersionMapper.toDto(toolVersion);
        restToolVersionMockMvc.perform(post("/api/tool-versions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toolVersionDTO)))
            .andExpect(status().isCreated());

        // Validate the ToolVersion in the database
        List<ToolVersion> toolVersionList = toolVersionRepository.findAll();
        assertThat(toolVersionList).hasSize(databaseSizeBeforeCreate + 1);
        ToolVersion testToolVersion = toolVersionList.get(toolVersionList.size() - 1);
        assertThat(testToolVersion.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testToolVersion.getVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    @Transactional
    public void createToolVersionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = toolVersionRepository.findAll().size();

        // Create the ToolVersion with an existing ID
        toolVersion.setId(1L);
        ToolVersionDTO toolVersionDTO = toolVersionMapper.toDto(toolVersion);

        // An entity with an existing ID cannot be created, so this API call must fail
        restToolVersionMockMvc.perform(post("/api/tool-versions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toolVersionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ToolVersion in the database
        List<ToolVersion> toolVersionList = toolVersionRepository.findAll();
        assertThat(toolVersionList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = toolVersionRepository.findAll().size();
        // set the field null
        toolVersion.setName(null);

        // Create the ToolVersion, which fails.
        ToolVersionDTO toolVersionDTO = toolVersionMapper.toDto(toolVersion);

        restToolVersionMockMvc.perform(post("/api/tool-versions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toolVersionDTO)))
            .andExpect(status().isBadRequest());

        List<ToolVersion> toolVersionList = toolVersionRepository.findAll();
        assertThat(toolVersionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = toolVersionRepository.findAll().size();
        // set the field null
        toolVersion.setVersion(null);

        // Create the ToolVersion, which fails.
        ToolVersionDTO toolVersionDTO = toolVersionMapper.toDto(toolVersion);

        restToolVersionMockMvc.perform(post("/api/tool-versions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toolVersionDTO)))
            .andExpect(status().isBadRequest());

        List<ToolVersion> toolVersionList = toolVersionRepository.findAll();
        assertThat(toolVersionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllToolVersions() throws Exception {
        // Initialize the database
        toolVersionRepository.saveAndFlush(toolVersion);

        // Get all the toolVersionList
        restToolVersionMockMvc.perform(get("/api/tool-versions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toolVersion.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())));
    }

    @Test
    @Transactional
    public void getToolVersion() throws Exception {
        // Initialize the database
        toolVersionRepository.saveAndFlush(toolVersion);

        // Get the toolVersion
        restToolVersionMockMvc.perform(get("/api/tool-versions/{id}", toolVersion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(toolVersion.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()));
    }

    @Test
    @Transactional
    public void getAllToolVersionsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        toolVersionRepository.saveAndFlush(toolVersion);

        // Get all the toolVersionList where name equals to DEFAULT_NAME
        defaultToolVersionShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the toolVersionList where name equals to UPDATED_NAME
        defaultToolVersionShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllToolVersionsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        toolVersionRepository.saveAndFlush(toolVersion);

        // Get all the toolVersionList where name in DEFAULT_NAME or UPDATED_NAME
        defaultToolVersionShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the toolVersionList where name equals to UPDATED_NAME
        defaultToolVersionShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllToolVersionsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        toolVersionRepository.saveAndFlush(toolVersion);

        // Get all the toolVersionList where name is not null
        defaultToolVersionShouldBeFound("name.specified=true");

        // Get all the toolVersionList where name is null
        defaultToolVersionShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllToolVersionsByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        toolVersionRepository.saveAndFlush(toolVersion);

        // Get all the toolVersionList where version equals to DEFAULT_VERSION
        defaultToolVersionShouldBeFound("version.equals=" + DEFAULT_VERSION);

        // Get all the toolVersionList where version equals to UPDATED_VERSION
        defaultToolVersionShouldNotBeFound("version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllToolVersionsByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        toolVersionRepository.saveAndFlush(toolVersion);

        // Get all the toolVersionList where version in DEFAULT_VERSION or UPDATED_VERSION
        defaultToolVersionShouldBeFound("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION);

        // Get all the toolVersionList where version equals to UPDATED_VERSION
        defaultToolVersionShouldNotBeFound("version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void getAllToolVersionsByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        toolVersionRepository.saveAndFlush(toolVersion);

        // Get all the toolVersionList where version is not null
        defaultToolVersionShouldBeFound("version.specified=true");

        // Get all the toolVersionList where version is null
        defaultToolVersionShouldNotBeFound("version.specified=false");
    }

    @Test
    @Transactional
    public void getAllToolVersionsByRunIsEqualToSomething() throws Exception {
        // Initialize the owner
        User owner = UserResourceIT.createOwner(em, userRepository, userService);

        // Initialize the database
        toolVersionRepository.saveAndFlush(toolVersion);
        Run run = RunResourceIT.createEntity(em);
        run.setOwner(owner);
        run.getToolVersions().add(toolVersion);
        em.persist(run);
        em.flush();
        toolVersion.getRuns().add(run);
        toolVersionRepository.saveAndFlush(toolVersion);
        Long runId = run.getId();

        // Get all the toolVersionList where run equals to runId
        defaultToolVersionShouldBeFound("runId.equals=" + runId);

        // Get all the toolVersionList where run equals to runId + 1
        defaultToolVersionShouldNotBeFound("runId.equals=" + (runId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultToolVersionShouldBeFound(String filter) throws Exception {
        restToolVersionMockMvc.perform(get("/api/tool-versions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toolVersion.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));

        // Check, that the count call also returns 1
        restToolVersionMockMvc.perform(get("/api/tool-versions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultToolVersionShouldNotBeFound(String filter) throws Exception {
        restToolVersionMockMvc.perform(get("/api/tool-versions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restToolVersionMockMvc.perform(get("/api/tool-versions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingToolVersion() throws Exception {
        // Get the toolVersion
        restToolVersionMockMvc.perform(get("/api/tool-versions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateToolVersion() throws Exception {
        // Initialize the database
        toolVersionRepository.saveAndFlush(toolVersion);

        int databaseSizeBeforeUpdate = toolVersionRepository.findAll().size();

        // Update the toolVersion
        ToolVersion updatedToolVersion = toolVersionRepository.findById(toolVersion.getId()).get();
        // Disconnect from session so that the updates on updatedToolVersion are not directly saved in db
        em.detach(updatedToolVersion);
        updatedToolVersion
            .name(UPDATED_NAME)
            .version(UPDATED_VERSION);
        ToolVersionDTO toolVersionDTO = toolVersionMapper.toDto(updatedToolVersion);

        restToolVersionMockMvc.perform(put("/api/tool-versions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toolVersionDTO)))
            .andExpect(status().isOk());

        // Validate the ToolVersion in the database
        List<ToolVersion> toolVersionList = toolVersionRepository.findAll();
        assertThat(toolVersionList).hasSize(databaseSizeBeforeUpdate);
        ToolVersion testToolVersion = toolVersionList.get(toolVersionList.size() - 1);
        assertThat(testToolVersion.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testToolVersion.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    @Transactional
    public void updateNonExistingToolVersion() throws Exception {
        int databaseSizeBeforeUpdate = toolVersionRepository.findAll().size();

        // Create the ToolVersion
        ToolVersionDTO toolVersionDTO = toolVersionMapper.toDto(toolVersion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restToolVersionMockMvc.perform(put("/api/tool-versions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toolVersionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ToolVersion in the database
        List<ToolVersion> toolVersionList = toolVersionRepository.findAll();
        assertThat(toolVersionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteToolVersion() throws Exception {
        // Initialize the database
        toolVersionRepository.saveAndFlush(toolVersion);

        int databaseSizeBeforeDelete = toolVersionRepository.findAll().size();

        // Delete the toolVersion
        restToolVersionMockMvc.perform(delete("/api/tool-versions/{id}", toolVersion.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ToolVersion> toolVersionList = toolVersionRepository.findAll();
        assertThat(toolVersionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ToolVersion.class);
        ToolVersion toolVersion1 = new ToolVersion();
        toolVersion1.setId(1L);
        ToolVersion toolVersion2 = new ToolVersion();
        toolVersion2.setId(toolVersion1.getId());
        assertThat(toolVersion1).isEqualTo(toolVersion2);
        toolVersion2.setId(2L);
        assertThat(toolVersion1).isNotEqualTo(toolVersion2);
        toolVersion1.setId(null);
        assertThat(toolVersion1).isNotEqualTo(toolVersion2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ToolVersionDTO.class);
        ToolVersionDTO toolVersionDTO1 = new ToolVersionDTO();
        toolVersionDTO1.setId(1L);
        ToolVersionDTO toolVersionDTO2 = new ToolVersionDTO();
        assertThat(toolVersionDTO1).isNotEqualTo(toolVersionDTO2);
        toolVersionDTO2.setId(toolVersionDTO1.getId());
        assertThat(toolVersionDTO1).isEqualTo(toolVersionDTO2);
        toolVersionDTO2.setId(2L);
        assertThat(toolVersionDTO1).isNotEqualTo(toolVersionDTO2);
        toolVersionDTO1.setId(null);
        assertThat(toolVersionDTO1).isNotEqualTo(toolVersionDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(toolVersionMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(toolVersionMapper.fromId(null)).isNull();
    }
}
