package com.airbus.archivemanager.repository;

import com.airbus.archivemanager.domain.enumeration.FileType;
import com.airbus.archivemanager.domain.enumeration.SecurityLevel;
import com.airbus.archivemanager.domain.enumeration.TransferStatus;
import com.airbus.archivemanager.service.ArchiveManagerGenericException;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;
import com.airbus.archivemanager.service.dto.ScenarioDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data repository for the Transfer entity.
 */
@SuppressWarnings("unused")
@Repository
public class JdbcTransferTmpRepository implements ITransferTmpRepository {
    private final Logger logger = LoggerFactory.getLogger(JdbcTransferTmpRepository.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public int createOutputTable(String tmpTableName) {
        return jdbcTemplate.update(createTmpOutputTable(tmpTableName));
    }

    @Override
    @Transactional
    public int createInputTable(String tmpTableName) {
        return jdbcTemplate.update(createTmpInputTable(tmpTableName));
    }

    @Override
    public int dropTable(String tmpTableName) {
        return jdbcTemplate.update(dropTmpTable(tmpTableName));
    }

    @Override
    public int saveOutputFile(String tmpTableName, OutputFileDTO file) {
        String ltInsertiondate = null;
        if (file.getlTInsertionDate() != null) {
            ltInsertiondate = file.getlTInsertionDate().format(DateTimeFormatter.ISO_DATE_TIME);
        }
        String securityLevel = null;
        if (file.getSecurityLevel() != null) {
            securityLevel = file.getSecurityLevel().toString();
        }
        return jdbcTemplate.update(insertOutputQuery(tmpTableName), ltInsertiondate, file.getFileType(),
                file.getFormat(), file.getOwnerId(), file.getPathInLT(), file.getRelativePathInST(), file.getRunId(),
                file.getSubSystemAtOriginOfData(), securityLevel, TransferStatus.IN_EDITION.name());
    }

    @Override
    public int saveScenarioFile(String tmpTableName, ScenarioFileDTO file) {
        String ltInsertiondate = null;
        if (file.getlTInsertionDate() != null) {
            ltInsertiondate = file.getlTInsertionDate().format(DateTimeFormatter.ISO_DATE_TIME);
        }
        Long scenarioId = null;
        if (file.getScenarios() != null && !file.getScenarios().isEmpty()) {
            Optional<ScenarioDTO> scenarioDto = file.getScenarios().stream().findFirst();
            if (scenarioDto.isPresent()) {
                scenarioId = scenarioDto.get().getId();
            }
        }
        String securityLevel = null;
        if (file.getSecurityLevel() != null) {
            securityLevel = file.getSecurityLevel().toString();
        }
        String inpuType = null;
        if (file.getInputType() != null) {
            inpuType = file.getInputType().toString();
        }
        String timeOfData = null;
        if (file.getTimeOfData() != null) {
            timeOfData = file.getTimeOfData().format(DateTimeFormatter.ISO_DATE_TIME);
        }
        return jdbcTemplate.update(insertInputQuery(tmpTableName), ltInsertiondate, file.getFileType(),
                file.getFormat(), file.getOwnerId(), file.getPathInLT(), file.getRelativePathInST(),
                file.getDatasetId(), file.getConfigDatasetId(), securityLevel, timeOfData,
                file.getSubSystemAtOriginOfData(), TransferStatus.IN_EDITION.name(), inpuType, scenarioId);
    }

    @Override
    @Transactional
    @Retryable(value = { ArchiveManagerGenericException.class }, maxAttempts = 10, backoff = @Backoff(delay = 200))
    public int update(String tmpTableName, String status, String relativePathInST)
            throws ArchiveManagerGenericException {

        int result = jdbcTemplate.update("update " + tmpTableName + " set STATUS = ? where RELATIVE_PATH_IN_ST = ?",
                status, relativePathInST);

        if (result == 0) {
            throw new ArchiveManagerGenericException("retry");
        }

        return result;

    }

    @Override
    @Transactional(readOnly = true)
    public List<OutputFileDTO> findAllOutputFiles(String tmpTableName) {
        return this.jdbcTemplate.query("SELECT * FROM " + tmpTableName, getOutputFileMap());
    }

    private RowMapper<OutputFileDTO> getOutputFileMap() {
        return (rs, rowNum) -> {
            OutputFileDTO outputFile = new OutputFileDTO();
            String ltInsertionDate = rs.getString("L_T_INSERTION_DATE");
            if (!rs.wasNull()) {
                outputFile.setlTInsertionDate(LocalDateTime.parse(ltInsertionDate, DateTimeFormatter.ISO_DATE_TIME));
            }
            outputFile.setFileType(rs.getString("FILE_TYPE"));
            outputFile.setFormat(rs.getString("FORMAT"));
            outputFile.setOwnerId(rs.getLong("OWNER_ID"));
            outputFile.setPathInLT(rs.getString("PATH_IN_LT"));
            outputFile.setRelativePathInST(rs.getString("RELATIVE_PATH_IN_ST"));
            outputFile.setRunId(rs.getLong("RUN_ID"));
            outputFile.setSubSystemAtOriginOfData(rs.getString("SUB_SYSTEM_AT_ORIGIN_OF_DATA"));
            try {
                outputFile.setSecurityLevel(SecurityLevel.valueOf(rs.getString("SECURITY_LEVEL")));
            } catch (IllegalArgumentException | NullPointerException e) {
                outputFile.setSecurityLevel(null);
            }
            return outputFile;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioFileDTO> findAllScenarioFiles(String tmpTableName) {
        return this.jdbcTemplate.query("SELECT * FROM " + tmpTableName, getScenarioFileMap());

    }

    private RowMapper<ScenarioFileDTO> getScenarioFileMap() {
        return (rs, rowNum) -> {
            ScenarioFileDTO scenarioFile = new ScenarioFileDTO();
            String ltInsertionDate = rs.getString("L_T_INSERTION_DATE");
            if (!rs.wasNull()) {
                scenarioFile.setlTInsertionDate(LocalDateTime.parse(ltInsertionDate, DateTimeFormatter.ISO_DATE_TIME));
            }
            scenarioFile.setFileType(rs.getString("FILE_TYPE"));
            scenarioFile.setFormat(rs.getString("FORMAT"));
            scenarioFile.setOwnerId(rs.getLong("OWNER_ID"));
            scenarioFile.setPathInLT(rs.getString("PATH_IN_LT"));
            scenarioFile.setRelativePathInST(rs.getString("RELATIVE_PATH_IN_ST"));

            Long datasetId = rs.getLong("DATASET_ID");
            if (rs.wasNull()) {
                datasetId = null;
            }
            scenarioFile.setDatasetId(datasetId);
            Long configDataSetId = rs.getLong("CONFIG_DATASET_ID");
            if (rs.wasNull()) {
                configDataSetId = null;
            }
            scenarioFile.setConfigDatasetId(configDataSetId);
            try {
                scenarioFile.setSecurityLevel(SecurityLevel.valueOf(rs.getString("SECURITY_LEVEL")));
            } catch (IllegalArgumentException | NullPointerException e) {
                scenarioFile.setSecurityLevel(null);
            }
            String timeOfData = rs.getString("TIME_OF_DATA");
            if (!rs.wasNull()) {
                scenarioFile.setTimeOfData(LocalDateTime.parse(timeOfData, DateTimeFormatter.ISO_DATE_TIME));
            }

            scenarioFile.setSubSystemAtOriginOfData(rs.getString("SUB_SYSTEM_AT_ORIGIN_OF_DATA"));
            try {
                scenarioFile.setInputType(FileType.valueOf(rs.getString("INPUT_TYPE")));
            } catch (IllegalArgumentException | NullPointerException e) {
                scenarioFile.setInputType(null);
            }
            Long scenarioId = rs.getLong("SCENARIO_ID");
            if (!rs.wasNull()) {
                ScenarioDTO scenario = new ScenarioDTO();
                scenario.setId(scenarioId);
                Set<ScenarioDTO> scenarios = new HashSet<>();
                scenarios.add(scenario);
                scenarioFile.setScenarios(scenarios);
            }

            return scenarioFile;
        };
    }

    private String createTmpOutputTable(String tmpTableName) {
        return String.format(
                "CREATE TABLE %s (L_T_INSERTION_DATE VARCHAR(255),FILE_TYPE VARCHAR(255), FORMAT VARCHAR(255),OWNER_ID BIGINT,PATH_IN_LT VARCHAR(10000),RELATIVE_PATH_IN_ST VARCHAR(10000),RUN_ID BIGINT,SUB_SYSTEM_AT_ORIGIN_OF_DATA VARCHAR(255), SECURITY_LEVEL VARCHAR(255),STATUS VARCHAR(20))",
                tmpTableName);
    }

    private String createTmpInputTable(String tmpTableName) {
        return String.format(
                "CREATE TABLE %s (L_T_INSERTION_DATE VARCHAR(255),FILE_TYPE VARCHAR(255),FORMAT VARCHAR(255),OWNER_ID BIGINT,PATH_IN_LT VARCHAR(10000),RELATIVE_PATH_IN_ST VARCHAR(10000),DATASET_ID BIGINT,CONFIG_DATASET_ID BIGINT,SECURITY_LEVEL VARCHAR(255),TIME_OF_DATA VARCHAR(255),SUB_SYSTEM_AT_ORIGIN_OF_DATA VARCHAR(255),STATUS VARCHAR(20),INPUT_TYPE VARCHAR(255),SCENARIO_ID BIGINT)",
                tmpTableName);
    }

    private String dropTmpTable(String tmpTableName) {
        return String.format("DROP TABLE IF EXISTS %s", tmpTableName);
    }

    private String insertOutputQuery(String tmpTableName) {
        return String.format(
                "INSERT into %s (L_T_INSERTION_DATE, FILE_TYPE,FORMAT,OWNER_ID,PATH_IN_LT,RELATIVE_PATH_IN_ST,RUN_ID,SUB_SYSTEM_AT_ORIGIN_OF_DATA,SECURITY_LEVEL, STATUS) VALUES(?,?,?,?,?,?,?,?,?,?)",
                tmpTableName);
    }

    private String insertInputQuery(String tmpTableName) {
        return String.format(
                "INSERT into %s (L_T_INSERTION_DATE,FILE_TYPE,FORMAT,OWNER_ID,PATH_IN_LT,RELATIVE_PATH_IN_ST,DATASET_ID,CONFIG_DATASET_ID,SECURITY_LEVEL,TIME_OF_DATA,SUB_SYSTEM_AT_ORIGIN_OF_DATA, STATUS, INPUT_TYPE,SCENARIO_ID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                tmpTableName);
    }

}
