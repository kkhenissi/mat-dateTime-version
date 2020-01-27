package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.ArchivemanagerApp;
import com.airbus.archivemanager.domain.OutputFile;
import com.airbus.archivemanager.domain.Run;
import com.airbus.archivemanager.domain.Scenario;
import com.airbus.archivemanager.domain.User;
import com.airbus.archivemanager.domain.enumeration.RunStatus;
import com.airbus.archivemanager.domain.enumeration.SecurityLevel;
import com.airbus.archivemanager.repository.OutputFileRepository;
import com.airbus.archivemanager.repository.UserRepository;
import com.airbus.archivemanager.security.AuthoritiesConstants;
import com.airbus.archivemanager.service.OutputFileQueryService;
import com.airbus.archivemanager.service.OutputFileService;
import com.airbus.archivemanager.service.UserService;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.mapper.OutputFileMapper;
import com.airbus.archivemanager.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
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
 * Integration tests for the {@link OutputFileResource} REST controller.
 */
@SpringBootTest(classes = ArchivemanagerApp.class)
public class OutputFileResourceIT {

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

    private static final SecurityLevel DEFAULT_SECURITY_LEVEL = SecurityLevel.NORMAL;
    private static final SecurityLevel UPDATED_SECURITY_LEVEL = SecurityLevel.EIC;

    private static final String DEFAULT_CRC = "AAAAAAAAAA";
    private static final String UPDATED_CRC = "BBBBBBBBBB";

    @Autowired
    private OutputFileRepository outputFileRepository;

    @Autowired
    private OutputFileMapper outputFileMapper;

    @Autowired
    private OutputFileService outputFileService;

    @Autowired
    private OutputFileQueryService outputFileQueryService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

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

    private MockMvc restOutputFileMockMvc;

    private OutputFile outputFile;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OutputFile createEntity(EntityManager em) {

        Run run = new Run()
            .startDate(LocalDateTime.of(1980, 1, 1, 1, 1, 1))
            .endDate(LocalDateTime.of(1990, 1, 1, 1, 1, 1))
            .status(RunStatus.UNKNOWN)
            .platformHardwareInfo("AAAAAAAAAA")
            .description("AAAAAAAAAA");


        OutputFile outputFile = new OutputFile()
            .relativePathInST(DEFAULT_RELATIVE_PATH_IN_ST)
            .run(run)
            .lTInsertionDate(DEFAULT_L_T_INSERTION_DATE)
            .pathInLT(DEFAULT_PATH_IN_LT)
            .fileType(DEFAULT_FILE_TYPE)
            .format(DEFAULT_FORMAT)
            .subSystemAtOriginOfData(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA)
            .securityLevel(DEFAULT_SECURITY_LEVEL)
            .crc(DEFAULT_CRC);
        return outputFile;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OutputFile createUpdatedEntity(EntityManager em) {
        OutputFile outputFile = new OutputFile()
            .relativePathInST(UPDATED_RELATIVE_PATH_IN_ST)
            .lTInsertionDate(UPDATED_L_T_INSERTION_DATE)
            .pathInLT(UPDATED_PATH_IN_LT)
            .fileType(UPDATED_FILE_TYPE)
            .format(UPDATED_FORMAT)
            .subSystemAtOriginOfData(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA)
            .securityLevel(UPDATED_SECURITY_LEVEL)
            .crc(UPDATED_CRC);
        return outputFile;
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OutputFileResource outputFileResource = new OutputFileResource(outputFileService, outputFileQueryService, messageSource);
        this.restOutputFileMockMvc = MockMvcBuilders.standaloneSetup(outputFileResource)
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

        // Initialize the run
        Run run = RunResourceIT.createEntity(em);
        run.setScenario(scenario);
        run.setOwner(owner);
        em.persist(run);
        em.flush();
        outputFile = createEntity(em);
        outputFile.setRun(run);
        outputFile.setOwner(owner);
    }

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void createOutputFile() throws Exception {
        int databaseSizeBeforeCreate = outputFileRepository.findAll().size();

        // Create the OutputFile
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(outputFile);
        restOutputFileMockMvc.perform(post("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isCreated());

        // Validate the OutputFile in the database
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeCreate + 1);
        OutputFile testOutputFile = outputFileList.get(outputFileList.size() - 1);
        assertThat(testOutputFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testOutputFile.getlTInsertionDate()).isEqualTo(DEFAULT_L_T_INSERTION_DATE);
        assertThat(testOutputFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testOutputFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testOutputFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testOutputFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testOutputFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testOutputFile.getCrc()).isEqualTo(DEFAULT_CRC);
    }*/

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void createOutputFileWithoutRelativePathInST() throws Exception {
        int databaseSizeBeforeCreate = outputFileRepository.findAll().size();

        // Create the OutputFile without a RelativePathInST
        outputFile.setRelativePathInST(null);
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(outputFile);

        // An entity without a RelativePathInST cannot be created, so this API call must fail
        restOutputFileMockMvc.perform(post("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OutputFile in the database
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeCreate);
    }*/

    @Test
    @Transactional
    public void updateOutputFileWithoutRelativePathInST() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        int databaseSizeBeforeTest = outputFileRepository.findAll().size();

        // Update the outputFile
        OutputFile updatedOutputFile = outputFileRepository.findById(outputFile.getRelativePathInST()).get();

        // Disconnect from session so that the updates on updatedOutputFile are not directly saved in db
        em.detach(updatedOutputFile);

        // set the field null
        updatedOutputFile.setRelativePathInST(null);

        // Create the OutputFile, which fails.
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(updatedOutputFile);

        restOutputFileMockMvc.perform(put("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isBadRequest());

        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOutputFiles() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList
        restOutputFileMockMvc.perform(get("/api/output-files?sort=relativePathInST,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].relativePathInST").value(hasItem(DEFAULT_RELATIVE_PATH_IN_ST)))
            .andExpect(jsonPath("$.[*].lTInsertionDate").value(hasItem(DEFAULT_L_T_INSERTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].pathInLT").value(hasItem(DEFAULT_PATH_IN_LT)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT)))
            .andExpect(jsonPath("$.[*].subSystemAtOriginOfData").value(hasItem(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA)))
            .andExpect(jsonPath("$.[*].securityLevel").value(hasItem(DEFAULT_SECURITY_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].crc").value(hasItem(DEFAULT_CRC)));
    }

    @Test
    @Transactional
    public void getAllOutputFilesByRelativePathInSTIsEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where relativePathInST equals to DEFAULT_RELATIVE_PATH_IN_ST
        defaultOutputFileShouldBeFound("relativePathInST.equals=" + DEFAULT_RELATIVE_PATH_IN_ST);

        // Get all the outputFileList where relativePathInST equals to UPDATED_RELATIVE_PATH_IN_ST
        defaultOutputFileShouldNotBeFound("relativePathInST.equals=" + UPDATED_RELATIVE_PATH_IN_ST);
    }

    @Test
    @Transactional
    public void getAllOutputFilesByRelativePathInSTIsInShouldWork() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where relativePathInST in DEFAULT_RELATIVE_PATH_IN_ST or UPDATED_RELATIVE_PATH_IN_ST
        defaultOutputFileShouldBeFound("relativePathInST.in=" + DEFAULT_RELATIVE_PATH_IN_ST + "," + UPDATED_RELATIVE_PATH_IN_ST);

        // Get all the outputFileList where relativePathInST equals to UPDATED_RELATIVE_PATH_IN_ST
        defaultOutputFileShouldNotBeFound("relativePathInST.in=" + UPDATED_RELATIVE_PATH_IN_ST);
    }

    @Test
    @Transactional
    public void getAllOutputFilesByRelativePathInSTIsNullOrNotNull() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where relativePathInST is not null
        defaultOutputFileShouldBeFound("relativePathInST.specified=true");

        // Get all the outputFileList where relativePathInST is null
        defaultOutputFileShouldNotBeFound("relativePathInST.specified=false");
    }

    @Test
    @Transactional
    public void getAllOutputFilesBylTInsertionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where lTInsertionDate equals to DEFAULT_L_T_INSERTION_DATE
        defaultOutputFileShouldBeFound("lTInsertionDate.equals=" + DEFAULT_L_T_INSERTION_DATE);

        // Get all the outputFileList where lTInsertionDate equals to UPDATED_L_T_INSERTION_DATE
        defaultOutputFileShouldNotBeFound("lTInsertionDate.equals=" + UPDATED_L_T_INSERTION_DATE);
    }

    @Test
    @Transactional
    public void getAllOutputFilesBylTInsertionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where lTInsertionDate is not null
        defaultOutputFileShouldBeFound("lTInsertionDate.specified=true");

        // Get all the outputFileList where lTInsertionDate is null
        defaultOutputFileShouldNotBeFound("lTInsertionDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllOutputFilesBylTInsertionDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where lTInsertionDate is greater than or equal to DEFAULT_L_T_INSERTION_DATE
        defaultOutputFileShouldBeFound("lTInsertionDate.greaterThanOrEqual=" + DEFAULT_L_T_INSERTION_DATE);

        // Get all the outputFileList where lTInsertionDate is greater than or equal to UPDATED_L_T_INSERTION_DATE
        defaultOutputFileShouldNotBeFound("lTInsertionDate.greaterThanOrEqual=" + UPDATED_L_T_INSERTION_DATE);
    }

    @Test
    @Transactional
    public void getAllOutputFilesBylTInsertionDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where lTInsertionDate is less than or equal to DEFAULT_L_T_INSERTION_DATE
        defaultOutputFileShouldBeFound("lTInsertionDate.lessThanOrEqual=" + DEFAULT_L_T_INSERTION_DATE);

        // Get all the outputFileList where lTInsertionDate is less than or equal to SMALLER_L_T_INSERTION_DATE
        defaultOutputFileShouldNotBeFound("lTInsertionDate.lessThanOrEqual=" + SMALLER_L_T_INSERTION_DATE);
    }

    @Test
    @Transactional
    public void getAllOutputFilesBylTInsertionDateIsLessThanSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where lTInsertionDate is less than DEFAULT_L_T_INSERTION_DATE
        defaultOutputFileShouldNotBeFound("lTInsertionDate.lessThan=" + DEFAULT_L_T_INSERTION_DATE);

        // Get all the outputFileList where lTInsertionDate is less than UPDATED_L_T_INSERTION_DATE
        defaultOutputFileShouldBeFound("lTInsertionDate.lessThan=" + UPDATED_L_T_INSERTION_DATE);
    }

    @Test
    @Transactional
    public void getAllOutputFilesBylTInsertionDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where lTInsertionDate is greater than DEFAULT_L_T_INSERTION_DATE
        defaultOutputFileShouldNotBeFound("lTInsertionDate.greaterThan=" + DEFAULT_L_T_INSERTION_DATE);

        // Get all the outputFileList where lTInsertionDate is greater than SMALLER_L_T_INSERTION_DATE
        defaultOutputFileShouldBeFound("lTInsertionDate.greaterThan=" + SMALLER_L_T_INSERTION_DATE);
    }


    @Test
    @Transactional
    public void getAllOutputFilesByPathInLTIsEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where pathInLT equals to DEFAULT_PATH_IN_LT
        defaultOutputFileShouldBeFound("pathInLT.equals=" + DEFAULT_PATH_IN_LT);

        // Get all the outputFileList where pathInLT equals to UPDATED_PATH_IN_LT
        defaultOutputFileShouldNotBeFound("pathInLT.equals=" + UPDATED_PATH_IN_LT);
    }

    @Test
    @Transactional
    public void getAllOutputFilesByPathInLTIsInShouldWork() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where pathInLT in DEFAULT_PATH_IN_LT or UPDATED_PATH_IN_LT
        defaultOutputFileShouldBeFound("pathInLT.in=" + DEFAULT_PATH_IN_LT + "," + UPDATED_PATH_IN_LT);

        // Get all the outputFileList where pathInLT equals to UPDATED_PATH_IN_LT
        defaultOutputFileShouldNotBeFound("pathInLT.in=" + UPDATED_PATH_IN_LT);
    }

    @Test
    @Transactional
    public void getAllOutputFilesByPathInLTIsNullOrNotNull() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where pathInLT is not null
        defaultOutputFileShouldBeFound("pathInLT.specified=true");

        // Get all the outputFileList where pathInLT is null
        defaultOutputFileShouldNotBeFound("pathInLT.specified=false");
    }

    @Test
    @Transactional
    public void getAllOutputFilesByFileTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where fileType equals to DEFAULT_FILE_TYPE
        defaultOutputFileShouldBeFound("fileType.equals=" + DEFAULT_FILE_TYPE);

        // Get all the outputFileList where fileType equals to UPDATED_FILE_TYPE
        defaultOutputFileShouldNotBeFound("fileType.equals=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    public void getAllOutputFilesByFileTypeIsInShouldWork() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where fileType in DEFAULT_FILE_TYPE or UPDATED_FILE_TYPE
        defaultOutputFileShouldBeFound("fileType.in=" + DEFAULT_FILE_TYPE + "," + UPDATED_FILE_TYPE);

        // Get all the outputFileList where fileType equals to UPDATED_FILE_TYPE
        defaultOutputFileShouldNotBeFound("fileType.in=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    public void getAllOutputFilesByFileTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where fileType is not null
        defaultOutputFileShouldBeFound("fileType.specified=true");

        // Get all the outputFileList where fileType is null
        defaultOutputFileShouldNotBeFound("fileType.specified=false");
    }

    @Test
    @Transactional
    public void getAllOutputFilesByFormatIsEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where format equals to DEFAULT_FORMAT
        defaultOutputFileShouldBeFound("format.equals=" + DEFAULT_FORMAT);

        // Get all the outputFileList where format equals to UPDATED_FORMAT
        defaultOutputFileShouldNotBeFound("format.equals=" + UPDATED_FORMAT);
    }

    @Test
    @Transactional
    public void getAllOutputFilesByFormatIsInShouldWork() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where format in DEFAULT_FORMAT or UPDATED_FORMAT
        defaultOutputFileShouldBeFound("format.in=" + DEFAULT_FORMAT + "," + UPDATED_FORMAT);

        // Get all the outputFileList where format equals to UPDATED_FORMAT
        defaultOutputFileShouldNotBeFound("format.in=" + UPDATED_FORMAT);
    }

    @Test
    @Transactional
    public void getAllOutputFilesByFormatIsNullOrNotNull() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where format is not null
        defaultOutputFileShouldBeFound("format.specified=true");

        // Get all the outputFileList where format is null
        defaultOutputFileShouldNotBeFound("format.specified=false");
    }

    @Test
    @Transactional
    public void getAllOutputFilesBySubSystemAtOriginOfDataIsEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where subSystemAtOriginOfData equals to DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA
        defaultOutputFileShouldBeFound("subSystemAtOriginOfData.equals=" + DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);

        // Get all the outputFileList where subSystemAtOriginOfData equals to UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA
        defaultOutputFileShouldNotBeFound("subSystemAtOriginOfData.equals=" + UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
    }

    @Test
    @Transactional
    public void getAllOutputFilesBySubSystemAtOriginOfDataIsInShouldWork() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where subSystemAtOriginOfData in DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA or UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA
        defaultOutputFileShouldBeFound("subSystemAtOriginOfData.in=" + DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA + "," + UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA);

        // Get all the outputFileList where subSystemAtOriginOfData equals to UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA
        defaultOutputFileShouldNotBeFound("subSystemAtOriginOfData.in=" + UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
    }

    @Test
    @Transactional
    public void getAllOutputFilesBySubSystemAtOriginOfDataIsNullOrNotNull() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where subSystemAtOriginOfData is not null
        defaultOutputFileShouldBeFound("subSystemAtOriginOfData.specified=true");

        // Get all the outputFileList where subSystemAtOriginOfData is null
        defaultOutputFileShouldNotBeFound("subSystemAtOriginOfData.specified=false");
    }

    @Test
    @Transactional
    public void getAllOutputFilesBySecurityLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where securityLevel equals to DEFAULT_SECURITY_LEVEL
        defaultOutputFileShouldBeFound("securityLevel.equals=" + DEFAULT_SECURITY_LEVEL);

        // Get all the outputFileList where securityLevel equals to UPDATED_SECURITY_LEVEL
        defaultOutputFileShouldNotBeFound("securityLevel.equals=" + UPDATED_SECURITY_LEVEL);
    }

    @Test
    @Transactional
    public void getAllOutputFilesBySecurityLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where securityLevel is not null
        defaultOutputFileShouldBeFound("securityLevel.specified=true");

        // Get all the outputFileList where securityLevel is null
        defaultOutputFileShouldNotBeFound("securityLevel.specified=false");
    }

    @Test
    @Transactional
    public void getAllOutputFilesByCrcIsEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where crc equals to DEFAULT_CRC
        defaultOutputFileShouldBeFound("crc.equals=" + DEFAULT_CRC);

        // Get all the outputFileList where crc equals to UPDATED_CRC
        defaultOutputFileShouldNotBeFound("crc.equals=" + UPDATED_CRC);
    }

    @Test
    @Transactional
    public void getAllOutputFilesByCrcIsInShouldWork() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where crc in DEFAULT_CRC or UPDATED_CRC
        defaultOutputFileShouldBeFound("crc.in=" + DEFAULT_CRC + "," + UPDATED_CRC);

        // Get all the outputFileList where crc equals to UPDATED_CRC
        defaultOutputFileShouldNotBeFound("crc.in=" + UPDATED_CRC);
    }

    @Test
    @Transactional
    public void getAllOutputFilesByCrcIsNullOrNotNull() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        // Get all the outputFileList where crc is not null
        defaultOutputFileShouldBeFound("crc.specified=true");

        // Get all the outputFileList where crc is null
        defaultOutputFileShouldNotBeFound("crc.specified=false");
    }

    @Test
    @Transactional
    public void getAllOutputFilesByOwnerIsEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);
        Long ownerId = outputFile.getOwner().getId();

        // Get all the outputFileList where owner equals to ownerId
        defaultOutputFileShouldBeFound("ownerId.equals=" + ownerId);

        // Get all the outputFileList where owner equals to ownerId + 1
        defaultOutputFileShouldNotBeFound("ownerId.equals=" + (ownerId + 1));
    }


    @Test
    @Transactional
    public void getAllOutputFilesByRunIsEqualToSomething() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);
        Run run = outputFile.getRun();
        Long runId = run.getId();

        // Get all the outputFileList where run equals to runId
        defaultOutputFileShouldBeFound("runId.equals=" + runId);

        // Get all the outputFileList where run equals to runId + 1
        defaultOutputFileShouldNotBeFound("runId.equals=" + (runId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOutputFileShouldBeFound(String filter) throws Exception {
        restOutputFileMockMvc.perform(get("/api/output-files?sort=relativePathInST,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].relativePathInST").value(hasItem(DEFAULT_RELATIVE_PATH_IN_ST)))
            .andExpect(jsonPath("$.[*].lTInsertionDate").value(hasItem(DEFAULT_L_T_INSERTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].pathInLT").value(hasItem(DEFAULT_PATH_IN_LT)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT)))
            .andExpect(jsonPath("$.[*].subSystemAtOriginOfData").value(hasItem(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA)))
            .andExpect(jsonPath("$.[*].securityLevel").value(hasItem(DEFAULT_SECURITY_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].crc").value(hasItem(DEFAULT_CRC)));


        // Check, that the count call also returns 1
        restOutputFileMockMvc.perform(get("/api/output-files/count?sort=relativePathInST,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOutputFileShouldNotBeFound(String filter) throws Exception {
        restOutputFileMockMvc.perform(get("/api/output-files?sort=relativePathInST,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOutputFileMockMvc.perform(get("/api/output-files/count?sort=relativePathInST,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingOutputFile() throws Exception {
        // Get the outputFile
        restOutputFileMockMvc.perform(get("/api/output-files/{relativePathInST}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOutputFile() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        int databaseSizeBeforeUpdate = outputFileRepository.findAll().size();

        //add admin authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        // Update the outputFile
        OutputFile updatedOutputFile = outputFileRepository.findById(outputFile.getRelativePathInST()).get();
        // Disconnect from session so that the updates on updatedOutputFile are not directly saved in db
        em.detach(updatedOutputFile);
        updatedOutputFile
            .lTInsertionDate(UPDATED_L_T_INSERTION_DATE)
            .pathInLT(UPDATED_PATH_IN_LT)
            .fileType(UPDATED_FILE_TYPE)
            .format(UPDATED_FORMAT)
            .subSystemAtOriginOfData(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA)
            .securityLevel(UPDATED_SECURITY_LEVEL)
            .crc(UPDATED_CRC);
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(updatedOutputFile);

        restOutputFileMockMvc.perform(put("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isOk());

        // Validate the OutputFile in the database
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeUpdate);
        OutputFile testOutputFile = outputFileList.get(outputFileList.size() - 1);
        assertThat(testOutputFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testOutputFile.getlTInsertionDate()).isEqualTo(UPDATED_L_T_INSERTION_DATE);
        assertThat(testOutputFile.getPathInLT()).isEqualTo(UPDATED_PATH_IN_LT);
        assertThat(testOutputFile.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testOutputFile.getFormat()).isEqualTo(UPDATED_FORMAT);
        assertThat(testOutputFile.getSubSystemAtOriginOfData()).isEqualTo(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testOutputFile.getSecurityLevel()).isEqualTo(UPDATED_SECURITY_LEVEL);
        assertThat(testOutputFile.getCrc()).isEqualTo(UPDATED_CRC);
    }

    @Test
    @Transactional
    public void updateNonExistingOutputFile() throws Exception {
        int databaseSizeBeforeUpdate = outputFileRepository.findAll().size();

        // Create the OutputFile
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(outputFile);

        // If the entity isn't stored in database, it will throw BadRequestAlertException
        restOutputFileMockMvc.perform(put("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OutputFile in the database
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteNonExistentOutputFile() throws Exception {
        // Initialize the database and file
        outputFileRepository.saveAndFlush(outputFile);

        int databaseSizeBeforeDelete = outputFileRepository.findAll().size();

        //add admin authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", authorities));
        SecurityContextHolder.setContext(securityContext);

        OutputFileDTO outputFileDTO = new OutputFileDTO();
        outputFileDTO.setRelativePathInST(UPDATED_RELATIVE_PATH_IN_ST);

        // Try to delete the outputFile
        restOutputFileMockMvc.perform(delete("/api/output-files/")
            .accept(TestUtil.APPLICATION_JSON_UTF8)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the database contains same number of items
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OutputFile.class);
        OutputFile outputFile1 = new OutputFile();
        outputFile1.setRelativePathInST(DEFAULT_RELATIVE_PATH_IN_ST);
        OutputFile outputFile2 = new OutputFile();
        outputFile2.setRelativePathInST(outputFile1.getRelativePathInST());
        assertThat(outputFile1).isEqualTo(outputFile2);
        outputFile2.setRelativePathInST(UPDATED_RELATIVE_PATH_IN_ST);
        assertThat(outputFile1).isNotEqualTo(outputFile2);
        outputFile1.setRelativePathInST(null);
        assertThat(outputFile1).isNotEqualTo(outputFile2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OutputFileDTO.class);
        OutputFileDTO outputFileDTO1 = new OutputFileDTO();
        outputFileDTO1.setRelativePathInST(DEFAULT_RELATIVE_PATH_IN_ST);
        OutputFileDTO outputFileDTO2 = new OutputFileDTO();
        assertThat(outputFileDTO1).isNotEqualTo(outputFileDTO2);
        outputFileDTO2.setRelativePathInST(outputFileDTO1.getRelativePathInST());
        assertThat(outputFileDTO1).isEqualTo(outputFileDTO2);
        outputFileDTO2.setRelativePathInST(UPDATED_RELATIVE_PATH_IN_ST);
        assertThat(outputFileDTO1).isNotEqualTo(outputFileDTO2);
        outputFileDTO1.setRelativePathInST(null);
        assertThat(outputFileDTO1).isNotEqualTo(outputFileDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(outputFileMapper.fromId(DEFAULT_RELATIVE_PATH_IN_ST).getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(outputFileMapper.fromId(null)).isNull();
    }

    @Test
    @Transactional
    public void checkRunIdIsRequiredInPutMethod() throws Exception {
        //add user authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        int databaseSizeBeforeTest = outputFileRepository.findAll().size();

        // Update the outputFile
        OutputFile updatedOutputFile = outputFileRepository.findById(outputFile.getRelativePathInST()).get();
        // Disconnect from session so that the updates on updatedOutputFile are not directly saved in db
        em.detach(updatedOutputFile);
        updatedOutputFile
            .lTInsertionDate(UPDATED_L_T_INSERTION_DATE)
            .pathInLT(UPDATED_PATH_IN_LT)
            .fileType(UPDATED_FILE_TYPE)
            .format(UPDATED_FORMAT)
            .subSystemAtOriginOfData(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA)
            .securityLevel(UPDATED_SECURITY_LEVEL)
            .crc(UPDATED_CRC);

        // set the field Run null
        updatedOutputFile.setRun(null);

        // Create the OutputFile, which fails.
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(updatedOutputFile);

        restOutputFileMockMvc.perform(put("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isInternalServerError());

        // Validate OutputFile doesn't updated in the database
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeTest);
        OutputFile testOutputFile = outputFileList.get(outputFileList.size() - 1);
        assertThat(testOutputFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testOutputFile.getlTInsertionDate()).isEqualTo(DEFAULT_L_T_INSERTION_DATE);
        assertThat(testOutputFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testOutputFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testOutputFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testOutputFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testOutputFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testOutputFile.getCrc()).isEqualTo(DEFAULT_CRC);
    }

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void checkRunIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = outputFileRepository.findAll().size();
        // set the field null
        outputFile.setRun(null);

        // Create the OutputFile, which fails.
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(outputFile);

        restOutputFileMockMvc.perform(post("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isBadRequest());

        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeTest);
    }*/

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void checkAutoCompletionOfLtInsertionDate() throws Exception {
        int databaseSizeBeforeCreate = outputFileRepository.findAll().size();
        // set the field null
        outputFile.setlTInsertionDate(null);

        // Create the OutputFile
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(outputFile);
        restOutputFileMockMvc.perform(post("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isCreated());

        // Validate the OutputFile in the database
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeCreate + 1);
        OutputFile testOutputFile = outputFileList.get(outputFileList.size() - 1);
        assertThat(testOutputFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testOutputFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testOutputFile.getlTInsertionDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter));
        assertThat(testOutputFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testOutputFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testOutputFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testOutputFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testOutputFile.getCrc()).isEqualTo(DEFAULT_CRC);
    }*/

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void checkPostScenarioFileWithMetadataAllTheSame() throws Exception {
        outputFileRepository.saveAndFlush(outputFile);
        int databaseSizeBeforeTest = outputFileRepository.findAll().size();

        // Create the ScenarioFile, which fails.
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(outputFile);

        restOutputFileMockMvc.perform(put("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().is5xxServerError());

        // Check that the count call still returns 1
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeTest);
    }*/

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void createScenarioFileWithSamePathInLTAndSameCrc() throws Exception {
        outputFileRepository.saveAndFlush(outputFile);
        int databaseSizeBeforeTest = outputFileRepository.findAll().size();

        // Create the ScenarioFile, which fails.
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(outputFile);
        outputFileDTO.setFormat(UPDATED_FORMAT);
        restOutputFileMockMvc.perform(post("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().is5xxServerError());

        // Check that the count call still returns 1
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeTest);
    }*/

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void checkPutOutputFileWithSamePathInLTButDifferentCrc() throws Exception {
        outputFileRepository.saveAndFlush(outputFile);
        int databaseSizeBeforeTest = outputFileRepository.findAll().size();

        // Create the ScenarioFile, which fails.
        outputFile.setCrc(UPDATED_CRC);
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(outputFile);

        restOutputFileMockMvc.perform(put("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().is5xxServerError());

        // Check that the count call still returns 1
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeTest);
    }*/

    //TODO: test à mettre dans TransferArchiveResourceIT
    /*@Test
    @Transactional
    public void checkAutoCompletionOfOwnerId() throws Exception {
        int databaseSizeBeforeCreate = outputFileRepository.findAll().size();
        // set the field null
        outputFile.setOwner(null);

        // Create the OutputFile
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(outputFile);
        restOutputFileMockMvc.perform(post("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isCreated());

        // Validate the OutputFile in the database
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeCreate + 1);
        OutputFile testOutputFile = outputFileList.get(outputFileList.size() - 1);
        assertThat(testOutputFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testOutputFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testOutputFile.getlTInsertionDate()).isEqualTo(DEFAULT_L_T_INSERTION_DATE);
        assertThat(testOutputFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testOutputFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testOutputFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testOutputFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testOutputFile.getCrc()).isEqualTo(DEFAULT_CRC);
    }*/

    @Test
    @Transactional
    public void checkUserWithoutAdminRightsCantDelete() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);
        int databaseSizeBeforeTest = outputFileRepository.findAll().size();

        //add user authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);
        OutputFileDTO outputFileDTO = new OutputFileDTO();
        outputFileDTO.setRelativePathInST(outputFile.getRelativePathInST());

        // Delete the OutputFile
        restOutputFileMockMvc.perform(delete("/api/output-files/")
            .accept(TestUtil.APPLICATION_JSON_UTF8)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isForbidden());

        // Validate the OutputFile in the database
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeTest);
        OutputFile testOutputFile = outputFileList.get(outputFileList.size() - 1);
        assertThat(testOutputFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testOutputFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testOutputFile.getlTInsertionDate()).isEqualTo(DEFAULT_L_T_INSERTION_DATE);
        assertThat(testOutputFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testOutputFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testOutputFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testOutputFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testOutputFile.getCrc()).isEqualTo(DEFAULT_CRC);
    }

    @Test
    @Transactional
    public void updateOutputFileWithoutAdminRights() throws Exception {
        // Initialize the database
        outputFileRepository.saveAndFlush(outputFile);

        int databaseSizeBeforeUpdate = outputFileRepository.findAll().size();

        //add user authorities to current user
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        // Update the outputFile
        OutputFile updatedOutputFile = outputFileRepository.findById(outputFile.getRelativePathInST()).get();
        // Disconnect from session so that the updates on updatedOutputFile are not directly saved in db
        em.detach(updatedOutputFile);
        updatedOutputFile
            .lTInsertionDate(UPDATED_L_T_INSERTION_DATE)
            .pathInLT(UPDATED_PATH_IN_LT)
            .fileType(UPDATED_FILE_TYPE)
            .format(UPDATED_FORMAT)
            .subSystemAtOriginOfData(UPDATED_SUB_SYSTEM_AT_ORIGIN_OF_DATA)
            .securityLevel(UPDATED_SECURITY_LEVEL)
            .crc(UPDATED_CRC);
        OutputFileDTO outputFileDTO = outputFileMapper.toDto(updatedOutputFile);

        restOutputFileMockMvc.perform(put("/api/output-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outputFileDTO)))
            .andExpect(status().isForbidden());

        // Validate the OutputFile in the database
        List<OutputFile> outputFileList = outputFileRepository.findAll();
        assertThat(outputFileList).hasSize(databaseSizeBeforeUpdate);
        OutputFile testOutputFile = outputFileList.get(outputFileList.size() - 1);
        assertThat(testOutputFile.getRelativePathInST()).isEqualTo(DEFAULT_RELATIVE_PATH_IN_ST);
        assertThat(testOutputFile.getlTInsertionDate()).isEqualTo(DEFAULT_L_T_INSERTION_DATE);
        assertThat(testOutputFile.getPathInLT()).isEqualTo(DEFAULT_PATH_IN_LT);
        assertThat(testOutputFile.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testOutputFile.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testOutputFile.getSubSystemAtOriginOfData()).isEqualTo(DEFAULT_SUB_SYSTEM_AT_ORIGIN_OF_DATA);
        assertThat(testOutputFile.getSecurityLevel()).isEqualTo(DEFAULT_SECURITY_LEVEL);
        assertThat(testOutputFile.getCrc()).isEqualTo(DEFAULT_CRC);
    }
}
