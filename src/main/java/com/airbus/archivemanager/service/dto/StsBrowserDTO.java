package com.airbus.archivemanager.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the list of files and folders in browser.
 */
public class StsBrowserDTO implements Serializable {

    private String relativePath;

    private String relativePathParent;

    private String startPath;

    private Set<String> filesList = new HashSet<>();

    private Set<String> foldersList = new HashSet<>();

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getRelativePathParent() {
        return relativePathParent;
    }

    public void setRelativePathParent(String relativePathParent) {
        this.relativePathParent = relativePathParent;
    }

    public Set<String> getFilesList() {
        return filesList;
    }

    public void setFilesList(Set<String> filesList) {
        this.filesList = filesList;
    }

    public Set<String> getFoldersList() {
        return foldersList;
    }

    public void setFoldersList(Set<String> foldersList) {
        this.foldersList = foldersList;
    }

    public String getStartPath() {
        return startPath;
    }

    public void setStartPath(String startPath) {
        this.startPath = startPath;
    }
}
