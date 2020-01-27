package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.ArchivemanagerApp;
import com.airbus.archivemanager.domain.Run;
import com.airbus.archivemanager.domain.Scenario;
import com.airbus.archivemanager.domain.ToolVersion;
import com.airbus.archivemanager.domain.User;
import com.airbus.archivemanager.domain.enumeration.RunStatus;
import com.airbus.archivemanager.repository.RunRepository;
import com.airbus.archivemanager.repository.UserRepository;
import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.service.*;
import com.airbus.archivemanager.service.dto.RunDTO;
import com.airbus.archivemanager.service.mapper.RunMapper;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.airbus.archivemanager.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link RunResource} REST controller.
 */
@SpringBootTest(classes = ArchivemanagerApp.class)
public class RunResourceIT {

    private static final LocalDateTime DEFAULT_START_DATE = LocalDateTime.of(1980, 1, 1, 1, 1, 1);
    private static final LocalDateTime UPDATED_START_DATE = LocalDateTime.of(1990, 1, 1, 1, 1, 1);
    private static final LocalDateTime SMALLER_START_DATE = LocalDateTime.of(1970, 1, 1, 1, 1, 1);

    private static final LocalDateTime DEFAULT_END_DATE = LocalDateTime.of(1980, 1, 1, 1, 1, 1);
    private static final LocalDateTime UPDATED_END_DATE = LocalDateTime.of(1990, 1, 1, 1, 1, 1);
    private static final LocalDateTime SMALLER_END_DATE = LocalDateTime.of(1970, 1, 1, 1, 1, 1);

    private static final RunStatus DEFAULT_STATUS = RunStatus.UNKNOWN;
    private static final RunStatus UPDATED_STATUS = RunStatus.READY;

    private static final String DEFAULT_PLATFORM_HARDWARE_INFO = "AAAAAAAAAA";
    private static final String UPDATED_PLATFORM_HARDWARE_INFO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";


    @Autowired
    private RunRepository runRepository;

    @Autowired
    private RunMapper runMapper;

    @Autowired
    private RunService runService;

    @Autowired
    private RunQueryService runQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private UserService userService;

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    private ToolVersionService toolVersionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restRunMockMvc;

    private Run run;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Run createEntity(EntityManager em) {
        Run run = new Run()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .status(DEFAULT_STATUS)
            .platformHardwareInfo(DEFAULT_PLATFORM_HARDWARE_INFO)
            .description(DEFAULT_DESCRIPTION);
        return run;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Run createUpdatedEntity(EntityManager em) {
        Run run = new Run()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .platformHardwareInfo(UPDATED_PLATFORM_HARDWARE_INFO)
            .description(UPDATED_DESCRIPTION);
        return run;
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RunResource runResource = new RunResource(runService, runQueryService,messageSource, scenarioService, toolVersionService);
        this.restRunMockMvc = MockMvcBuilders.standaloneSetup(runResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    @BeforeEach
    public void initTest() {
        // Initialize the owner
        User owner = UserResourceIT.createOwner(em, userRepository, userService);

        // Initialize the scenario
        Scenario scenario = ScenarioResourceIT.createEntity(em);
        scenario.setOwner(owner);
        em.persist(scenario);
        em.flush();
        run = createEntity(em);
        run.setScenario(scenario);
        run.setOwner(owner);
    }

    @Test
    @Transactional
    public void createRun() throws Exception {
        int databaseSizeBeforeCreate = runRepository.findAll().size();

        // Create the Run
        RunDTO runDTO = runMapper.toDto(run);

        restRunMockMvc.perform(post("/api/runs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(runDTO)))
            .andExpect(status().isCreated());

        // Validate the Run in the database
        List<Run> runList = runRepository.findAll();
        assertThat(runList).hasSize(databaseSizeBeforeCreate + 1);
        Run testRun = runList.get(runList.size() - 1);
        assertThat(testRun.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testRun.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testRun.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRun.getPlatformHardwareInfo()).isEqualTo(DEFAULT_PLATFORM_HARDWARE_INFO);
        assertThat(testRun.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createRunWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = runRepository.findAll().size();

        // Create the Run with an existing ID
        run.setId(1L);
        RunDTO runDTO = runMapper.toDto(run);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRunMockMvc.perform(post("/api/runs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(runDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Run in the database
        List<Run> runList = runRepository.findAll();
        assertThat(runList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = runRepository.findAll().size();
        // set the field null
        run.setStartDate(null);
        // Create the Run, which fails.
        RunDTO runDTO = runMapper.toDto(run);

        restRunMockMvc.perform(post("/api/runs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(runDTO)))
            .andExpect(status().isBadRequest());

        List<Run> runList = runRepository.findAll();
        assertThat(runList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRuns() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList
        restRunMockMvc.perform(get("/api/runs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(run.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].platformHardwareInfo").value(hasItem(DEFAULT_PLATFORM_HARDWARE_INFO.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getRun() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get the run
        restRunMockMvc.perform(get("/api/runs/{id}", run.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(run.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.platformHardwareInfo").value(DEFAULT_PLATFORM_HARDWARE_INFO.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getAllRunsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where startDate equals to DEFAULT_START_DATE
        defaultRunShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the runList where startDate equals to UPDATED_START_DATE
        defaultRunShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllRunsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where startDate is not null
        defaultRunShouldBeFound("startDate.specified=true");

        // Get all the runList where startDate is null
        defaultRunShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllRunsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where startDate is greater than or equal to DEFAULT_START_DATE
        defaultRunShouldBeFound("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the runList where startDate is greater than or equal to UPDATED_START_DATE
        defaultRunShouldNotBeFound("startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllRunsByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where startDate is less than or equal to DEFAULT_START_DATE
        defaultRunShouldBeFound("startDate.lessThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the runList where startDate is less than or equal to SMALLER_START_DATE
        defaultRunShouldNotBeFound("startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    public void getAllRunsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where startDate is less than DEFAULT_START_DATE
        defaultRunShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the runList where startDate is less than UPDATED_START_DATE
        defaultRunShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllRunsByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where startDate is greater than DEFAULT_START_DATE
        defaultRunShouldNotBeFound("startDate.greaterThan=" + DEFAULT_START_DATE);

        // Get all the runList where startDate is greater than SMALLER_START_DATE
        defaultRunShouldBeFound("startDate.greaterThan=" + SMALLER_START_DATE);
    }


    @Test
    @Transactional
    public void getAllRunsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where endDate equals to DEFAULT_END_DATE
        defaultRunShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the runList where endDate equals to UPDATED_END_DATE
        defaultRunShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllRunsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where endDate is not null
        defaultRunShouldBeFound("endDate.specified=true");

        // Get all the runList where endDate is null
        defaultRunShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllRunsByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where endDate is greater than or equal to DEFAULT_END_DATE
        defaultRunShouldBeFound("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the runList where endDate is greater than or equal to UPDATED_END_DATE
        defaultRunShouldNotBeFound("endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllRunsByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where endDate is less than or equal to DEFAULT_END_DATE
        defaultRunShouldBeFound("endDate.lessThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the runList where endDate is less than or equal to SMALLER_END_DATE
        defaultRunShouldNotBeFound("endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    public void getAllRunsByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where endDate is less than DEFAULT_END_DATE
        defaultRunShouldNotBeFound("endDate.lessThan=" + DEFAULT_END_DATE);

        // Get all the runList where endDate is less than UPDATED_END_DATE
        defaultRunShouldBeFound("endDate.lessThan=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllRunsByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where endDate is greater than DEFAULT_END_DATE
        defaultRunShouldNotBeFound("endDate.greaterThan=" + DEFAULT_END_DATE);

        // Get all the runList where endDate is greater than SMALLER_END_DATE
        defaultRunShouldBeFound("endDate.greaterThan=" + SMALLER_END_DATE);
    }


    @Test
    @Transactional
    public void getAllRunsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where status equals to DEFAULT_STATUS
        defaultRunShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the runList where status equals to UPDATED_STATUS
        defaultRunShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllRunsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultRunShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the runList where status equals to UPDATED_STATUS
        defaultRunShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllRunsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where status is not null
        defaultRunShouldBeFound("status.specified=true");

        // Get all the runList where status is null
        defaultRunShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllRunsByPlatformHardwareInfoIsEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where platformHardwareInfo equals to DEFAULT_PLATFORM_HARDWARE_INFO
        defaultRunShouldBeFound("platformHardwareInfo.equals=" + DEFAULT_PLATFORM_HARDWARE_INFO);

        // Get all the runList where platformHardwareInfo equals to UPDATED_PLATFORM_HARDWARE_INFO
        defaultRunShouldNotBeFound("platformHardwareInfo.equals=" + UPDATED_PLATFORM_HARDWARE_INFO);
    }

    @Test
    @Transactional
    public void getAllRunsByPlatformHardwareInfoIsInShouldWork() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where platformHardwareInfo in DEFAULT_PLATFORM_HARDWARE_INFO or UPDATED_PLATFORM_HARDWARE_INFO
        defaultRunShouldBeFound("platformHardwareInfo.in=" + DEFAULT_PLATFORM_HARDWARE_INFO + "," + UPDATED_PLATFORM_HARDWARE_INFO);

        // Get all the runList where platformHardwareInfo equals to UPDATED_PLATFORM_HARDWARE_INFO
        defaultRunShouldNotBeFound("platformHardwareInfo.in=" + UPDATED_PLATFORM_HARDWARE_INFO);
    }

    @Test
    @Transactional
    public void getAllRunsByPlatformHardwareInfoIsNullOrNotNull() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where platformHardwareInfo is not null
        defaultRunShouldBeFound("platformHardwareInfo.specified=true");

        // Get all the runList where platformHardwareInfo is null
        defaultRunShouldNotBeFound("platformHardwareInfo.specified=false");
    }

    @Test
    @Transactional
    public void getAllRunsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where description equals to DEFAULT_DESCRIPTION
        defaultRunShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the runList where description equals to UPDATED_DESCRIPTION
        defaultRunShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllRunsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultRunShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the runList where description equals to UPDATED_DESCRIPTION
        defaultRunShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllRunsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        // Get all the runList where description is not null
        defaultRunShouldBeFound("description.specified=true");

        // Get all the runList where description is null
        defaultRunShouldNotBeFound("description.specified=false");
    }

    // @Test
    // @Transactional
    // public void getAllRunsByOutputFilesIsEqualToSomething() throws Exception {
    //     // Initialize the database
    //     runRepository.saveAndFlush(run);
    //     OutputFile outputFiles = OutputFileResourceIT.createEntity(em);
    //     em.persist(outputFiles);
    //     em.flush();
    //     run.addOutputFiles(outputFiles);
    //     runRepository.saveAndFlush(run);
    //     Long outputFilesId = outputFiles.getId();

    //     // Get all the runList where outputFiles equals to outputFilesId
    //     defaultRunShouldBeFound("outputFilesId.equals=" + outputFilesId);

    //     // Get all the runList where outputFiles equals to outputFilesId + 1
    //     defaultRunShouldNotBeFound("outputFilesId.equals=" + (outputFilesId + 1));
    // }


    @Test
    @Transactional
    public void getAllRunsByToolVersionsIsEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);
        ToolVersion toolVersions = ToolVersionResourceIT.createEntity(em);
        em.persist(toolVersions);
        em.flush();
        run.addToolVersions(toolVersions);
        runRepository.saveAndFlush(run);
        Long toolVersionsId = toolVersions.getId();

        // Get all the runList where toolVersions equals to toolVersionsId
        defaultRunShouldBeFound("toolVersionsId.equals=" + toolVersionsId);

        // Get all the runList where toolVersions equals to toolVersionsId + 1
        defaultRunShouldNotBeFound("toolVersionsId.equals=" + (toolVersionsId + 1));
    }


    @Test
    @Transactional
    public void getAllRunsByOwnerIsEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);
        User owner = UserResourceIT.createEntity(em);
        em.persist(owner);
        em.flush();
        run.setOwner(owner);
        runRepository.saveAndFlush(run);
        Long ownerId = owner.getId();

        // Get all the runList where owner equals to ownerId
        defaultRunShouldBeFound("ownerId.equals=" + ownerId);

        // Get all the runList where owner equals to ownerId + 1
        defaultRunShouldNotBeFound("ownerId.equals=" + (ownerId + 1));
    }


    @Test
    @Transactional
    public void getAllRunsByScenarioIsEqualToSomething() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);
        Scenario scenario = run.getScenario();
        Long scenarioId = scenario.getId();

        // Get all the runList where scenario equals to scenarioId
        defaultRunShouldBeFound("scenarioId.equals=" + scenarioId);

        // Get all the runList where scenario equals to scenarioId + 1
        defaultRunShouldNotBeFound("scenarioId.equals=" + (scenarioId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRunShouldBeFound(String filter) throws Exception {
        restRunMockMvc.perform(get("/api/runs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(run.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].platformHardwareInfo").value(hasItem(DEFAULT_PLATFORM_HARDWARE_INFO)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restRunMockMvc.perform(get("/api/runs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRunShouldNotBeFound(String filter) throws Exception {
        restRunMockMvc.perform(get("/api/runs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRunMockMvc.perform(get("/api/runs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingRun() throws Exception {
        // Get the run
        restRunMockMvc.perform(get("/api/runs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRun() throws Exception {
        // Initialize the database
        runRepository.saveAndFlush(run);

        int databaseSizeBeforeUpdate = runRepository.findAll().size();

        // Update the run
        Run updatedRun = runRepository.findById(run.getId()).get();
        // Disconnect from session so that the updates on updatedRun are not directly saved in db
        em.detach(updatedRun);
        updatedRun
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .platformHardwareInfo(UPDATED_PLATFORM_HARDWARE_INFO)
            .description(UPDATED_DESCRIPTION)
            .setOwner(run.getOwner());
        RunDTO runDTO = runMapper.toDto(updatedRun);

        restRunMockMvc.perform(put("/api/runs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(runDTO)))
            .andExpect(status().isOk());

        // Validate the Run in the database
        List<Run> runList = runRepository.findAll();
        assertThat(runList).hasSize(databaseSizeBeforeUpdate);
        Run testRun = runList.get(runList.size() - 1);
        assertThat(testRun.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testRun.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testRun.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRun.getPlatformHardwareInfo()).isEqualTo(UPDATED_PLATFORM_HARDWARE_INFO);
        assertThat(testRun.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingRun() throws Exception {
        int databaseSizeBeforeUpdate = runRepository.findAll().size();

        // Create the Run
        RunDTO runDTO = runMapper.toDto(run);
        runDTO.setId(null);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRunMockMvc.perform(put("/api/runs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(runDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Run in the database
        List<Run> runList = runRepository.findAll();
        assertThat(runList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRun() throws Exception {
        //add admin authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        // Initialize the database
        runRepository.saveAndFlush(run);

        int databaseSizeBeforeDelete = runRepository.findAll().size();

        // Delete the run
        restRunMockMvc.perform(delete("/api/runs/{id}", run.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Run> runList = runRepository.findAll();
        assertThat(runList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Run.class);
        Run run1 = new Run();
        run1.setId(1L);
        Run run2 = new Run();
        run2.setId(run1.getId());
        assertThat(run1).isEqualTo(run2);
        run2.setId(2L);
        assertThat(run1).isNotEqualTo(run2);
        run1.setId(null);
        assertThat(run1).isNotEqualTo(run2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RunDTO.class);
        RunDTO runDTO1 = new RunDTO();
        runDTO1.setId(1L);
        RunDTO runDTO2 = new RunDTO();
        assertThat(runDTO1).isNotEqualTo(runDTO2);
        runDTO2.setId(runDTO1.getId());
        assertThat(runDTO1).isEqualTo(runDTO2);
        runDTO2.setId(2L);
        assertThat(runDTO1).isNotEqualTo(runDTO2);
        runDTO1.setId(null);
        assertThat(runDTO1).isNotEqualTo(runDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(runMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(runMapper.fromId(null)).isNull();
    }

    @Test
    @Transactional
    public void createRunWithEndDateIsLessThanStartDate() throws Exception {
        int databaseSizeBeforeCreate = runRepository.findAll().size();
        // Create the Run with an end date less than start date
        run.setEndDate(SMALLER_END_DATE);
        RunDTO runDTO = runMapper.toDto(run);

        // An entity with an end date less than start date cannot be created, so this API call must fail
        restRunMockMvc.perform(post("/api/runs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(runDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Run in the database
        List<Run> runList = runRepository.findAll();
        assertThat(runList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateRunWithEndDateIsLessThanStartDate() throws Exception {
        runRepository.saveAndFlush(run);
        int databaseSizeBeforeUpdate = runRepository.findAll().size();
        // Update the Run with an end date less than start date
        Run updatedRun = runRepository.findById(run.getId()).get();
        // Disconnect from session so that the updates on updatedRun are not directly saved in db
        em.detach(updatedRun);
        updatedRun
            .endDate(SMALLER_END_DATE);
        RunDTO runDTO = runMapper.toDto(updatedRun);

        restRunMockMvc.perform(put("/api/runs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(runDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Run in the database
        List<Run> runList = runRepository.findAll();
        assertThat(runList).hasSize(databaseSizeBeforeUpdate);
        Run testRun = runList.get(runList.size() - 1);
        assertThat(testRun.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testRun.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testRun.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRun.getPlatformHardwareInfo()).isEqualTo(DEFAULT_PLATFORM_HARDWARE_INFO);
        assertThat(testRun.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkAutoCompletionOfOwnerId() throws Exception {
        int databaseSizeBeforeCreate = runRepository.findAll().size();
        // set the field null
        run.setOwner(null);

        // Create the OutputFile
        RunDTO runDTO = runMapper.toDto(run);
        restRunMockMvc.perform(post("/api/runs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(runDTO)))
            .andExpect(status().isCreated());

        // Validate the OutputFile in the database
        List<Run> runList = runRepository.findAll();
        assertThat(runList).hasSize(databaseSizeBeforeCreate + 1);
        Run testRun = runList.get(runList.size() - 1);
        assertThat(testRun.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testRun.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testRun.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRun.getPlatformHardwareInfo()).isEqualTo(DEFAULT_PLATFORM_HARDWARE_INFO);
        assertThat(testRun.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteRunWithoutAdminRights() throws Exception {
        //add user authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        // Initialize the database
        runRepository.saveAndFlush(run);

        int databaseSizeBeforeDelete = runRepository.findAll().size();

        // Delete the run
        restRunMockMvc.perform(delete("/api/runs/{Id}", run.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());

        // Validate the OutputFile in the database
        List<Run> runList = runRepository.findAll();
        assertThat(runList).hasSize(databaseSizeBeforeDelete);
        Run testRun = runList.get(runList.size() - 1);
        assertThat(testRun.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testRun.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testRun.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRun.getPlatformHardwareInfo()).isEqualTo(DEFAULT_PLATFORM_HARDWARE_INFO);
        assertThat(testRun.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }
}
