package com.airbus.archivemanager.service.dto;

import java.io.Serializable;

/**
 * A DTO for the downloading file.
 */
public class DownloadDTO implements Serializable {

    private String localPath;

    private String lTSPath;

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getlTSPath() {
        return lTSPath;
    }

    public void setlTSPath(String lTSPath) {
        this.lTSPath = lTSPath;
    }
}
