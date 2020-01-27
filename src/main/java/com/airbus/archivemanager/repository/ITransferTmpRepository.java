package com.airbus.archivemanager.repository;

import com.airbus.archivemanager.service.ArchiveManagerGenericException;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;

import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.List;

/**
 * Spring Data  repository for the Transfer entity.
 */
@SuppressWarnings("unused")
public interface ITransferTmpRepository {

    int createOutputTable(String tmpTableName);

    int createInputTable(String tmpTableName);

    int dropTable(String tmpTableName);

    int saveOutputFile(String tmpTableName, OutputFileDTO outputFile);

    int update(String tmpTableName, String status, String relativePathInST) throws ArchiveManagerGenericException;

    int saveScenarioFile(String tmpTableName, ScenarioFileDTO scenarioFile);

    List<OutputFileDTO> findAllOutputFiles(String tmpTableName);

    List<ScenarioFileDTO> findAllScenarioFiles(String tmpTableName);
}
