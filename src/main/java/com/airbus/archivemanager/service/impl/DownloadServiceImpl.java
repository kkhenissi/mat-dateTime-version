package com.airbus.archivemanager.service.impl;

import com.airbus.archivemanager.config.ApplicationProperties;
import com.airbus.archivemanager.service.ArchiveManagerGenericException;
import com.airbus.archivemanager.service.DownloadService;
import com.airbus.archivemanager.service.dto.DownloadDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;

@Service
@Transactional
public class DownloadServiceImpl implements DownloadService {

    private final Logger log = LoggerFactory.getLogger(DownloadServiceImpl.class);

    public DownloadServiceImpl() {
        //empty constructor
    }

    @Override
    public DownloadDTO download(DownloadDTO downloadDTO) {
        try (InputStream is = new FileInputStream(downloadDTO.getlTSPath());
             OutputStream os = new FileOutputStream(downloadDTO.getLocalPath())) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new ArchiveManagerGenericException(e.getMessage());
        }
        downloadDTO.setlTSPath(downloadDTO.getlTSPath().substring(ApplicationProperties.getRootPathInLT().length()));
        return downloadDTO;
    }
}
