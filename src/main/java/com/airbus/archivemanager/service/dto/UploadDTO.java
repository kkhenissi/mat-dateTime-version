package com.airbus.archivemanager.service.dto;

import com.airbus.archivemanager.domain.enumeration.FileType;
import com.airbus.archivemanager.domain.enumeration.SecurityLevel;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the uploading file.
 */
public class UploadDTO implements Serializable {

    private Long id;

    private String localPath;

    private String lTSPath;

    private String fileType;

    private String inputType;

    @NotNull
    private String relativePathInST;

    private LocalDateTime lTInsertionDate;

    private String format;

    private String subSystemAtOriginOfData;

    private LocalDateTime timeOfData;

    private SecurityLevel securityLevel;

    private String crc;

    private Long ownerId;

    private Set<ScenarioDTO> scenarios = new HashSet<>();

    private Long datasetId;

    private Long configDatasetId;

    private Long runId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getRelativePathInST() {
        return relativePathInST;
    }

    public void setRelativePathInST(String relativePathInST) {
        this.relativePathInST = relativePathInST;
    }

    public LocalDateTime getlTInsertionDate() {
        return lTInsertionDate;
    }

    public void setlTInsertionDate(LocalDateTime lTInsertionDate) {
        this.lTInsertionDate = lTInsertionDate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSubSystemAtOriginOfData() {
        return subSystemAtOriginOfData;
    }

    public void setSubSystemAtOriginOfData(String subSystemAtOriginOfData) {
        this.subSystemAtOriginOfData = subSystemAtOriginOfData;
    }

    public LocalDateTime getTimeOfData() {
        return timeOfData;
    }

    public void setTimeOfData(LocalDateTime timeOfData) {
        this.timeOfData = timeOfData;
    }

    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Set<ScenarioDTO> getScenarios() {
        return scenarios;
    }

    public void setScenarios(Set<ScenarioDTO> scenarios) {
        this.scenarios = scenarios;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public Long getConfigDatasetId() {
        return configDatasetId;
    }

    public void setConfigDatasetId(Long configDatasetId) {
        this.configDatasetId = configDatasetId;
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }
}
