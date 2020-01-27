package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.ArchivemanagerApp;
import com.airbus.archivemanager.domain.Run;
import com.airbus.archivemanager.domain.Scenario;
import com.airbus.archivemanager.domain.ScenarioFile;
import com.airbus.archivemanager.domain.User;
import com.airbus.archivemanager.domain.enumeration.SimulationModeType;
import com.airbus.archivemanager.repository.ScenarioRepository;
import com.airbus.archivemanager.repository.UserRepository;
import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.service.ScenarioQueryService;
import com.airbus.archivemanager.service.ScenarioService;
import com.airbus.archivemanager.service.UserService;
import com.airbus.archivemanager.service.dto.ScenarioDTO;
import com.airbus.archivemanager.service.mapper.ScenarioMapper;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.airbus.archivemanager.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ScenarioResource} REST controller.
 */
@SpringBootTest(classes = ArchivemanagerApp.class)
public class ScenarioResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDateTime DEFAULT_CREATION_DATE = LocalDateTime.of(1980, 1, 1, 1, 1, 1);
    private static final LocalDateTime UPDATED_CREATION_DATE = LocalDateTime.of(1990, 1, 1, 1, 1, 1);
    private static final LocalDateTime SMALLER_CREATION_DATE = LocalDateTime.of(1970, 1, 1, 1, 1, 1);

    private static final SimulationModeType DEFAULT_SIMULATION_MODE = SimulationModeType.PLAY;
    private static final SimulationModeType UPDATED_SIMULATION_MODE = SimulationModeType.REPLAY;

    private static final LocalDateTime DEFAULT_START_SIMULATED_DATE = LocalDateTime.of(1980, 1, 1, 1, 1, 1);
    private static final LocalDateTime UPDATED_START_SIMULATED_DATE = LocalDateTime.of(1990, 1, 1, 1, 1, 1);
    private static final LocalDateTime SMALLER_START_SIMULATED_DATE = LocalDateTime.of(1970, 1, 1, 1, 1, 1);

    private static final LocalDateTime DEFAULT_END_SIMULATED_DATE = LocalDateTime.of(1980, 2, 1, 1, 1, 1);
    private static final LocalDateTime UPDATED_END_SIMULATED_DATE = LocalDateTime.of(1990, 2, 1, 1, 1, 1);
    private static final LocalDateTime SMALLER_END_SIMULATED_DATE = LocalDateTime.of(1970, 2, 1, 1, 1, 1);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private ScenarioMapper scenarioMapper;

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    private ScenarioQueryService scenarioQueryService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

    private MockMvc restScenarioMockMvc;

    private Scenario scenario;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scenario createEntity(EntityManager em) {
        Scenario scenario = new Scenario()
            .name(DEFAULT_NAME)
            .creationDate(DEFAULT_CREATION_DATE)
            .simulationMode(DEFAULT_SIMULATION_MODE)
            .startSimulatedDate(DEFAULT_START_SIMULATED_DATE)
            .simulation(DEFAULT_END_SIMULATED_DATE)
            .description(DEFAULT_DESCRIPTION);
        return scenario;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scenario createUpdatedEntity(EntityManager em) {
        Scenario scenario = new Scenario()
            .name(UPDATED_NAME)
            .creationDate(UPDATED_CREATION_DATE)
            .simulationMode(UPDATED_SIMULATION_MODE)
            .startSimulatedDate(UPDATED_START_SIMULATED_DATE)
            .simulation(UPDATED_END_SIMULATED_DATE)
            .description(UPDATED_DESCRIPTION);
        return scenario;
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ScenarioResource scenarioResource = new ScenarioResource(scenarioService, scenarioQueryService, messageSource);
        this.restScenarioMockMvc = MockMvcBuilders.standaloneSetup(scenarioResource)
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
        scenario = createEntity(em);
        scenario.setOwner(owner);
    }

    @Test
    @Transactional
    public void createScenario() throws Exception {
        int databaseSizeBeforeCreate = scenarioRepository.findAll().size();

        // Create the Scenario
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);
        restScenarioMockMvc.perform(post("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isCreated());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeCreate + 1);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScenario.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testScenario.getSimulationMode()).isEqualTo(DEFAULT_SIMULATION_MODE);
        assertThat(testScenario.getStartSimulatedDate()).isEqualTo(DEFAULT_START_SIMULATED_DATE);
        assertThat(testScenario.getEndSimulatedDate()).isEqualTo(DEFAULT_END_SIMULATED_DATE);
        assertThat(testScenario.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createScenarioWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = scenarioRepository.findAll().size();

        // Create the Scenario with an existing ID
        scenario.setId(1L);
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        // An entity with an existing ID cannot be created, so this API call must fail
        restScenarioMockMvc.perform(post("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setName(null);

        // Create the Scenario, which fails.
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        restScenarioMockMvc.perform(post("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isBadRequest());

        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllScenarios() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList
        restScenarioMockMvc.perform(get("/api/scenarios?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scenario.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].simulationMode").value(hasItem(DEFAULT_SIMULATION_MODE.toString())))
            .andExpect(jsonPath("$.[*].startSimulatedDate").value(hasItem(DEFAULT_START_SIMULATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].simulation").value(hasItem(DEFAULT_END_SIMULATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getScenario() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get the scenario
        restScenarioMockMvc.perform(get("/api/scenarios/{id}", scenario.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scenario.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.simulationMode").value(DEFAULT_SIMULATION_MODE.toString()))
            .andExpect(jsonPath("$.startSimulatedDate").value(DEFAULT_START_SIMULATED_DATE.toString()))
            .andExpect(jsonPath("$.simulation").value(DEFAULT_END_SIMULATED_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getAllScenariosByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where name equals to DEFAULT_NAME
        defaultScenarioShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the scenarioList where name equals to UPDATED_NAME
        defaultScenarioShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllScenariosByNameIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where name in DEFAULT_NAME or UPDATED_NAME
        defaultScenarioShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the scenarioList where name equals to UPDATED_NAME
        defaultScenarioShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllScenariosByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where name is not null
        defaultScenarioShouldBeFound("name.specified=true");

        // Get all the scenarioList where name is null
        defaultScenarioShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenariosByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where creationDate equals to DEFAULT_CREATION_DATE
        defaultScenarioShouldBeFound("creationDate.equals=" + DEFAULT_CREATION_DATE);

        // Get all the scenarioList where creationDate equals to UPDATED_CREATION_DATE
        defaultScenarioShouldNotBeFound("creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where creationDate is not null
        defaultScenarioShouldBeFound("creationDate.specified=true");

        // Get all the scenarioList where creationDate is null
        defaultScenarioShouldNotBeFound("creationDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenariosByCreationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where creationDate is greater than or equal to DEFAULT_CREATION_DATE
        defaultScenarioShouldBeFound("creationDate.greaterThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the scenarioList where creationDate is greater than or equal to UPDATED_CREATION_DATE
        defaultScenarioShouldNotBeFound("creationDate.greaterThanOrEqual=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByCreationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where creationDate is less than or equal to DEFAULT_CREATION_DATE
        defaultScenarioShouldBeFound("creationDate.lessThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the scenarioList where creationDate is less than or equal to SMALLER_CREATION_DATE
        defaultScenarioShouldNotBeFound("creationDate.lessThanOrEqual=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByCreationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where creationDate is less than DEFAULT_CREATION_DATE
        defaultScenarioShouldNotBeFound("creationDate.lessThan=" + DEFAULT_CREATION_DATE);

        // Get all the scenarioList where creationDate is less than UPDATED_CREATION_DATE
        defaultScenarioShouldBeFound("creationDate.lessThan=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByCreationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where creationDate is greater than DEFAULT_CREATION_DATE
        defaultScenarioShouldNotBeFound("creationDate.greaterThan=" + DEFAULT_CREATION_DATE);

        // Get all the scenarioList where creationDate is greater than SMALLER_CREATION_DATE
        defaultScenarioShouldBeFound("creationDate.greaterThan=" + SMALLER_CREATION_DATE);
    }


    @Test
    @Transactional
    public void getAllScenariosBySimulationModeIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where simulationMode equals to DEFAULT_SIMULATION_MODE
        defaultScenarioShouldBeFound("simulationMode.equals=" + DEFAULT_SIMULATION_MODE);

        // Get all the scenarioList where simulationMode equals to UPDATED_SIMULATION_MODE
        defaultScenarioShouldNotBeFound("simulationMode.equals=" + UPDATED_SIMULATION_MODE);
    }

    @Test
    @Transactional
    public void getAllScenariosBySimulationModeIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where simulationMode is not null
        defaultScenarioShouldBeFound("simulationMode.specified=true");

        // Get all the scenarioList where simulationMode is null
        defaultScenarioShouldNotBeFound("simulationMode.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenariosByStartSimulatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where startSimulatedDate equals to DEFAULT_START_SIMULATED_DATE
        defaultScenarioShouldBeFound("startSimulatedDate.equals=" + DEFAULT_START_SIMULATED_DATE);

        // Get all the scenarioList where startSimulatedDate equals to UPDATED_START_SIMULATED_DATE
        defaultScenarioShouldNotBeFound("startSimulatedDate.equals=" + UPDATED_START_SIMULATED_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByStartSimulatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where startSimulatedDate is not null
        defaultScenarioShouldBeFound("startSimulatedDate.specified=true");

        // Get all the scenarioList where startSimulatedDate is null
        defaultScenarioShouldNotBeFound("startSimulatedDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenariosByStartSimulatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where startSimulatedDate is greater than or equal to DEFAULT_START_SIMULATED_DATE
        defaultScenarioShouldBeFound("startSimulatedDate.greaterThanOrEqual=" + DEFAULT_START_SIMULATED_DATE);

        // Get all the scenarioList where startSimulatedDate is greater than or equal to UPDATED_START_SIMULATED_DATE
        defaultScenarioShouldNotBeFound("startSimulatedDate.greaterThanOrEqual=" + UPDATED_START_SIMULATED_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByStartSimulatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where startSimulatedDate is less than or equal to DEFAULT_START_SIMULATED_DATE
        defaultScenarioShouldBeFound("startSimulatedDate.lessThanOrEqual=" + DEFAULT_START_SIMULATED_DATE);

        // Get all the scenarioList where startSimulatedDate is less than or equal to SMALLER_START_SIMULATED_DATE
        defaultScenarioShouldNotBeFound("startSimulatedDate.lessThanOrEqual=" + SMALLER_START_SIMULATED_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByStartSimulatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where startSimulatedDate is less than DEFAULT_START_SIMULATED_DATE
        defaultScenarioShouldNotBeFound("startSimulatedDate.lessThan=" + DEFAULT_START_SIMULATED_DATE);

        // Get all the scenarioList where startSimulatedDate is less than UPDATED_START_SIMULATED_DATE
        defaultScenarioShouldBeFound("startSimulatedDate.lessThan=" + UPDATED_START_SIMULATED_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByStartSimulatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where startSimulatedDate is greater than DEFAULT_START_SIMULATED_DATE
        defaultScenarioShouldNotBeFound("startSimulatedDate.greaterThan=" + DEFAULT_START_SIMULATED_DATE);

        // Get all the scenarioList where startSimulatedDate is greater than SMALLER_START_SIMULATED_DATE
        defaultScenarioShouldBeFound("startSimulatedDate.greaterThan=" + SMALLER_START_SIMULATED_DATE);
    }


    @Test
    @Transactional
    public void getAllScenariosByEndSimulatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where simulation equals to DEFAULT_END_SIMULATED_DATE
        defaultScenarioShouldBeFound("simulation.equals=" + DEFAULT_END_SIMULATED_DATE);

        // Get all the scenarioList where simulation equals to UPDATED_END_SIMULATED_DATE
        defaultScenarioShouldNotBeFound("simulation.equals=" + UPDATED_END_SIMULATED_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByEndSimulatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where simulation is not null
        defaultScenarioShouldBeFound("simulation.specified=true");

        // Get all the scenarioList where simulation is null
        defaultScenarioShouldNotBeFound("simulation.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenariosByEndSimulatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where simulation is greater than or equal to DEFAULT_END_SIMULATED_DATE
        defaultScenarioShouldBeFound("simulation.greaterThanOrEqual=" + DEFAULT_END_SIMULATED_DATE);

        // Get all the scenarioList where simulation is greater than or equal to UPDATED_END_SIMULATED_DATE
        defaultScenarioShouldNotBeFound("simulation.greaterThanOrEqual=" + UPDATED_END_SIMULATED_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByEndSimulatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where simulation is less than or equal to DEFAULT_END_SIMULATED_DATE
        defaultScenarioShouldBeFound("simulation.lessThanOrEqual=" + DEFAULT_END_SIMULATED_DATE);

        // Get all the scenarioList where simulation is less than or equal to SMALLER_END_SIMULATED_DATE
        defaultScenarioShouldNotBeFound("simulation.lessThanOrEqual=" + SMALLER_END_SIMULATED_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByEndSimulatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where simulation is less than DEFAULT_END_SIMULATED_DATE
        defaultScenarioShouldNotBeFound("simulation.lessThan=" + DEFAULT_END_SIMULATED_DATE);

        // Get all the scenarioList where simulation is less than UPDATED_END_SIMULATED_DATE
        defaultScenarioShouldBeFound("simulation.lessThan=" + UPDATED_END_SIMULATED_DATE);
    }

    @Test
    @Transactional
    public void getAllScenariosByEndSimulatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where simulation is greater than DEFAULT_END_SIMULATED_DATE
        defaultScenarioShouldNotBeFound("simulation.greaterThan=" + DEFAULT_END_SIMULATED_DATE);

        // Get all the scenarioList where simulation is greater than SMALLER_END_SIMULATED_DATE
        defaultScenarioShouldBeFound("simulation.greaterThan=" + SMALLER_END_SIMULATED_DATE);
    }


    @Test
    @Transactional
    public void getAllScenariosByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where description equals to DEFAULT_DESCRIPTION
        defaultScenarioShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the scenarioList where description equals to UPDATED_DESCRIPTION
        defaultScenarioShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllScenariosByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultScenarioShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the scenarioList where description equals to UPDATED_DESCRIPTION
        defaultScenarioShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllScenariosByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where description is not null
        defaultScenarioShouldBeFound("description.specified=true");

        // Get all the scenarioList where description is null
        defaultScenarioShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenariosByRunsIsEqualToSomething() throws Exception {
        // Initialize the owner
        User owner = UserResourceIT.createOwner(em, userRepository, userService);

        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);
        Run runs = RunResourceIT.createEntity(em);
        runs.setOwner(owner);
        em.persist(runs);
        em.flush();
        scenario.addRuns(runs);
        scenarioRepository.saveAndFlush(scenario);
        Long runsId = runs.getId();

        // Get all the scenarioList where runs equals to runsId
        defaultScenarioShouldBeFound("runsId.equals=" + runsId);

        // Get all the scenarioList where runs equals to runsId + 1
        defaultScenarioShouldNotBeFound("runsId.equals=" + (runsId + 1));
    }


    @Test
    @Transactional
    public void getAllScenariosByOwnerIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);
        User owner = UserResourceIT.createEntity(em);
        em.persist(owner);
        em.flush();
        scenario.setOwner(owner);
        scenarioRepository.saveAndFlush(scenario);
        Long ownerId = owner.getId();

        // Get all the scenarioList where owner equals to ownerId
        defaultScenarioShouldBeFound("ownerId.equals=" + ownerId);

        // Get all the scenarioList where owner equals to ownerId + 1
        defaultScenarioShouldNotBeFound("ownerId.equals=" + (ownerId + 1));
    }


    @Test
    @Transactional
    public void getAllScenariosByScenarioFilesIsEqualToSomething() throws Exception {
        // Initialize the owner
        User owner = UserResourceIT.createOwner(em, userRepository, userService);

        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);
        ScenarioFile scenarioFile = ScenarioFileResourceIT.createEntity(em);
        scenarioFile.setOwner(owner);
        scenarioFile.getScenarios().add(scenario);
        em.persist(scenarioFile);
        em.flush();
        scenario.getScenarioFiles().add(scenarioFile);
        scenarioRepository.saveAndFlush(scenario);
        String scenarioFilesRelativePathInST = scenarioFile.getRelativePathInST();

        // Get all the scenarioList where scenarioFiles equals to scenarioFilesRelativePathInST
        defaultScenarioShouldBeFound("scenarioFilesRelativePathInST.equals=" + scenarioFilesRelativePathInST);

        // Get all the scenarioList where scenarioFiles equals to scenarioFilesRelativePathInST + 1
        defaultScenarioShouldNotBeFound("scenarioFilesRelativePathInST.equals=" + (scenarioFilesRelativePathInST + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScenarioShouldBeFound(String filter) throws Exception {
        restScenarioMockMvc.perform(get("/api/scenarios?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scenario.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].simulationMode").value(hasItem(DEFAULT_SIMULATION_MODE.toString())))
            .andExpect(jsonPath("$.[*].startSimulatedDate").value(hasItem(DEFAULT_START_SIMULATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].simulation").value(hasItem(DEFAULT_END_SIMULATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restScenarioMockMvc.perform(get("/api/scenarios/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScenarioShouldNotBeFound(String filter) throws Exception {
        restScenarioMockMvc.perform(get("/api/scenarios?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScenarioMockMvc.perform(get("/api/scenarios/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingScenario() throws Exception {
        // Get the scenario
        restScenarioMockMvc.perform(get("/api/scenarios/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScenario() throws Exception {
        //add admin authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", authorities));
        SecurityContextHolder.setContext(securityContext);
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();

        // Update the scenario
        Scenario updatedScenario = scenarioRepository.findById(scenario.getId()).get();
        // Disconnect from session so that the updates on updatedScenario are not directly saved in db
        em.detach(updatedScenario);
        updatedScenario
            .name(UPDATED_NAME)
            .creationDate(UPDATED_CREATION_DATE)
            .simulationMode(UPDATED_SIMULATION_MODE)
            .startSimulatedDate(UPDATED_START_SIMULATED_DATE)
            .simulation(UPDATED_END_SIMULATED_DATE)
            .description(UPDATED_DESCRIPTION);
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(updatedScenario);

        restScenarioMockMvc.perform(put("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isOk());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testScenario.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testScenario.getSimulationMode()).isEqualTo(UPDATED_SIMULATION_MODE);
        assertThat(testScenario.getStartSimulatedDate()).isEqualTo(UPDATED_START_SIMULATED_DATE);
        assertThat(testScenario.getEndSimulatedDate()).isEqualTo(UPDATED_END_SIMULATED_DATE);
        assertThat(testScenario.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();

        // Create the Scenario
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScenarioMockMvc.perform(put("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteScenario() throws Exception {
        //add admin authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", authorities));
        SecurityContextHolder.setContext(securityContext);
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeDelete = scenarioRepository.findAll().size();

        // Delete the scenario
        restScenarioMockMvc.perform(delete("/api/scenarios/{id}", scenario.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Scenario.class);
        Scenario scenario1 = new Scenario();
        scenario1.setId(1L);
        Scenario scenario2 = new Scenario();
        scenario2.setId(scenario1.getId());
        assertThat(scenario1).isEqualTo(scenario2);
        scenario2.setId(2L);
        assertThat(scenario1).isNotEqualTo(scenario2);
        scenario1.setId(null);
        assertThat(scenario1).isNotEqualTo(scenario2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScenarioDTO.class);
        ScenarioDTO scenarioDTO1 = new ScenarioDTO();
        scenarioDTO1.setId(1L);
        ScenarioDTO scenarioDTO2 = new ScenarioDTO();
        assertThat(scenarioDTO1).isNotEqualTo(scenarioDTO2);
        scenarioDTO2.setId(scenarioDTO1.getId());
        assertThat(scenarioDTO1).isEqualTo(scenarioDTO2);
        scenarioDTO2.setId(2L);
        assertThat(scenarioDTO1).isNotEqualTo(scenarioDTO2);
        scenarioDTO1.setId(null);
        assertThat(scenarioDTO1).isNotEqualTo(scenarioDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(scenarioMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(scenarioMapper.fromId(null)).isNull();
    }

    @Test
    @Transactional
    public void checkAutoCompletionOfOwnerId() throws Exception {
        int databaseSizeBeforeCreate = scenarioRepository.findAll().size();
        // set the field null
        scenario.setOwner(null);

        // Create the Scenario
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);
        restScenarioMockMvc.perform(post("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isCreated());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeCreate + 1);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScenario.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testScenario.getSimulationMode()).isEqualTo(DEFAULT_SIMULATION_MODE);
        assertThat(testScenario.getStartSimulatedDate()).isEqualTo(DEFAULT_START_SIMULATED_DATE);
        assertThat(testScenario.getEndSimulatedDate()).isEqualTo(DEFAULT_END_SIMULATED_DATE);
        assertThat(testScenario.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createScenarioWithNameAlreadyExistsInOtherScenario() throws Exception {
        // initialize database
        scenarioRepository.saveAndFlush(scenario);
        int databaseSizeBeforeCreate = scenarioRepository.findAll().size();

        // Create the dataSet which fails
        User owner = UserResourceIT.createOwner(em, userRepository, userService);
        Scenario scenario2 = createUpdatedEntity(em);
        scenario2.setOwner(owner);
        scenario2.setName(scenario.getName());
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario2);

        // If the entity doesn't have an name, it will throw BadRequestAlertException
        restScenarioMockMvc.perform(post("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> configDataSetList = scenarioRepository.findAll();
        assertThat(configDataSetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateScenarioWithNameAlreadyExistsInOtherScenario() throws Exception {
        // initialize database with 2 rows
        scenarioRepository.saveAndFlush(scenario);
        User owner = UserResourceIT.createOwner(em, userRepository, userService);
        Scenario anotherScenario = createUpdatedEntity(em);
        anotherScenario.setOwner(owner);
        scenarioRepository.saveAndFlush(anotherScenario);
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();

        // Update the first scenario
        Scenario updatedScenario = scenarioRepository.findById(scenario.getId()).get();
        // Disconnect from session so that the updates on updatedScenario are not directly saved in db
        em.detach(updatedScenario);
        updatedScenario
            .name(anotherScenario.getName());
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(updatedScenario);

        //add admin authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", authorities));
        SecurityContextHolder.setContext(securityContext);

        // If the name has already been used, it will throw BadRequestAlertException
        restScenarioMockMvc.perform(put("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DataSet in the database
        List<Scenario> configDataSetList = scenarioRepository.findAll();
        assertThat(configDataSetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void createScenarioWithEndSimulatedDateIsLessThanStartSimulatedDate() throws Exception {
        int databaseSizeBeforeCreate = scenarioRepository.findAll().size();
        // Create the Scenario with an end date less than start date
        scenario.setEndSimulatedDate(SMALLER_END_SIMULATED_DATE);
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        // An entity with an end date less than start date cannot be created, so this API call must fail
        restScenarioMockMvc.perform(post("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Run in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateScenarioWithEndSimulatedDateIsLessThanStartSimulatedDate() throws Exception {
        //add admin authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", authorities));
        SecurityContextHolder.setContext(securityContext);
        scenarioRepository.saveAndFlush(scenario);
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        // Update the Scenario with an end date less than start date
        Scenario updatedScenario = scenarioRepository.findById(scenario.getId()).get();
        // Disconnect from session so that the updates on updatedScenario are not directly saved in db
        em.detach(updatedScenario);
        updatedScenario
            .simulation(SMALLER_END_SIMULATED_DATE);
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(updatedScenario);

        restScenarioMockMvc.perform(put("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScenario.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testScenario.getSimulationMode()).isEqualTo(DEFAULT_SIMULATION_MODE);
        assertThat(testScenario.getStartSimulatedDate()).isEqualTo(DEFAULT_START_SIMULATED_DATE);
        assertThat(testScenario.getEndSimulatedDate()).isEqualTo(DEFAULT_END_SIMULATED_DATE);
        assertThat(testScenario.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkUserWithoutAdminRightsCantDelete() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();

        //add user authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        // Delete the Scenario
        restScenarioMockMvc.perform(delete("/api/scenarios/{Id}", scenario.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());

        // Validate the OutputFile in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeTest);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScenario.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testScenario.getSimulationMode()).isEqualTo(DEFAULT_SIMULATION_MODE);
        assertThat(testScenario.getStartSimulatedDate()).isEqualTo(DEFAULT_START_SIMULATED_DATE);
        assertThat(testScenario.getEndSimulatedDate()).isEqualTo(DEFAULT_END_SIMULATED_DATE);
        assertThat(testScenario.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateOutputFileWithoutAdminRights() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();

        //add user authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        // Update the scenario
        Scenario updatedScenario = scenarioRepository.findById(scenario.getId()).get();
        // Disconnect from session so that the updates on updatedOutputFile are not directly saved in db
        em.detach(updatedScenario);
        updatedScenario
            .name(UPDATED_NAME)
            .creationDate(UPDATED_CREATION_DATE)
            .simulationMode(UPDATED_SIMULATION_MODE)
            .startSimulatedDate(UPDATED_START_SIMULATED_DATE)
            .simulation(UPDATED_END_SIMULATED_DATE)
            .description(UPDATED_DESCRIPTION);
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(updatedScenario);

        restScenarioMockMvc.perform(put("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isForbidden());

        // Validate the OutputFile in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScenario.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testScenario.getSimulationMode()).isEqualTo(DEFAULT_SIMULATION_MODE);
        assertThat(testScenario.getStartSimulatedDate()).isEqualTo(DEFAULT_START_SIMULATED_DATE);
        assertThat(testScenario.getEndSimulatedDate()).isEqualTo(DEFAULT_END_SIMULATED_DATE);
        assertThat(testScenario.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkAutoCompletionOfLtInsertionDate() throws Exception {
        int databaseSizeBeforeCreate = scenarioRepository.findAll().size();
        // set the field null
        scenario.setCreationDate(null);

        // Create the OutputFile
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);
        restScenarioMockMvc.perform(post("/api/scenarios")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isCreated());

        // Validate the OutputFile in the database
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeCreate + 1);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScenario.getCreationDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter));
        assertThat(testScenario.getSimulationMode()).isEqualTo(DEFAULT_SIMULATION_MODE);
        assertThat(testScenario.getStartSimulatedDate()).isEqualTo(DEFAULT_START_SIMULATED_DATE);
        assertThat(testScenario.getEndSimulatedDate()).isEqualTo(DEFAULT_END_SIMULATED_DATE);
        assertThat(testScenario.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }
}
