package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.service.ArchiveManagerGenericException;
import com.airbus.archivemanager.service.StsBrowserService;
import com.airbus.archivemanager.service.dto.StsBrowserDTO;
import com.airbus.archivemanager.service.util.FilesUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * REST controller for managing {@link com.airbus.archivemanager.service.dto.StsBrowserDTO}.
 */
@RestController
@RequestMapping("/api")
public class StsBrowserResource {

    private static final String ENTITY_NAME = "stsBrowser";
    private final Logger log = LoggerFactory.getLogger(StsBrowserResource.class);
    private final MessageSource messageSource;
    private final StsBrowserService stsBrowserService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public StsBrowserResource(MessageSource messageSource, StsBrowserService stsBrowserService) {
        this.messageSource = messageSource;
        this.stsBrowserService = stsBrowserService;
    }

    @ApiOperation(value = "Return all files and folders from a relative path in STS")
    @PostMapping("/browser")
    public ResponseEntity<StsBrowserDTO> getAllFilesAndFolders(@RequestBody StsBrowserDTO stsBrowserDTO) {
        log.info("REST request to get all files and folders from a stsBrowserDTO: {}", stsBrowserDTO.getRelativePath());
        String completeStartPath = FilesUtil.createCompletePathInST(stsBrowserDTO.getStartPath(), messageSource, log);
        Path startPath = Paths.get(completeStartPath);
        if (!startPath.toFile().exists()) {
            String[] variableArray = new String[]{String.valueOf(stsBrowserDTO.getStartPath())};
            String message = messageSource.getMessage("error.stsBrowser.startPathUnknown", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
        String completePath = FilesUtil.createCompletePathInST(stsBrowserDTO.getRelativePath(), messageSource, log);
        Path path = Paths.get(completePath);
        if (!path.toFile().exists()) {
            String[] variableArray = new String[]{String.valueOf(stsBrowserDTO.getRelativePath())};
            String message = messageSource.getMessage("error.stsBrowser.pathUnknown", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
        return ResponseEntity.ok().body(stsBrowserService.findAll(stsBrowserDTO));
    }
}
