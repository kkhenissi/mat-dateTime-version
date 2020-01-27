package com.airbus.archivemanager.web.rest;

import com.airbus.archivemanager.service.DownloadService;
import com.airbus.archivemanager.service.dto.DownloadDTO;
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

/**
 * REST controller for managing {@link DownloadDTO}.
 */
@RestController
@RequestMapping("/api")
public class DownloadResource {

    private static final String ENTITY_NAME = "downloadResource";
    private final Logger log = LoggerFactory.getLogger(DownloadResource.class);
    private final MessageSource messageSource;
    private final DownloadService downloadService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public DownloadResource(MessageSource messageSource, DownloadService downloadService) {
        this.messageSource = messageSource;
        this.downloadService = downloadService;
    }

    @ApiOperation(value = "copy a file from LTS to local")
    @PostMapping("/download")
    public ResponseEntity<DownloadDTO> download(@RequestBody DownloadDTO downloadDTO) {
        log.info("REST request to get a file from LTS to local with a LTS path: {}", downloadDTO.getlTSPath());
        String lTSPath = FilesUtil.createCompletePathInLT(downloadDTO.getlTSPath(), messageSource, log);
        Path pathLTS = Paths.get(lTSPath);
        FilesUtil.checkLTSPath(downloadDTO.getlTSPath(), pathLTS, messageSource, log);
        Path localPath = Paths.get(downloadDTO.getLocalPath());
        FilesUtil.checkIfFileAlreadyExistsInLocal(localPath, messageSource, log);
        downloadDTO.setlTSPath(lTSPath);
        downloadDTO.setLocalPath(localPath.toString());
        return ResponseEntity.ok().body(downloadService.download(downloadDTO));
    }
}
