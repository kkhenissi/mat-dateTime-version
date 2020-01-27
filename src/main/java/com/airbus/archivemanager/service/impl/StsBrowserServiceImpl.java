package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.config.ApplicationProperties;
import com.airbus.archivemanager.service.ArchiveManagerGenericException;
import com.airbus.archivemanager.service.StsBrowserService;
import com.airbus.archivemanager.service.dto.StsBrowserDTO;
import com.airbus.archivemanager.service.util.FilesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class StsBrowserServiceImpl implements StsBrowserService {

    private final Logger log = LoggerFactory.getLogger(StsBrowserServiceImpl.class);

    private final MessageSource messageSource;

    public StsBrowserServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public StsBrowserDTO findAll(StsBrowserDTO stsBrowserDTO) {
        Set<String> filesList = stsBrowserDTO.getFilesList();
        Set<String> foldersList = stsBrowserDTO.getFoldersList();
        String normalizePath = FilesUtil.normalizePath(stsBrowserDTO.getRelativePath(), messageSource, log);
        String completePath = FilesUtil.createCompletePathInST(stsBrowserDTO.getRelativePath(), messageSource, log);
        Path dir = Paths.get(completePath);
        if (dir.toFile().isFile()) {
            filesList.add(normalizePath);
            stsBrowserDTO.setFilesList(filesList);
            return stsBrowserDTO;
        }
        String pathParentNormalized = dir.getParent().toString();
        String rootPathInStsNormalized = FilesUtil.normalizePath(ApplicationProperties.getRootPathInST(), messageSource, log);

        if (pathParentNormalized.equalsIgnoreCase(rootPathInStsNormalized) || !pathParentNormalized.contains(rootPathInStsNormalized)) {
            stsBrowserDTO.setRelativePathParent(null);
        } else {
            stsBrowserDTO.setRelativePathParent(dir.getParent().toString().substring(ApplicationProperties.getRootPathInST().length()));
        }
        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(dir);
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    String pathFolder = path.toString().substring(ApplicationProperties.getRootPathInST().length());
                    foldersList.add(pathFolder);
                } else if (path.toFile().exists()) {
                    filesList.add(path.toString().substring(ApplicationProperties.getRootPathInST().length()));
                } else {
                    String message = messageSource.getMessage("error.generic.genericException", null, Locale.ENGLISH);
                    log.error(message);
                    throw new ArchiveManagerGenericException(message);
                }
            }
            stsBrowserDTO.setFilesList(filesList);
            stsBrowserDTO.setFoldersList(foldersList);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return stsBrowserDTO;
    }
}
