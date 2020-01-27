package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.service.ArchiveManagerGenericException;
import com.airbus.archivemanager.service.OutputFileQueryService;
import com.airbus.archivemanager.service.ScenarioFileQueryService;
import com.airbus.archivemanager.service.dto.OutputFileCriteria;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.dto.ScenarioFileCriteria;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;
import com.airbus.archivemanager.service.util.FilesUtil;
import io.github.jhipster.service.filter.StringFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Locale;

@Service
public class CheckCrc {

    private final Logger log = LoggerFactory.getLogger(CheckCrc.class);

    private final ScenarioFileQueryService scenarioFileQueryService;

    private final OutputFileQueryService outputFileQueryService;

    private final MessageSource messageSource;

    public CheckCrc(ScenarioFileQueryService scenarioFileQueryService, OutputFileQueryService outputFileQueryService, MessageSource messageSource) {
        this.scenarioFileQueryService = scenarioFileQueryService;
        this.outputFileQueryService = outputFileQueryService;
        this.messageSource = messageSource;
    }

    /**
     * Check if a similar scenarioFileDTO already exists in database.
     *
     * @param scenarioFileDTO the entity to check.
     */
    protected void checkIfScenarioFileAlreadyExists(ScenarioFileDTO scenarioFileDTO) {
        StringFilter relativePathInST = new StringFilter();
        relativePathInST.setEquals(scenarioFileDTO.getRelativePathInST());
        ScenarioFileCriteria scenarioFileCriteria = new ScenarioFileCriteria();
        scenarioFileCriteria.setRelativePathInST(relativePathInST);
        List<ScenarioFileDTO> scenarioFileDTOList = scenarioFileQueryService.findByCriteria(scenarioFileCriteria);
        if (!(scenarioFileDTOList.isEmpty()) && scenarioFileDTOList.get(0).compareMetadata(scenarioFileDTO)) {
            String message = messageSource.getMessage("warning.fileExistsWithSameMetadata", null, Locale.ENGLISH);
            log.warn(message);
            throw new ArchiveManagerGenericException(message);
        }
        if (!(scenarioFileDTOList.isEmpty()) && scenarioFileDTOList.get(0).getCrc().equals(scenarioFileDTO.getCrc())) {
            String message = messageSource.getMessage("warning.fileExistsWithSamePathAndCrc", null, Locale.ENGLISH);
            log.warn(message);
            throw new ArchiveManagerGenericException(message);
        }
        if (!(scenarioFileDTOList.isEmpty()) && !(scenarioFileDTOList.get(0).getCrc().equals(scenarioFileDTO.getCrc()))) {
            String message = messageSource.getMessage("warning.fileExistsWithSamePathButCrcDifferent", null, Locale.ENGLISH);
            log.warn(message);
            throw new ArchiveManagerGenericException(message);
        }
    }

    /**
     * Check if a similar outputFileDTO already exists in database.
     *
     * @param outputFileDTO the entity to check.
     */
    protected void checkIfOutputFileAlreadyExists(OutputFileDTO outputFileDTO) {
        StringFilter relativePathInST = new StringFilter();
        relativePathInST.setEquals(outputFileDTO.getRelativePathInST());
        OutputFileCriteria outputFileCriteria = new OutputFileCriteria();
        outputFileCriteria.setRelativePathInST(relativePathInST);
        List<OutputFileDTO> outputFileDTOList = outputFileQueryService.findByCriteria(outputFileCriteria);
        if (!(outputFileDTOList.isEmpty()) && outputFileDTOList.get(0).compareMetadata((outputFileDTO))) {
            String message = messageSource.getMessage("warning.fileExistsWithSameMetadata", null, Locale.ENGLISH);
            log.warn(message);
            throw new ArchiveManagerGenericException(message);
        }
        if (!(outputFileDTOList.isEmpty()) && outputFileDTOList.get(0).getCrc().equals(outputFileDTO.getCrc())) {
            String message = messageSource.getMessage("warning.fileExistsWithSamePathAndCrc", null, Locale.ENGLISH);
            log.warn(message);
            throw new ArchiveManagerGenericException(message);
        }
        if (!(outputFileDTOList.isEmpty()) && !(outputFileDTOList.get(0).getCrc().equals(outputFileDTO.getCrc()))) {
            String message = messageSource.getMessage("warning.fileExistsWithSamePathButCrcDifferent", null, Locale.ENGLISH);
            log.warn(message);
            throw new ArchiveManagerGenericException(message);
        }
    }

    protected void checkIfFileAlreadyExistsInSTSBeforeRetrieve(String relativePathInST) {

        File temp = new File(FilesUtil.createCompletePathInST(relativePathInST,messageSource,log));
        if (temp.exists()) {
            String[] variableArray = new String[]{relativePathInST};
            String message = messageSource.getMessage("warning.pathAlreadyExists", variableArray, Locale.ENGLISH);
            log.warn(message);
        }
    }
}
