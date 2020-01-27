package com.airbus.archivemanager.web.rest;

// import com.airbus.archivemanager.ArchivemanagerApp;
// import com.airbus.archivemanager.domain.Transfer;
// import com.airbus.archivemanager.repository.TransferRepository;
// import com.airbus.archivemanager.service.TransferService;
// import com.airbus.archivemanager.service.dto.TransferDTO;
// import com.airbus.archivemanager.service.mapper.TransferMapper;
// import com.airbus.archivemanager.web.rest.errors.ExceptionTranslator;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
// import org.springframework.http.MediaType;
// import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.validation.Validator;

// import javax.persistence.EntityManager;
// import java.util.List;

// import static com.airbus.archivemanager.web.rest.TestUtil.createFormattingConversionService;
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.hamcrest.Matchers.hasItem;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import com.airbus.archivemanager.domain.enumeration.Direction;
// import com.airbus.archivemanager.domain.enumeration.TransferStatus;
// /**
//  * Integration tests for the {@link TransferResource} REST controller.
//  */
// @SpringBootTest(classes = ArchivemanagerApp.class)
// public class TransferResourceIT {

//     private static final String DEFAULT_NAME = "AAAAAAAAAA";
//     private static final String UPDATED_NAME = "BBBBBBBBBB";

//     private static final Direction DEFAULT_DIRECTION = Direction.LTS_TO_STS;
//     private static final Direction UPDATED_DIRECTION = Direction.STS_TO_LTS;

//     private static final TransferStatus DEFAULT_STATUS = TransferStatus.IN_EDITION;
//     private static final TransferStatus UPDATED_STATUS = TransferStatus.IN_PROGRESS;

//     @Autowired
//     private TransferRepository transferRepository;

//     @Autowired
//     private TransferMapper transferMapper;

//     @Autowired
//     private TransferService transferService;

//     @Autowired
//     private MappingJackson2HttpMessageConverter jacksonMessageConverter;

//     @Autowired
//     private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

//     @Autowired
//     private ExceptionTranslator exceptionTranslator;

//     @Autowired
//     private EntityManager em;

//     @Autowired
//     private Validator validator;

//     private MockMvc restTransferMockMvc;

//     private Transfer transfer;

//     @BeforeEach
//     public void setup() {
//         MockitoAnnotations.initMocks(this);
//         final TransferResource transferResource = new TransferResource(transferService);
//         this.restTransferMockMvc = MockMvcBuilders.standaloneSetup(transferResource)
//             .setCustomArgumentResolvers(pageableArgumentResolver)
//             .setControllerAdvice(exceptionTranslator)
//             .setConversionService(createFormattingConversionService())
//             .setMessageConverters(jacksonMessageConverter)
//             .setValidator(validator).build();
//     }

//     /**
//      * Create an entity for this test.
//      *
//      * This is a static method, as tests for other entities might also need it,
//      * if they test an entity which requires the current entity.
//      */
//     public static Transfer createEntity(EntityManager em) {
//         Transfer transfer = new Transfer()
//             .name(DEFAULT_NAME)
//             .direction(DEFAULT_DIRECTION)
//             .status(DEFAULT_STATUS);
//         return transfer;
//     }
//     /**
//      * Create an updated entity for this test.
//      *
//      * This is a static method, as tests for other entities might also need it,
//      * if they test an entity which requires the current entity.
//      */
//     public static Transfer createUpdatedEntity(EntityManager em) {
//         Transfer transfer = new Transfer()
//             .name(UPDATED_NAME)
//             .direction(UPDATED_DIRECTION)
//             .status(UPDATED_STATUS);
//         return transfer;
//     }

//     @BeforeEach
//     public void initTest() {
//         transfer = createEntity(em);
//     }

//     @Test
//     @Transactional
//     public void createTransfer() throws Exception {
//         int databaseSizeBeforeCreate = transferRepository.findAll().size();

//         // Create the Transfer
//         TransferDTO transferDTO = transferMapper.toDto(transfer);
//         restTransferMockMvc.perform(post("/api/transfers")
//             .contentType(TestUtil.APPLICATION_JSON_UTF8)
//             .content(TestUtil.convertObjectToJsonBytes(transferDTO)))
//             .andExpect(status().isCreated());

//         // Validate the Transfer in the database
//         List<Transfer> transferList = transferRepository.findAll();
//         assertThat(transferList).hasSize(databaseSizeBeforeCreate + 1);
//         Transfer testTransfer = transferList.get(transferList.size() - 1);
//         assertThat(testTransfer.getName()).isEqualTo(DEFAULT_NAME);
//         assertThat(testTransfer.getDirection()).isEqualTo(DEFAULT_DIRECTION);
//         assertThat(testTransfer.getStatus()).isEqualTo(DEFAULT_STATUS);
//     }

//     @Test
//     @Transactional
//     public void createTransferWithExistingId() throws Exception {
//         int databaseSizeBeforeCreate = transferRepository.findAll().size();

//         // Create the Transfer with an existing ID
//         transfer.setId(1L);
//         TransferDTO transferDTO = transferMapper.toDto(transfer);

//         // An entity with an existing ID cannot be created, so this API call must fail
//         restTransferMockMvc.perform(post("/api/transfers")
//             .contentType(TestUtil.APPLICATION_JSON_UTF8)
//             .content(TestUtil.convertObjectToJsonBytes(transferDTO)))
//             .andExpect(status().isBadRequest());

//         // Validate the Transfer in the database
//         List<Transfer> transferList = transferRepository.findAll();
//         assertThat(transferList).hasSize(databaseSizeBeforeCreate);
//     }


//     @Test
//     @Transactional
//     public void checkNameIsRequired() throws Exception {
//         int databaseSizeBeforeTest = transferRepository.findAll().size();
//         // set the field null
//         transfer.setName(null);

//         // Create the Transfer, which fails.
//         TransferDTO transferDTO = transferMapper.toDto(transfer);

//         restTransferMockMvc.perform(post("/api/transfers")
//             .contentType(TestUtil.APPLICATION_JSON_UTF8)
//             .content(TestUtil.convertObjectToJsonBytes(transferDTO)))
//             .andExpect(status().isBadRequest());

//         List<Transfer> transferList = transferRepository.findAll();
//         assertThat(transferList).hasSize(databaseSizeBeforeTest);
//     }

//     @Test
//     @Transactional
//     public void checkDirectionIsRequired() throws Exception {
//         int databaseSizeBeforeTest = transferRepository.findAll().size();
//         // set the field null
//         transfer.setDirection(null);

//         // Create the Transfer, which fails.
//         TransferDTO transferDTO = transferMapper.toDto(transfer);

//         restTransferMockMvc.perform(post("/api/transfers")
//             .contentType(TestUtil.APPLICATION_JSON_UTF8)
//             .content(TestUtil.convertObjectToJsonBytes(transferDTO)))
//             .andExpect(status().isBadRequest());

//         List<Transfer> transferList = transferRepository.findAll();
//         assertThat(transferList).hasSize(databaseSizeBeforeTest);
//     }

//     @Test
//     @Transactional
//     public void getAllTransfers() throws Exception {
//         // Initialize the database
//         transferRepository.saveAndFlush(transfer);

//         // Get all the transferList
//         restTransferMockMvc.perform(get("/api/transfers?sort=id,desc"))
//             .andExpect(status().isOk())
//             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//             .andExpect(jsonPath("$.[*].id").value(hasItem(transfer.getId().intValue())))
//             .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
//             .andExpect(jsonPath("$.[*].direction").value(hasItem(DEFAULT_DIRECTION.toString())))
//             .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
//     }
    
//     @Test
//     @Transactional
//     public void getTransfer() throws Exception {
//         // Initialize the database
//         transferRepository.saveAndFlush(transfer);

//         // Get the transfer
//         restTransferMockMvc.perform(get("/api/transfers/{id}", transfer.getId()))
//             .andExpect(status().isOk())
//             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//             .andExpect(jsonPath("$.id").value(transfer.getId().intValue()))
//             .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
//             .andExpect(jsonPath("$.direction").value(DEFAULT_DIRECTION.toString()))
//             .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
//     }

//     @Test
//     @Transactional
//     public void getNonExistingTransfer() throws Exception {
//         // Get the transfer
//         restTransferMockMvc.perform(get("/api/transfers/{id}", Long.MAX_VALUE))
//             .andExpect(status().isNotFound());
//     }

//     @Test
//     @Transactional
//     public void updateTransfer() throws Exception {
//         // Initialize the database
//         transferRepository.saveAndFlush(transfer);

//         int databaseSizeBeforeUpdate = transferRepository.findAll().size();

//         // Update the transfer
//         Transfer updatedTransfer = transferRepository.findById(transfer.getId()).get();
//         // Disconnect from session so that the updates on updatedTransfer are not directly saved in db
//         em.detach(updatedTransfer);
//         updatedTransfer
//             .name(UPDATED_NAME)
//             .direction(UPDATED_DIRECTION)
//             .status(UPDATED_STATUS);
//         TransferDTO transferDTO = transferMapper.toDto(updatedTransfer);

//         restTransferMockMvc.perform(put("/api/transfers")
//             .contentType(TestUtil.APPLICATION_JSON_UTF8)
//             .content(TestUtil.convertObjectToJsonBytes(transferDTO)))
//             .andExpect(status().isOk());

//         // Validate the Transfer in the database
//         List<Transfer> transferList = transferRepository.findAll();
//         assertThat(transferList).hasSize(databaseSizeBeforeUpdate);
//         Transfer testTransfer = transferList.get(transferList.size() - 1);
//         assertThat(testTransfer.getName()).isEqualTo(UPDATED_NAME);
//         assertThat(testTransfer.getDirection()).isEqualTo(UPDATED_DIRECTION);
//         assertThat(testTransfer.getStatus()).isEqualTo(UPDATED_STATUS);
//     }

//     @Test
//     @Transactional
//     public void updateNonExistingTransfer() throws Exception {
//         int databaseSizeBeforeUpdate = transferRepository.findAll().size();

//         // Create the Transfer
//         TransferDTO transferDTO = transferMapper.toDto(transfer);

//         // If the entity doesn't have an ID, it will throw BadRequestAlertException
//         restTransferMockMvc.perform(put("/api/transfers")
//             .contentType(TestUtil.APPLICATION_JSON_UTF8)
//             .content(TestUtil.convertObjectToJsonBytes(transferDTO)))
//             .andExpect(status().isBadRequest());

//         // Validate the Transfer in the database
//         List<Transfer> transferList = transferRepository.findAll();
//         assertThat(transferList).hasSize(databaseSizeBeforeUpdate);
//     }

//     @Test
//     @Transactional
//     public void deleteTransfer() throws Exception {
//         // Initialize the database
//         transferRepository.saveAndFlush(transfer);

//         int databaseSizeBeforeDelete = transferRepository.findAll().size();

//         // Delete the transfer
//         restTransferMockMvc.perform(delete("/api/transfers/{id}", transfer.getId())
//             .accept(TestUtil.APPLICATION_JSON_UTF8))
//             .andExpect(status().isNoContent());

//         // Validate the database contains one less item
//         List<Transfer> transferList = transferRepository.findAll();
//         assertThat(transferList).hasSize(databaseSizeBeforeDelete - 1);
//     }

//     @Test
//     @Transactional
//     public void equalsVerifier() throws Exception {
//         TestUtil.equalsVerifier(Transfer.class);
//         Transfer transfer1 = new Transfer();
//         transfer1.setId(1L);
//         Transfer transfer2 = new Transfer();
//         transfer2.setId(transfer1.getId());
//         assertThat(transfer1).isEqualTo(transfer2);
//         transfer2.setId(2L);
//         assertThat(transfer1).isNotEqualTo(transfer2);
//         transfer1.setId(null);
//         assertThat(transfer1).isNotEqualTo(transfer2);
//     }

//     @Test
//     @Transactional
//     public void dtoEqualsVerifier() throws Exception {
//         TestUtil.equalsVerifier(TransferDTO.class);
//         TransferDTO transferDTO1 = new TransferDTO();
//         transferDTO1.setId(1L);
//         TransferDTO transferDTO2 = new TransferDTO();
//         assertThat(transferDTO1).isNotEqualTo(transferDTO2);
//         transferDTO2.setId(transferDTO1.getId());
//         assertThat(transferDTO1).isEqualTo(transferDTO2);
//         transferDTO2.setId(2L);
//         assertThat(transferDTO1).isNotEqualTo(transferDTO2);
//         transferDTO1.setId(null);
//         assertThat(transferDTO1).isNotEqualTo(transferDTO2);
//     }

//     @Test
//     @Transactional
//     public void testEntityFromId() {
//         assertThat(transferMapper.fromId(42L).getId()).isEqualTo(42);
//         assertThat(transferMapper.fromId(null)).isNull();
//     }
// }
