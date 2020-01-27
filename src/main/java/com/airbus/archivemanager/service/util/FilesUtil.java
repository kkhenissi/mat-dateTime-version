package com.airbus.archivemanager.service.util;

import com.airbus.archivemanager.config.ApplicationProperties;
import com.airbus.archivemanager.service.ArchiveManagerGenericException;
import com.airbus.archivemanager.service.OutputFileService;
import com.airbus.archivemanager.service.ScenarioFileService;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;
import org.slf4j.Logger;
import org.springframework.context.MessageSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Locale;

/**
 * Utility class for file operations.
 */
public final class FilesUtil {

    private FilesUtil() {
    }

    /**
     * @param pathfileName path file name
     * @throws IOException Input Output exception
     */
    public static void createDirectory(String pathfileName, FileAttribute<?>... attrs) throws IOException {
        if (Files.notExists(Paths.get(pathfileName).getParent())) {
            try {
                Files.createDirectories(Paths.get(pathfileName).getParent(), attrs);
            } catch (UnsupportedOperationException e) {
                Files.createDirectories(Paths.get(pathfileName).getParent());
            }
        }
    }

    /**
     * @param relativePathInST path file name from DTO object
     * @param messageSource    to generate error message
     * @param log              for recording in log
     * @return full path in STS
     */
    public static String createCompletePathInST(String relativePathInST, MessageSource messageSource, Logger log) {
        String path = relativePathInST == null ? ApplicationProperties.getRootPathInST() : (ApplicationProperties.getRootPathInST() + relativePathInST);
        return normalizePath(path, messageSource, log);
    }

    /**
     * @param pathInLT      path file name from DTO object
     * @param messageSource to generate error message
     * @param log           for recording in log
     * @return full path in LTS
     */
    public static String createCompletePathInLT(String pathInLT, MessageSource messageSource, Logger log) {
        return normalizePath(ApplicationProperties.getRootPathInLT() + pathInLT, messageSource, log);
    }

    /**
     * @param fileToTransfer      object to delete in LTS
     * @param scenarioFileservice scenarioFile service
     * @param outputFileservice   outputFile service
     * @param messageSource       to generate error message
     * @param log                 for recording in log
     */
    public static void deleteFileInLTS(Object fileToTransfer, ScenarioFileService scenarioFileservice, OutputFileService outputFileservice, MessageSource messageSource, Logger log) {
        if (fileToTransfer instanceof OutputFileDTO) {
            OutputFileDTO outputFileDTO = (OutputFileDTO) fileToTransfer;
            if (outputFileservice.findOne(outputFileDTO.getRelativePathInST()).isPresent()) {
                String message = messageSource.getMessage("error.outputFile.outputFileAlreadyExists", null, Locale.ENGLISH);
                log.error(message);
                throw new ArchiveManagerGenericException(message);
            }
            deleteFileInLTS(outputFileDTO.getPathInLT(), messageSource, log);

        } else {
            ScenarioFileDTO scenarioFileDTO = (ScenarioFileDTO) fileToTransfer;
            if (scenarioFileservice.findOne(scenarioFileDTO.getRelativePathInST()).isPresent()) {
                String message = messageSource.getMessage("error.scenarioFile.scenarioFileAlreadyExists", null, Locale.ENGLISH);
                log.error(message);
                throw new ArchiveManagerGenericException(message);
            }
            deleteFileInLTS(scenarioFileDTO.getPathInLT(), messageSource, log);
        }


    }

    public static void checkIfFileIsPresentInSTSBeforeArchive(String relativePathInST, MessageSource messageSource, Logger log) {
        Path path = Paths.get(createCompletePathInST(relativePathInST, messageSource, log));
        if (Files.notExists(path)) {
            String[] variableArray = new String[]{String.valueOf(path)};
            String message = messageSource.getMessage("error.noSuchFile", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
    }

    public static String normalizePath(String relativePathInST, MessageSource messageSource, Logger log) {
        if (relativePathInST == null) {
            return null;
        }
        if (relativePathInST.contains("..")) {
            String message = messageSource.getMessage("error.2DotsInRelativePathInST", null, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
        Path pathNormalized = Paths.get(relativePathInST).normalize();
        return pathNormalized.toString();
    }

    public static void checkIfFileIsPresentInLTSBeforeArchive(String relativePathInST, MessageSource messageSource, Logger log) {
        Path path = Paths.get(createCompletePathInLT(relativePathInST, messageSource, log));
        if (!Files.notExists(path)) {
            String[] variableArray = new String[]{String.valueOf(path)};
            String message = messageSource.getMessage("error.fileAlreadyExistsInLTS", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
    }

    /**
     * @param pathInLT      relative path in LTS
     * @param messageSource to generate error message
     * @param log           for recording in log
     */
    public static void deleteFileInLTS(String pathInLT, MessageSource messageSource, Logger log) {
        String pathInLTS = createCompletePathInLT(pathInLT, messageSource, log);
        Path path = Paths.get(pathInLTS);
        try {
            Files.delete(path);
        } catch (NoSuchFileException x) {
            String[] variableArray = new String[]{pathInLT};
            String message = messageSource.getMessage("error.noSuchFile", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        } catch (DirectoryNotEmptyException x) {
            String[] variableArray = new String[]{pathInLT};
            String message = messageSource.getMessage("error.folderNotEmpty", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        } catch (IOException x) {
            // File permission problems are caught here.
            log.error(String.valueOf(x));
        }
    }

    public static void checkIfFileAlreadyExistsInLTS(File file, MessageSource messageSource, Logger log) {
        String relativePath = file.getPath();
        if (relativePath.contains(FilesUtil.normalizePath(ApplicationProperties.getRootPathInLT(), messageSource, log))) {
            relativePath = file.getPath().substring(ApplicationProperties.getRootPathInLT().length());
        }
        if (file.exists()) {
            String[] variableArray = new String[]{relativePath};
            String message = messageSource.getMessage("error.fileAlreadyExists", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
    }

    public static void checkIfFileAlreadyExistsInLocal(Path localPath, MessageSource messageSource, Logger log) {
        File file = localPath.toFile();
        if (file.exists()) {
            String[] variableArray = new String[]{localPath.toString()};
            String message = messageSource.getMessage("error.fileAlreadyExists", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
    }

    public static void checkLTSPath(String pathInLT, Path pathLTS, MessageSource messageSource, Logger log) {
        if (!pathLTS.toFile().exists()) {
            String[] variableArray = new String[]{pathInLT};
            String message = messageSource.getMessage("error.uploadDownload.LTSPathUnknown", variableArray, Locale.ENGLISH);
            log.error(message);
            throw new ArchiveManagerGenericException(message);
        }
    }
}
