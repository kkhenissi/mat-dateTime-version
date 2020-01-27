package com.airbus.archivemanager.service;

import com.airbus.archivemanager.service.dto.StsBrowserDTO;

import java.io.IOException;

public interface StsBrowserService {

    /**
     * Get all the files and folders within a folder.
     *
     * @param stsBrowserDTO contains the relative path in STS of the folder.
     * @return StsBrowserDTO with lists of files and folders.
     */
    StsBrowserDTO findAll(StsBrowserDTO stsBrowserDTO);
}
