package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.ArchivemanagerApp;
import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.domain.enumeration.FileType;
import com.airbus.archivemanager.domain.enumeration.SecurityLevel;
import com.airbus.archivemanager.repository.AuthorityRepository;
import com.airbus.archivemanager.repository.OutputFileRepository;
import com.airbus.archivemanager.repository.ScenarioFileRepository;
import com.airbus.archivemanager.repository.UserRepository;
import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.service.ScenarioFileQueryService;
import com.airbus.archivemanager.service.ScenarioFileService;
import com.airbus.archivemanager.service.UserService;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;
import com.airbus.archivemanager.service.mapper.OutputFileMapper;
import com.airbus.archivemanager.service.mapper.ScenarioFileMapper;
import com.airbus.archivemanager.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageImpl;
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
import java.util.*;

import static com.airbus.archivemanager.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ScenarioFileResource} REST controller.
 */
@SpringBootTest(classes = ArchivemanagerApp.class)
public class ScenarioFileResourceIT {

    private static final FileType DEFAULT_INPUT_TYPE = FileType.INPUT;
    private static final FileType UPDATED_INPUT_TYPE = FileType.CONFIG;

    private static final String DEFAULT_RELATIVE_PATH_IN_ST = "AAAAAAAAAA";
    private static final String UPDATED_RELATIVE_PATH_IN_ST = "BBBBBBBBBB";

    private static final LocalDateTime DEFAULT_L_T_INSERTION_DATE = LocalDateTime.of(1980, 1, 1, 1, 1, 1);
    private static final LocalDateTime UPDATED_L_T_INSERTION_DATE = LocalDateTime.of(1990, 1, 1, 1, 1, 1);
    private static final LocalDateTime SMALLER_L_T_INSERTION_DATE = LocalDateTime.of(1970, 1, 1, 1, 1, 1);

    private static final String DEFAULT_PATH_IN_LT = "AAAAAAAAAA";
    private static final String UPDATED_PATH_IN_LT = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FILE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_FORMAT = "AAAAAAAAAA";
    private static final String UPDATED_FORMAT = "BBBBBBBBBB";

    private static final String DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA = "AAAAAAAAAA";
    private static final String UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA = "BBBBBBBBBB";

    private static final LocalDateTime DEFAULT_TIME_OF_DATA = LocalDateTime.of(1980, 1, 1, 1, 1, 1);
    private static final LocalDateTime UPDATED_TIME_OF_DATA = LocalDateTime.of(1990, 1, 1, 1, 1, 1);
    private static final LocalDateTime SMALLER_TIME_OF_DATA = LocalDateTime.of(1970, 1, 1, 1, 1, 1);

    private static final SecurityLevel DEFAULT_SECURITY_LEVEL = SecurityLevel.NORMAL;
    private static final SecurityLevel UPDATED_SECURITY_LEVEL = SecurityLevel.EIC;

    private static final String DEFAULT_CRC = "AAAAAAAAAA";
    private static final String UPDATED_CRC = "BBBBBBBBBB";

    @Autowired
    private ScenarioFileRepository scenarioFileRepository;

    @Mock
    private ScenarioFileRepository scenarioFileRepositoryMock;

    @Autowired
    private ScenarioFileMapper scenarioFileMapper;

    @Mock
    private ScenarioFileService scenarioFileServiceMock;

    @Autowired
    private ScenarioFileService scenarioFileService;

    @Autowired
    private ScenarioFileQueryService scenarioFileQueryService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private OutputFileMapper outputFileMapper;

    @Autowired
    private OutputFileRepository outputFileRepository;

    private MockMvc restScenarioFileMockMvc;

    private ScenarioFile scenarioFile;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScenarioFile createEntity(EntityManager em) {
        ScenarioFile scenarioFile = new ScenarioFile()
            .inputType(DEFAULT_INPUT_TYPE)
            .relativePathInST(DEFAULT_RELATIVE_PATH_IN_ST)
            .lTInsertionDate(DEFAULT_L_T_INSERTION_DATE)
            .pathInLT(DEFAULT_PATH_IN_LT)
            .fileType(DEFAULT_FILE_TYPE)
            .format(DEFAULT_FORMAT)
            .subSystemAtOriginOfData(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA)
            .timeOfData(DEFAULT_TIME_OF_DATA)
            .securityLevel(DEFAULT_SECURITY_LEVEL)
            .crc(DEFAULT_CRC);
        return scenarioFile;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScenarioFile createUpdatedEntity(EntityManager em) {
        ScenarioFile scenarioFile = new ScenarioFile()
            .relativePathInST(UPDATED_RELATIVE_PATH_IN_ST)
            .lTInsertionDate(UPDATED_L_T_INSERTION_DATE)
            .pathInLT(UPDATED_PATH_IN_LT)
            .fileType(UPDATED_FILE_TYPE)
            .format(UPDATED_FORMAT)
            .subSystemAtOriginOfData(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA)
            .timeOfData(UPDATED_TIME_OF_DATA)
            .securityLevel(UPDATED_SECURITY_LEVEL)
            .crc(UPDATED_CRC);
        return scenarioFile;
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ScenarioFileResource scenarioFileResource = new ScenarioFileResource(scenarioFileService, scenarioFileQueryService, messageSource);
        this.restScenarioFileMockMvc = MockMvcBuilders.standaloneSetup(scenarioFileResource)
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
        scenarioFile = createEntity(em);
        scenarioFile.setOwner(owner);
    }

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void createScenarioFile() throws Exception {
        int databaseSizeBeforeCreate = scenarioFileRepository.findAll().size();

        // Create the ScenarioFile
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(scenarioFile);
        restScenarioFileMockMvc.perform(post("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isCreated());

        // Validate the ScenarioFile in the database
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeCreate + 1);
        ScenarioFile testScenarioFile = scenarioFileList.get(scenarioFileList.size() - 1);
        assertThat(testScenarioFile.getInputType()).isEqualTo(DEFAULT_INPUT_TYPE);
        assertThat(testScenarioFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testScenarioFile.getlTInsertionDate()).isEqualTo(DEFAULT_L_T_INSERTION_DATE);
        assertThat(testScenarioFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testScenarioFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testScenarioFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testScenarioFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testScenarioFile.getTimeOfData()).isEqualTo(DEFAULT_TIME_OF_DATA);
        assertThat(testScenarioFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testScenarioFile.getCrc()).isEqualTo(DEFAULT_CRC);
    }*/

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void createScenarioFileWithoutRelativePathInST() throws Exception {
        int databaseSizeBeforeCreate = scenarioFileRepository.findAll().size();

        // Create the ScenarioFile without a RelativePathInST
        scenarioFile.setRelativePathInST(null);
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(scenarioFile);

        // An entity without a RelativePathInST cannot be created, so this API call must fail
        restScenarioFileMockMvc.perform(post("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ScenarioFile in the database
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeCreate);
    }*/

    @Test
    @Transactional
    public void updateScenarioFileWithoutRelativePathInST() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        int databaseSizeBeforeUpdate = scenarioFileRepository.findAll().size();

        // Update the scenarioFile
        ScenarioFile updatedScenarioFile = scenarioFileRepository.findById(scenarioFile.getRelativePathInST()).get();
        // Disconnect from session so that the updates on updatedScenarioFile are not directly saved in db
        em.detach(updatedScenarioFile);

        // Create the ScenarioFile without a RelativePathInST
        updatedScenarioFile.setRelativePathInST(null);
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(updatedScenarioFile);

        // An entity without a RelativePathInST cannot be updated, so this API call must fail
        restScenarioFileMockMvc.perform(put("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ScenarioFile in the database
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeUpdate);

        ScenarioFile testScenarioFile = scenarioFileList.get(scenarioFileList.size() - 1);
        assertThat(testScenarioFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
    }

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void checkInputTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioFileRepository.findAll().size();
        // set the field null
        scenarioFile.setInputType(null);

        // Create the ScenarioFile, which fails.
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(scenarioFile);

        restScenarioFileMockMvc.perform(post("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isBadRequest());

        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeTest);
    }*/

    @Test
    @Transactional
    public void getAllScenarioFiles() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList
        restScenarioFileMockMvc.perform(get("/api/scenario-files?sort=relativePathInST,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].inputType").value(hasItem(DEFAULT_INPUT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].relativePathInST").value(hasItem(DEFAULT_RELATIVE_PATH_IN_ST)))
            .andExpect(jsonPath("$.[*].lTInsertionDate").value(hasItem(DEFAULT_L_T_INSERTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].pathInLT").value(hasItem(DEFAULT_PATH_IN_LT)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT)))
            .andExpect(jsonPath("$.[*].subSystemAtOriginOfData").value(hasItem(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA)))
            .andExpect(jsonPath("$.[*].timeOfData").value(hasItem(DEFAULT_TIME_OF_DATA.toString())))
            .andExpect(jsonPath("$.[*].securityLevel").value(hasItem(DEFAULT_SECURITY_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].crc").value(hasItem(DEFAULT_CRC)));
    }

    @SuppressWarnings({"unchecked"})
    public void getAllScenarioFilesWithEagerRelationshipsIsEnabled() throws Exception {
        ScenarioFileResource scenarioFileResource = new ScenarioFileResource(scenarioFileServiceMock, scenarioFileQueryService, messageSource);
        when(scenarioFileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restScenarioFileMockMvc = MockMvcBuilders.standaloneSetup(scenarioFileResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restScenarioFileMockMvc.perform(get("/api/scenario-files?eagerload=true"))
            .andExpect(status().isOk());

        verify(scenarioFileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllScenarioFilesWithEagerRelationshipsIsNotEnabled() throws Exception {
        ScenarioFileResource scenarioFileResource = new ScenarioFileResource(scenarioFileServiceMock, scenarioFileQueryService, messageSource);
        when(scenarioFileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
        MockMvc restScenarioFileMockMvc = MockMvcBuilders.standaloneSetup(scenarioFileResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restScenarioFileMockMvc.perform(get("/api/scenario-files?eagerload=true"))
            .andExpect(status().isOk());

        verify(scenarioFileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByInputTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where inputType equals to DEFAULT_INPUT_TYPE
        defaultScenarioFileShouldBeFound("inputType.equals=" + DEFAULT_INPUT_TYPE);

        // Get all the scenarioFileList where inputType equals to UPDATED_INPUT_TYPE
        defaultScenarioFileShouldNotBeFound("inputType.equals=" + UPDATED_INPUT_TYPE);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByInputTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where inputType is not null
        defaultScenarioFileShouldBeFound("inputType.specified=true");

        // Get all the scenarioFileList where inputType is null
        defaultScenarioFileShouldNotBeFound("inputType.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByRelativePathInSTIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where relativePathInST equals to DEFAULT_RELATIVE_PATH_IN_ST
        defaultScenarioFileShouldBeFound("relativePathInST.equals=" + DEFAULT_RELATIVE_PATH_IN_ST);

        // Get all the scenarioFileList where relativePathInST equals to UPDATED_RELATIVE_PATH_IN_ST
        defaultScenarioFileShouldNotBeFound("relativePathInST.equals=" + UPDATED_RELATIVE_PATH_IN_ST);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByRelativePathInSTIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where relativePathInST in DEFAULT_RELATIVE_PATH_IN_ST or UPDATED_RELATIVE_PATH_IN_ST
        defaultScenarioFileShouldBeFound("relativePathInST.in=" + DEFAULT_RELATIVE_PATH_IN_ST + "," + UPDATED_RELATIVE_PATH_IN_ST);

        // Get all the scenarioFileList where relativePathInST equals to UPDATED_RELATIVE_PATH_IN_ST
        defaultScenarioFileShouldNotBeFound("relativePathInST.in=" + UPDATED_RELATIVE_PATH_IN_ST);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByRelativePathInSTIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where relativePathInST is not null
        defaultScenarioFileShouldBeFound("relativePathInST.specified=true");

        // Get all the scenarioFileList where relativePathInST is null
        defaultScenarioFileShouldNotBeFound("relativePathInST.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenarioFilesBylTInsertionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where lTInsertionDate equals to DEFAULT_L_T_INSERTION_DATE
        defaultScenarioFileShouldBeFound("lTInsertionDate.equals=" + DEFAULT_L_T_INSERTION_DATE);

        // Get all the scenarioFileList where lTInsertionDate equals to UPDATED_L_T_INSERTION_DATE
        defaultScenarioFileShouldNotBeFound("lTInsertionDate.equals=" + UPDATED_L_T_INSERTION_DATE);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesBylTInsertionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where lTInsertionDate is not null
        defaultScenarioFileShouldBeFound("lTInsertionDate.specified=true");

        // Get all the scenarioFileList where lTInsertionDate is null
        defaultScenarioFileShouldNotBeFound("lTInsertionDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenarioFilesBylTInsertionDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where lTInsertionDate is greater than or equal to DEFAULT_L_T_INSERTION_DATE
        defaultScenarioFileShouldBeFound("lTInsertionDate.greaterThanOrEqual=" + DEFAULT_L_T_INSERTION_DATE);

        // Get all the scenarioFileList where lTInsertionDate is greater than or equal to UPDATED_L_T_INSERTION_DATE
        defaultScenarioFileShouldNotBeFound("lTInsertionDate.greaterThanOrEqual=" + UPDATED_L_T_INSERTION_DATE);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesBylTInsertionDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where lTInsertionDate is less than or equal to DEFAULT_L_T_INSERTION_DATE
        defaultScenarioFileShouldBeFound("lTInsertionDate.lessThanOrEqual=" + DEFAULT_L_T_INSERTION_DATE);

        // Get all the scenarioFileList where lTInsertionDate is less than or equal to SMALLER_L_T_INSERTION_DATE
        defaultScenarioFileShouldNotBeFound("lTInsertionDate.lessThanOrEqual=" + SMALLER_L_T_INSERTION_DATE);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesBylTInsertionDateIsLessThanSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where lTInsertionDate is less than DEFAULT_L_T_INSERTION_DATE
        defaultScenarioFileShouldNotBeFound("lTInsertionDate.lessThan=" + DEFAULT_L_T_INSERTION_DATE);

        // Get all the scenarioFileList where lTInsertionDate is less than UPDATED_L_T_INSERTION_DATE
        defaultScenarioFileShouldBeFound("lTInsertionDate.lessThan=" + UPDATED_L_T_INSERTION_DATE);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesBylTInsertionDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where lTInsertionDate is greater than DEFAULT_L_T_INSERTION_DATE
        defaultScenarioFileShouldNotBeFound("lTInsertionDate.greaterThan=" + DEFAULT_L_T_INSERTION_DATE);

        // Get all the scenarioFileList where lTInsertionDate is greater than SMALLER_L_T_INSERTION_DATE
        defaultScenarioFileShouldBeFound("lTInsertionDate.greaterThan=" + SMALLER_L_T_INSERTION_DATE);
    }


    @Test
    @Transactional
    public void getAllScenarioFilesByPathInLTIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where pathInLT equals to DEFAULT_PATH_IN_LT
        defaultScenarioFileShouldBeFound("pathInLT.equals=" + DEFAULT_PATH_IN_LT);

        // Get all the scenarioFileList where pathInLT equals to UPDATED_PATH_IN_LT
        defaultScenarioFileShouldNotBeFound("pathInLT.equals=" + UPDATED_PATH_IN_LT);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByPathInLTIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where pathInLT in DEFAULT_PATH_IN_LT or UPDATED_PATH_IN_LT
        defaultScenarioFileShouldBeFound("pathInLT.in=" + DEFAULT_PATH_IN_LT + "," + UPDATED_PATH_IN_LT);

        // Get all the scenarioFileList where pathInLT equals to UPDATED_PATH_IN_LT
        defaultScenarioFileShouldNotBeFound("pathInLT.in=" + UPDATED_PATH_IN_LT);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByPathInLTIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where pathInLT is not null
        defaultScenarioFileShouldBeFound("pathInLT.specified=true");

        // Get all the scenarioFileList where pathInLT is null
        defaultScenarioFileShouldNotBeFound("pathInLT.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByFileTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where fileType equals to DEFAULT_FILE_TYPE
        defaultScenarioFileShouldBeFound("fileType.equals=" + DEFAULT_FILE_TYPE);

        // Get all the scenarioFileList where fileType equals to UPDATED_FILE_TYPE
        defaultScenarioFileShouldNotBeFound("fileType.equals=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByFileTypeIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where fileType in DEFAULT_FILE_TYPE or UPDATED_FILE_TYPE
        defaultScenarioFileShouldBeFound("fileType.in=" + DEFAULT_FILE_TYPE + "," + UPDATED_FILE_TYPE);

        // Get all the scenarioFileList where fileType equals to UPDATED_FILE_TYPE
        defaultScenarioFileShouldNotBeFound("fileType.in=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByFileTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where fileType is not null
        defaultScenarioFileShouldBeFound("fileType.specified=true");

        // Get all the scenarioFileList where fileType is null
        defaultScenarioFileShouldNotBeFound("fileType.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByFormatIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where format equals to DEFAULT_FORMAT
        defaultScenarioFileShouldBeFound("format.equals=" + DEFAULT_FORMAT);

        // Get all the scenarioFileList where format equals to UPDATED_FORMAT
        defaultScenarioFileShouldNotBeFound("format.equals=" + UPDATED_FORMAT);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByFormatIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where format in DEFAULT_FORMAT or UPDATED_FORMAT
        defaultScenarioFileShouldBeFound("format.in=" + DEFAULT_FORMAT + "," + UPDATED_FORMAT);

        // Get all the scenarioFileList where format equals to UPDATED_FORMAT
        defaultScenarioFileShouldNotBeFound("format.in=" + UPDATED_FORMAT);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByFormatIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where format is not null
        defaultScenarioFileShouldBeFound("format.specified=true");

        // Get all the scenarioFileList where format is null
        defaultScenarioFileShouldNotBeFound("format.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenarioFilesBySubSystemAtOriginOfDataIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where subSystemAtOriginOfData equals to DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA
        defaultScenarioFileShouldBeFound("subSystemAtOriginOfData.equals=" + DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);

        // Get all the scenarioFileList where subSystemAtOriginOfData equals to UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA
        defaultScenarioFileShouldNotBeFound("subSystemAtOriginOfData.equals=" + UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesBySubSystemAtOriginOfDataIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where subSystemAtOriginOfData in DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA or UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA
        defaultScenarioFileShouldBeFound("subSystemAtOriginOfData.in=" + DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA + "," + UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA);

        // Get all the scenarioFileList where subSystemAtOriginOfData equals to UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA
        defaultScenarioFileShouldNotBeFound("subSystemAtOriginOfData.in=" + UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesBySubSystemAtOriginOfDataIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where subSystemAtOriginOfData is not null
        defaultScenarioFileShouldBeFound("subSystemAtOriginOfData.specified=true");

        // Get all the scenarioFileList where subSystemAtOriginOfData is null
        defaultScenarioFileShouldNotBeFound("subSystemAtOriginOfData.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByTimeOfDataIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where timeOfData equals to DEFAULT_TIME_OF_DATA
        defaultScenarioFileShouldBeFound("timeOfData.equals=" + DEFAULT_TIME_OF_DATA);

        // Get all the scenarioFileList where timeOfData equals to UPDATED_TIME_OF_DATA
        defaultScenarioFileShouldNotBeFound("timeOfData.equals=" + UPDATED_TIME_OF_DATA);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByTimeOfDataIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where timeOfData is not null
        defaultScenarioFileShouldBeFound("timeOfData.specified=true");

        // Get all the scenarioFileList where timeOfData is null
        defaultScenarioFileShouldNotBeFound("timeOfData.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByTimeOfDataIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where timeOfData is greater than or equal to DEFAULT_TIME_OF_DATA
        defaultScenarioFileShouldBeFound("timeOfData.greaterThanOrEqual=" + DEFAULT_TIME_OF_DATA);

        // Get all the scenarioFileList where timeOfData is greater than or equal to UPDATED_TIME_OF_DATA
        defaultScenarioFileShouldNotBeFound("timeOfData.greaterThanOrEqual=" + UPDATED_TIME_OF_DATA);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByTimeOfDataIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where timeOfData is less than or equal to DEFAULT_TIME_OF_DATA
        defaultScenarioFileShouldBeFound("timeOfData.lessThanOrEqual=" + DEFAULT_TIME_OF_DATA);

        // Get all the scenarioFileList where timeOfData is less than or equal to SMALLER_TIME_OF_DATA
        defaultScenarioFileShouldNotBeFound("timeOfData.lessThanOrEqual=" + SMALLER_TIME_OF_DATA);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByTimeOfDataIsLessThanSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where timeOfData is less than DEFAULT_TIME_OF_DATA
        defaultScenarioFileShouldNotBeFound("timeOfData.lessThan=" + DEFAULT_TIME_OF_DATA);

        // Get all the scenarioFileList where timeOfData is less than UPDATED_TIME_OF_DATA
        defaultScenarioFileShouldBeFound("timeOfData.lessThan=" + UPDATED_TIME_OF_DATA);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByTimeOfDataIsGreaterThanSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where timeOfData is greater than DEFAULT_TIME_OF_DATA
        defaultScenarioFileShouldNotBeFound("timeOfData.greaterThan=" + DEFAULT_TIME_OF_DATA);

        // Get all the scenarioFileList where timeOfData is greater than SMALLER_TIME_OF_DATA
        defaultScenarioFileShouldBeFound("timeOfData.greaterThan=" + SMALLER_TIME_OF_DATA);
    }


    @Test
    @Transactional
    public void getAllScenarioFilesBySecurityLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where securityLevel equals to DEFAULT_SECURITY_LEVEL
        defaultScenarioFileShouldBeFound("securityLevel.equals=" + DEFAULT_SECURITY_LEVEL);

        // Get all the scenarioFileList where securityLevel equals to UPDATED_SECURITY_LEVEL
        defaultScenarioFileShouldNotBeFound("securityLevel.equals=" + UPDATED_SECURITY_LEVEL);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesBySecurityLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where securityLevel is not null
        defaultScenarioFileShouldBeFound("securityLevel.specified=true");

        // Get all the scenarioFileList where securityLevel is null
        defaultScenarioFileShouldNotBeFound("securityLevel.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByCrcIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where crc equals to DEFAULT_CRC
        defaultScenarioFileShouldBeFound("crc.equals=" + DEFAULT_CRC);

        // Get all the scenarioFileList where crc equals to UPDATED_CRC
        defaultScenarioFileShouldNotBeFound("crc.equals=" + UPDATED_CRC);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByCrcIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where crc in DEFAULT_CRC or UPDATED_CRC
        defaultScenarioFileShouldBeFound("crc.in=" + DEFAULT_CRC + "," + UPDATED_CRC);

        // Get all the scenarioFileList where crc equals to UPDATED_CRC
        defaultScenarioFileShouldNotBeFound("crc.in=" + UPDATED_CRC);
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByCrcIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        // Get all the scenarioFileList where crc is not null
        defaultScenarioFileShouldBeFound("crc.specified=true");

        // Get all the scenarioFileList where crc is null
        defaultScenarioFileShouldNotBeFound("crc.specified=false");
    }

    @Test
    @Transactional
    public void getAllScenarioFilesByOwnerIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);
        User owner = UserResourceIT.createEntity(em);
        em.persist(owner);
        em.flush();
        scenarioFile.setOwner(owner);
        scenarioFileRepository.saveAndFlush(scenarioFile);
        Long ownerId = owner.getId();

        // Get all the scenarioFileList where owner equals to ownerId
        defaultScenarioFileShouldBeFound("ownerId.equals=" + ownerId);

        // Get all the scenarioFileList where owner equals to ownerId + 1
        defaultScenarioFileShouldNotBeFound("ownerId.equals=" + (ownerId + 1));
    }


    @Test
    @Transactional
    public void getAllScenarioFilesByScenariosIsEqualToSomething() throws Exception {
        // Initialize the owner
        User owner = UserResourceIT.createOwner(em, userRepository, userService);
        scenarioFileRepository.saveAndFlush(scenarioFile);
        Scenario scenarios = ScenarioResourceIT.createEntity(em);
        scenarios.setOwner(owner);
        em.persist(scenarios);
        em.flush();
        scenarioFile.addScenarios(scenarios);
        scenarioFileRepository.saveAndFlush(scenarioFile);
        Long scenariosId = scenarios.getId();

        // Get all the scenarioFileList where scenarios equals to scenariosId
        defaultScenarioFileShouldBeFound("scenariosId.equals=" + scenariosId);

        // Get all the scenarioFileList where scenarios equals to scenariosId + 1
        defaultScenarioFileShouldNotBeFound("scenariosId.equals=" + (scenariosId + 1));
    }


    @Test
    @Transactional
    public void getAllScenarioFilesByDatasetIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);
        DataSet dataset = DataSetResourceIT.createEntity(em);
        em.persist(dataset);
        em.flush();
        scenarioFile.setDataset(dataset);
        scenarioFileRepository.saveAndFlush(scenarioFile);
        Long datasetId = dataset.getId();

        // Get all the scenarioFileList where dataset equals to datasetId
        defaultScenarioFileShouldBeFound("datasetId.equals=" + datasetId);

        // Get all the scenarioFileList where dataset equals to datasetId + 1
        defaultScenarioFileShouldNotBeFound("datasetId.equals=" + (datasetId + 1));
    }


    @Test
    @Transactional
    public void getAllScenarioFilesByConfigDatasetIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);
        ConfigDataSet configDataset = ConfigDataSetResourceIT.createEntity(em);
        em.persist(configDataset);
        em.flush();
        scenarioFile.setConfigDataset(configDataset);
        scenarioFileRepository.saveAndFlush(scenarioFile);
        Long configDatasetId = configDataset.getId();

        // Get all the scenarioFileList where configDataset equals to configDatasetId
        defaultScenarioFileShouldBeFound("configDatasetId.equals=" + configDatasetId);

        // Get all the scenarioFileList where configDataset equals to configDatasetId + 1
        defaultScenarioFileShouldNotBeFound("configDatasetId.equals=" + (configDatasetId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScenarioFileShouldBeFound(String filter) throws Exception {
        restScenarioFileMockMvc.perform(get("/api/scenario-files?sort=relativePathInST,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].inputType").value(hasItem(DEFAULT_INPUT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].relativePathInST").value(hasItem(DEFAULT_RELATIVE_PATH_IN_ST)))
            .andExpect(jsonPath("$.[*].lTInsertionDate").value(hasItem(DEFAULT_L_T_INSERTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].pathInLT").value(hasItem(DEFAULT_PATH_IN_LT)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT)))
            .andExpect(jsonPath("$.[*].subSystemAtOriginOfData").value(hasItem(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA)))
            .andExpect(jsonPath("$.[*].timeOfData").value(hasItem(DEFAULT_TIME_OF_DATA.toString())))
            .andExpect(jsonPath("$.[*].securityLevel").value(hasItem(DEFAULT_SECURITY_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].crc").value(hasItem(DEFAULT_CRC)));

        // Check, that the count call also returns 1
        restScenarioFileMockMvc.perform(get("/api/scenario-files/count?sort=relativePathInST,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScenarioFileShouldNotBeFound(String filter) throws Exception {
        restScenarioFileMockMvc.perform(get("/api/scenario-files?sort=relativePathInST,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScenarioFileMockMvc.perform(get("/api/scenario-files/count?sort=relativePathInST,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingScenarioFile() throws Exception {
        // Get the scenarioFile
        restScenarioFileMockMvc.perform(get("/api/scenario-files/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScenarioFile() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        //add admin authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        int databaseSizeBeforeUpdate = scenarioFileRepository.findAll().size();

        // Update the scenarioFile
        ScenarioFile updatedScenarioFile = scenarioFileRepository.findById(scenarioFile.getRelativePathInST()).get();
        // Disconnect from session so that the updates on updatedScenarioFile are not directly saved in db
        em.detach(updatedScenarioFile);
        updatedScenarioFile
            .inputType(UPDATED_INPUT_TYPE)
            .lTInsertionDate(UPDATED_L_T_INSERTION_DATE)
            .pathInLT(UPDATED_PATH_IN_LT)
            .fileType(UPDATED_FILE_TYPE)
            .format(UPDATED_FORMAT)
            .subSystemAtOriginOfData(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA)
            .timeOfData(UPDATED_TIME_OF_DATA)
            .securityLevel(UPDATED_SECURITY_LEVEL)
            .crc(UPDATED_CRC);
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(updatedScenarioFile);

        restScenarioFileMockMvc.perform(put("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isOk());

        // Validate the ScenarioFile in the database
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeUpdate);
        ScenarioFile testScenarioFile = scenarioFileList.get(scenarioFileList.size() - 1);
        assertThat(testScenarioFile.getInputType()).isEqualTo(UPDATED_INPUT_TYPE);
        assertThat(testScenarioFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testScenarioFile.getlTInsertionDate()).isEqualTo(UPDATED_L_T_INSERTION_DATE);
        assertThat(testScenarioFile.getPathInLT()).isEqualTo(UPDATED_PATH_IN_LT);
        assertThat(testScenarioFile.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testScenarioFile.getFormat()).isEqualTo(UPDATED_FORMAT);
        assertThat(testScenarioFile.getSubSystemAtOriginOfData()).isEqualTo(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testScenarioFile.getTimeOfData()).isEqualTo(UPDATED_TIME_OF_DATA);
        assertThat(testScenarioFile.getSecurityLevel()).isEqualTo(UPDATED_SECURITY_LEVEL);
        assertThat(testScenarioFile.getCrc()).isEqualTo(UPDATED_CRC);
    }

    @Test
    @Transactional
    public void updateNonExistingScenarioFile() throws Exception {
        int databaseSizeBeforeUpdate = scenarioFileRepository.findAll().size();

        // Create the ScenarioFile
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(scenarioFile);

        // If the entity doesn't already saved in database, it will throw BadRequestAlertException
        restScenarioFileMockMvc.perform(put("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ScenarioFile in the database
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteNonExistentScenarioFile() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        int databaseSizeBeforeDelete = scenarioFileRepository.findAll().size();

        //add user authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", authorities));
        SecurityContextHolder.setContext(securityContext);

        ScenarioFileDTO scenarioFileDTO = new ScenarioFileDTO();
        scenarioFileDTO.setRelativePathInST(UPDATED_RELATIVE_PATH_IN_ST);

        // Delete the scenarioFile
        restScenarioFileMockMvc.perform(delete("/api/scenario-files/")
            .accept(TestUtil.APPLICATION_JSON_UTF8)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the database contains same number of items
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScenarioFile.class);
        ScenarioFile scenarioFile1 = new ScenarioFile();
        scenarioFile1.setRelativePathInST(DEFAULT_RELATIVE_PATH_IN_ST);
        ScenarioFile scenarioFile2 = new ScenarioFile();
        scenarioFile2.setRelativePathInST(scenarioFile1.getRelativePathInST());
        assertThat(scenarioFile1).isEqualTo(scenarioFile2);
        scenarioFile2.setRelativePathInST(UPDATED_RELATIVE_PATH_IN_ST);
        assertThat(scenarioFile1).isNotEqualTo(scenarioFile2);
        scenarioFile1.setRelativePathInST(null);
        assertThat(scenarioFile1).isNotEqualTo(scenarioFile2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScenarioFileDTO.class);
        ScenarioFileDTO scenarioFileDTO1 = new ScenarioFileDTO();
        scenarioFileDTO1.setRelativePathInST(DEFAULT_RELATIVE_PATH_IN_ST);
        ScenarioFileDTO scenarioFileDTO2 = new ScenarioFileDTO();
        assertThat(scenarioFileDTO1).isNotEqualTo(scenarioFileDTO2);
        scenarioFileDTO2.setRelativePathInST(scenarioFileDTO1.getRelativePathInST());
        assertThat(scenarioFileDTO1).isEqualTo(scenarioFileDTO2);
        scenarioFileDTO2.setRelativePathInST(UPDATED_RELATIVE_PATH_IN_ST);
        assertThat(scenarioFileDTO1).isNotEqualTo(scenarioFileDTO2);
        scenarioFileDTO1.setRelativePathInST(null);
        assertThat(scenarioFileDTO1).isNotEqualTo(scenarioFileDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(scenarioFileMapper.fromId(DEFAULT_RELATIVE_PATH_IN_ST).getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(scenarioFileMapper.fromId(null)).isNull();
    }

    /*@Test
    @Transactional
    public void getScenarioFilesByOutputFileRelativePathInST() throws Exception {
        // Initialize the database
        OutputFile outputFile = initializeScenarioFileAndDatabase();
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(outputFile);

        restScenarioFileMockMvc.perform(post("/api/scenario-files-by-outputfile/")
            .accept(TestUtil.APPLICATION_JSON_UTF8)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isOk());

        // Get the empty scenarioFileList where OutputFile RelativePathInST has no scenarioFile
        scenarioFile.setScenarios(null);
        em.persist(scenarioFile);
        em.flush();
        restScenarioFileMockMvc.perform(post("/api/scenario-files-by-outputfile/")
            .accept(TestUtil.APPLICATION_JSON_UTF8)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isInternalServerError());

        // Get all the scenarioFileList where OutputFile RelativePathInST is wrong
        outputFileShouldNotBeFound("wrong");
    }*/

    private void outputFileShouldNotBeFound(String wrongRelativePathInST) throws Exception {
        restScenarioFileMockMvc.perform(get("/api/scenario-files-by-outputfile/" + wrongRelativePathInST))
            .andExpect(status().is5xxServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    private OutputFile initializeScenarioFileAndDatabase() {
        // Initialize the owner
        User owner = UserResourceIT.createOwner(em, userRepository, userService);

        // Initialize the outputfile
        Scenario scenario = ScenarioResourceIT.createEntity(em);
        scenario.setOwner(owner);
        em.persist(scenario);
        em.flush();
        Set<Scenario> scenarios = new HashSet<>();
        scenarios.add(scenario);

        scenarioFile = ScenarioFileResourceIT.createEntity(em);
        scenarioFile.setScenarios(scenarios);
        scenarioFile.setOwner(owner);
        em.persist(scenarioFile);
        em.flush();

        Run run = RunResourceIT.createEntity(em);
        run.setScenario(scenario);
        run.setOwner(owner);
        em.persist(run);
        em.flush();

        OutputFile outputFile = OutputFileResourceIT.createEntity(em);
        outputFile.setRun(run);
        outputFile.setOwner(owner);
        outputFile.setRelativePathInST("path");
        em.persist(outputFile);
        em.flush();

        return outputFile;
    }

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void checkAutoCompletionOfLtInsertionDate() throws Exception {
        int databaseSizeBeforeCreate = scenarioFileRepository.findAll().size();
        // set the field null
        scenarioFile.setlTInsertionDate(null);

        // Create the OutputFile
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(scenarioFile);
        restScenarioFileMockMvc.perform(post("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isCreated());

        // Validate the OutputFile in the database
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeCreate + 1);
        ScenarioFile testScenarioFile = scenarioFileList.get(scenarioFileList.size() - 1);
        assertThat(testScenarioFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testScenarioFile.getlTInsertionDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter));
        assertThat(testScenarioFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testScenarioFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testScenarioFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testScenarioFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testScenarioFile.getTimeOfData()).isEqualTo(DEFAULT_TIME_OF_DATA);
        assertThat(testScenarioFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testScenarioFile.getCrc()).isEqualTo(DEFAULT_CRC);
    }*/

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void checkTimeOfDataIsNotRequired() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        int databaseSizeBeforeUpdate = scenarioFileRepository.findAll().size();

        // Update the scenarioFile
        ScenarioFile updatedScenarioFile = scenarioFileRepository.findById(scenarioFile.getRelativePathInST()).get();
        // Disconnect from session so that the updates on updatedScenarioFile are not directly saved in db
        em.detach(updatedScenarioFile);
        updatedScenarioFile
            .inputType(UPDATED_INPUT_TYPE)
            .lTInsertionDate(UPDATED_L_T_INSERTION_DATE)
            .pathInLT(UPDATED_PATH_IN_LT)
            .fileType(UPDATED_FILE_TYPE)
            .format(UPDATED_FORMAT)
            .subSystemAtOriginOfData(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA)
            .timeOfData(UPDATED_TIME_OF_DATA)
            .securityLevel(UPDATED_SECURITY_LEVEL)
            .crc(UPDATED_CRC);
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(updatedScenarioFile);

        restScenarioFileMockMvc.perform(put("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isOk());

        // Validate the ScenarioFile in the database
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeUpdate);
        ScenarioFile testScenarioFile = scenarioFileList.get(scenarioFileList.size() - 1);
        assertThat(testScenarioFile.getInputType()).isEqualTo(UPDATED_INPUT_TYPE);
        assertThat(testScenarioFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testScenarioFile.getlTInsertionDate()).isEqualTo(UPDATED_L_T_INSERTION_DATE);
        assertThat(testScenarioFile.getPathInLT()).isEqualTo(UPDATED_PATH_IN_LT);
        assertThat(testScenarioFile.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testScenarioFile.getFormat()).isEqualTo(UPDATED_FORMAT);
        assertThat(testScenarioFile.getSubSystemAtOriginOfData()).isEqualTo(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testScenarioFile.getTimeOfData()).isEqualTo(UPDATED_TIME_OF_DATA);
        assertThat(testScenarioFile.getSecurityLevel()).isEqualTo(UPDATED_SECURITY_LEVEL);
        assertThat(testScenarioFile.getCrc()).isEqualTo(UPDATED_CRC);
    }*/

    @Test
    @Transactional
    public void checkTimeOfDataIsNotRequired() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        int databaseSizeBeforeUpdate = scenarioFileRepository.findAll().size();

        //add admin authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        // Update the scenarioFile
        ScenarioFile updatedScenarioFile = scenarioFileRepository.findById(scenarioFile.getRelativePathInST()).get();
        // Disconnect from session so that the updates on updatedScenarioFile are not directly saved in db
        em.detach(updatedScenarioFile);
        updatedScenarioFile
            .lTInsertionDate(UPDATED_L_T_INSERTION_DATE)
            .pathInLT(UPDATED_PATH_IN_LT)
            .fileType(UPDATED_FILE_TYPE)
            .format(UPDATED_FORMAT)
            .subSystemAtOriginOfData(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA)
            .timeOfData(null)
            .securityLevel(UPDATED_SECURITY_LEVEL)
            .crc(UPDATED_CRC);
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(updatedScenarioFile);

        restScenarioFileMockMvc.perform(put("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isOk());

        // Validate the ScenarioFile in the database
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeUpdate);
        ScenarioFile testScenarioFile = scenarioFileList.get(scenarioFileList.size() - 1);
        assertThat(testScenarioFile.getInputType()).isEqualTo(DEFAULT_INPUT_TYPE);
        assertThat(testScenarioFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testScenarioFile.getlTInsertionDate()).isEqualTo(UPDATED_L_T_INSERTION_DATE);
        assertThat(testScenarioFile.getPathInLT()).isEqualTo(UPDATED_PATH_IN_LT);
        assertThat(testScenarioFile.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testScenarioFile.getFormat()).isEqualTo(UPDATED_FORMAT);
        assertThat(testScenarioFile.getSubSystemAtOriginOfData()).isEqualTo(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testScenarioFile.getTimeOfData()).isNull();
        assertThat(testScenarioFile.getSecurityLevel()).isEqualTo(UPDATED_SECURITY_LEVEL);
        assertThat(testScenarioFile.getCrc()).isEqualTo(UPDATED_CRC);
    }

    @Test
    @Transactional
    public void checkUserWithoutAdminRightsCantDelete() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        int databaseSizeBeforeTest = scenarioFileRepository.findAll().size();

        //add user authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);
        ScenarioFileDTO scenarioFileDTO = new ScenarioFileDTO();
        scenarioFileDTO.setRelativePathInST(scenarioFile.getRelativePathInST());

        // Delete the scenarioFile
        restScenarioFileMockMvc.perform(delete("/api/scenario-files/")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isForbidden());

        // Validate the ScenarioFile in the database
        List<ScenarioFile> outputFileList = scenarioFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeTest);
        ScenarioFile testScenarioFile = outputFileList.get(outputFileList.size() - 1);
        assertThat(testScenarioFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testScenarioFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testScenarioFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testScenarioFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testScenarioFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testScenarioFile.getTimeOfData()).isEqualTo(DEFAULT_TIME_OF_DATA);
        assertThat(testScenarioFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testScenarioFile.getCrc()).isEqualTo(DEFAULT_CRC);
    }

    @Test
    @Transactional
    public void updateScenarioFileWithoutAdminRights() throws Exception {
        // Initialize the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        //add user authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        int databaseSizeBeforeUpdate = scenarioFileRepository.findAll().size();

        // Update the scenarioFile
        ScenarioFile updatedScenarioFile = scenarioFileRepository.findById(scenarioFile.getRelativePathInST()).get();
        // Disconnect from session so that the updates on updatedScenarioFile are not directly saved in db
        em.detach(updatedScenarioFile);
        updatedScenarioFile
            .inputType(UPDATED_INPUT_TYPE)
            .lTInsertionDate(UPDATED_L_T_INSERTION_DATE)
            .pathInLT(UPDATED_PATH_IN_LT)
            .fileType(UPDATED_FILE_TYPE)
            .format(UPDATED_FORMAT)
            .subSystemAtOriginOfData(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA)
            .timeOfData(UPDATED_TIME_OF_DATA)
            .securityLevel(UPDATED_SECURITY_LEVEL)
            .crc(UPDATED_CRC);
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(updatedScenarioFile);

        // update should be forbidden
        restScenarioFileMockMvc.perform(put("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isOk());

        // Validate the ScenarioFile in the database
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeUpdate);
        ScenarioFile testScenarioFile = scenarioFileList.get(scenarioFileList.size() - 1);
        assertThat(testScenarioFile.getInputType()).isEqualTo(DEFAULT_INPUT_TYPE);
        assertThat(testScenarioFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testScenarioFile.getlTInsertionDate()).isEqualTo(DEFAULT_L_T_INSERTION_DATE);
        assertThat(testScenarioFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testScenarioFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testScenarioFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testScenarioFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testScenarioFile.getTimeOfData()).isEqualTo(DEFAULT_TIME_OF_DATA);
        assertThat(testScenarioFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testScenarioFile.getCrc()).isEqualTo(DEFAULT_CRC);
    }

    @Test
    @Transactional
    public void checkCannotUpdateINPUTFileWithConfigDataset() throws Exception {
        // Initialize an inputFile and the database
        scenarioFileRepository.saveAndFlush(scenarioFile);

        //add admin authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        int databaseSizeBeforeUpdate = scenarioFileRepository.findAll().size();

        // Update the scenarioFile
        ScenarioFile updatedScenarioFile = scenarioFileRepository.findById(scenarioFile.getRelativePathInST()).get();
        // Disconnect from session so that the updates on updatedScenarioFile are not directly saved in db
        em.detach(updatedScenarioFile);
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(updatedScenarioFile);
        scenarioFileDTO.setConfigDatasetId(1L);

        restScenarioFileMockMvc.perform(put("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isInternalServerError());

        // Validate the ScenarioFile in the database
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeUpdate);
        ScenarioFile testScenarioFile = scenarioFileList.get(scenarioFileList.size() - 1);
        assertThat(testScenarioFile.getInputType()).isEqualTo(DEFAULT_INPUT_TYPE);
        assertThat(testScenarioFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testScenarioFile.getlTInsertionDate()).isEqualTo(DEFAULT_L_T_INSERTION_DATE);
        assertThat(testScenarioFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testScenarioFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testScenarioFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testScenarioFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testScenarioFile.getTimeOfData()).isEqualTo(DEFAULT_TIME_OF_DATA);
        assertThat(testScenarioFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testScenarioFile.getCrc()).isEqualTo(DEFAULT_CRC);
        assertThat(testScenarioFile.getConfigDataset()).isEqualTo(null);
    }

    @Test
    @Transactional
    public void checkCannotUpdateCONFIGFileWithDataset() throws Exception {
        // Initialize a config file and the database
        scenarioFile.setInputType(UPDATED_INPUT_TYPE);
        scenarioFileRepository.saveAndFlush(scenarioFile);

        //add admin authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        int databaseSizeBeforeUpdate = scenarioFileRepository.findAll().size();

        // Update the scenarioFile
        ScenarioFile updatedScenarioFile = scenarioFileRepository.findById(scenarioFile.getRelativePathInST()).get();
        // Disconnect from session so that the updates on updatedScenarioFile are not directly saved in db
        em.detach(updatedScenarioFile);
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(updatedScenarioFile);
        scenarioFileDTO.setDatasetId(1L);

        restScenarioFileMockMvc.perform(put("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isInternalServerError());

        // Validate the ScenarioFile in the database
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeUpdate);
        ScenarioFile testScenarioFile = scenarioFileList.get(scenarioFileList.size() - 1);
        assertThat(testScenarioFile.getInputType()).isEqualTo(UPDATED_INPUT_TYPE);
        assertThat(testScenarioFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testScenarioFile.getlTInsertionDate()).isEqualTo(DEFAULT_L_T_INSERTION_DATE);
        assertThat(testScenarioFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testScenarioFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testScenarioFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testScenarioFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testScenarioFile.getTimeOfData()).isEqualTo(DEFAULT_TIME_OF_DATA);
        assertThat(testScenarioFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testScenarioFile.getCrc()).isEqualTo(DEFAULT_CRC);
        assertThat(testScenarioFile.getDataset()).isEqualTo(null);
    }

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void checkPostScenarioFileWithSamePathInLTAndSameCrc() throws Exception {
        scenarioFileRepository.saveAndFlush(scenarioFile);
        int databaseSizeBeforeTest = scenarioFileRepository.findAll().size();

        // Create the ScenarioFile, which fails.
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(scenarioFile);

        restScenarioFileMockMvc.perform(put("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().is5xxServerError());

        // Check that the count call still returns 1
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeTest);
    }*/

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void checkPostScenarioFileWithSamePathInLTButDifferentCrc() throws Exception {
        scenarioFileRepository.saveAndFlush(scenarioFile);
        int databaseSizeBeforeTest = scenarioFileRepository.findAll().size();

        // Create the ScenarioFile, which fails.
        scenarioFile.setCrc(UPDATED_CRC);
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(scenarioFile);

        restScenarioFileMockMvc.perform(put("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().is5xxServerError());

        // Check that the count call still returns 1
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeTest);
    }*/

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void checkAutoCompletionOfOwnerId() throws Exception {
        int databaseSizeBeforeCreate = scenarioFileRepository.findAll().size();
        // set the field null
        scenarioFile.setOwner(null);

        // Create the OutputFile
        ScenarioFileDTO scenarioFileDTO = scenarioFileMapper.toDto(scenarioFile);
        restScenarioFileMockMvc.perform(post("/api/scenario-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scenarioFileDTO)))
            .andExpect(status().isCreated());

        // Validate the OutputFile in the database
        List<ScenarioFile> scenarioFileList = scenarioFileRepository.findAll();
        assertThat(scenarioFileList).hasSize(databaseSizeBeforeCreate + 1);
        ScenarioFile testScenarioFile = scenarioFileList.get(scenarioFileList.size() - 1);
        assertThat(testScenarioFile.getInputType()).isEqualTo(DEFAULT_INPUT_TYPE);
        assertThat(testScenarioFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testScenarioFile.getlTInsertionDate()).isEqualTo(DEFAULT_L_T_INSERTION_DATE);
        assertThat(testScenarioFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testScenarioFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testScenarioFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testScenarioFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testScenarioFile.getTimeOfData()).isEqualTo(DEFAULT_TIME_OF_DATA);
        assertThat(testScenarioFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testScenarioFile.getCrc()).isEqualTo(DEFAULT_CRC);
    }*/
}
