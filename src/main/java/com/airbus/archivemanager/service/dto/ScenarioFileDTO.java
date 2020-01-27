package com.airbus.archivemanager.service.dto;

import com.airbus.archivemanager.domain.enumeration.FileType;
import com.airbus.archivemanager.domain.enumeration.SecurityLevel;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.airbus.archivemanager.domain.ScenarioFile} entity.
 */
public class ScenarioFileDTO implements Serializable {

    private String fileType;

    private FileType inputType;

    @NotNull
    private String relativePathInST;

    private LocalDateTime lTInsertionDate;

    private String pathInLT;

    private String format;

    private String subSystemAtOriginOfData;

    private LocalDateTime timeOfData;

    private SecurityLevel securityLevel;

    private String crc;

    private Long ownerId;

    private Set<ScenarioDTO> scenarios = new HashSet<>();

    private Long datasetId;

    private Long configDatasetId;

    public FileType getInputType() {
        return inputType;
    }

    public void setInputType(FileType inputType) {
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

    public String getPathInLT() {
        return pathInLT;
    }

    public void setPathInLT(String pathInLT) {
        this.pathInLT = pathInLT;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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

    public void setOwnerId(Long userId) {
        this.ownerId = userId;
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

    public void setDatasetId(Long dataSetId) {
        this.datasetId = dataSetId;
    }

    public Long getConfigDatasetId() {
        return configDatasetId;
    }

    public void setConfigDatasetId(Long configDataSetId) {
        this.configDatasetId = configDataSetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScenarioFileDTO scenarioFileDTO = (ScenarioFileDTO) o;
        if (scenarioFileDTO.getRelativePathInST() == null || getRelativePathInST() == null) {
            return false;
        }
        return Objects.equals(getRelativePathInST(), scenarioFileDTO.getRelativePathInST());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRelativePathInST());
    }

    @Override
    public String toString() {
        return "ScenarioFileDTO{" +
            ", inputType='" + getInputType() + "'" +
            ", relativePathInST='" + getRelativePathInST() + "'" +
            ", lTInsertionDate='" + getlTInsertionDate() + "'" +
            ", pathInLT='" + getPathInLT() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", format='" + getFormat() + "'" +
            ", subSystemAtOriginOfData='" + getSubSystemAtOriginOfData() + "'" +
            ", timeOfData='" + getTimeOfData() + "'" +
            ", securityLevel='" + getSecurityLevel() + "'" +
            ", crc='" + getCrc() + "'" +
            ", owner=" + getOwnerId() +
            ", dataset=" + getDatasetId() +
            ", configDataset=" + getConfigDatasetId() +
            "}";
    }

    public boolean compareMetadata(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScenarioFileDTO scenarioFileDTO = (ScenarioFileDTO) o;
        return
            Objects.equals(relativePathInST, scenarioFileDTO.relativePathInST) &&
                Objects.equals(inputType, scenarioFileDTO.inputType) &&
                Objects.equals(pathInLT, scenarioFileDTO.pathInLT) &&
                Objects.equals(timeOfData, scenarioFileDTO.timeOfData) &&
                Objects.equals(crc, scenarioFileDTO.crc) &&
                Objects.equals(ownerId, scenarioFileDTO.ownerId) &&
                Objects.equals(fileType, scenarioFileDTO.fileType) &&
                Objects.equals(format, scenarioFileDTO.format) &&
                Objects.equals(subSystemAtOriginOfData, scenarioFileDTO.subSystemAtOriginOfData) &&
                (Objects.equals(securityLevel, scenarioFileDTO.securityLevel) || scenarioFileDTO.getSecurityLevel() == null && securityLevel.equals(SecurityLevel.NORMAL)) &&
                Objects.equals(datasetId, scenarioFileDTO.datasetId) &&
                Objects.equals(configDatasetId, scenarioFileDTO.configDatasetId);
    }
}
