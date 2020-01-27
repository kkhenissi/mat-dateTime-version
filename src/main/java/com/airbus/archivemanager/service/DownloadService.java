package com.airbus.archivemanager.service;

import com.airbus.archivemanager.service.dto.DownloadDTO;

public interface DownloadService {

    /**
     * Post an download.
     *
     * @param downloadDTO contains the relative path in LTS and local path.
     */
    DownloadDTO download(DownloadDTO downloadDTO);
}
