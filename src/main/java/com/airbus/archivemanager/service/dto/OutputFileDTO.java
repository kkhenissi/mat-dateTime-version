package com.airbus.archivemanager.service.dto;

import com.airbus.archivemanager.domain.enumeration.SecurityLevel;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.airbus.archivemanager.domain.OutputFile} entity.
 */
public class OutputFileDTO implements Serializable {


    @NotNull
    private String relativePathInST;

    private LocalDateTime lTInsertionDate;

    private String pathInLT;

    private String fileType;

    private String format;

    private String subSystemAtOriginOfData;

    private SecurityLevel securityLevel;

    private String crc;

    private Long ownerId;

    private Long runId;


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

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OutputFileDTO outputFileDTO = (OutputFileDTO) o;
        if (outputFileDTO.getRelativePathInST() == null || getRelativePathInST() == null) {
            return false;
        }
        return Objects.equals(getRelativePathInST(), outputFileDTO.getRelativePathInST());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRelativePathInST());
    }

    @Override
    public String toString() {
        return "OutputFileDTO{" + "relativePathInST='" + getRelativePathInST() + "'"
            + ", lTInsertionDate='" + getlTInsertionDate() + "'" + ", pathInLT='" + getPathInLT() + "'"
            + ", fileType='" + getFileType() + "'" + ", format='" + getFormat() + "'"
            + ", subSystemAtOriginOfData='" + getSubSystemAtOriginOfData() + ", securityLevel='" + getSecurityLevel() + "'"
            + ", crc='" + getCrc() + "'"
            + ", owner=" + getOwnerId() + ", run=" + getRunId() + "}";
    }

    public boolean compareMetadata(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OutputFileDTO outputFileDTO = (OutputFileDTO) o;
        return
            Objects.equals(relativePathInST, outputFileDTO.relativePathInST) &&
                Objects.equals(pathInLT, outputFileDTO.pathInLT) &&
                Objects.equals(crc, outputFileDTO.crc) &&
                Objects.equals(runId, outputFileDTO.runId) &&
                Objects.equals(ownerId, outputFileDTO.ownerId) &&
                Objects.equals(fileType, outputFileDTO.fileType) &&
                Objects.equals(format, outputFileDTO.format) &&
                Objects.equals(subSystemAtOriginOfData, outputFileDTO.subSystemAtOriginOfData) &&
                (Objects.equals(securityLevel, outputFileDTO.securityLevel) || outputFileDTO.getSecurityLevel() == null && securityLevel.equals(SecurityLevel.NORMAL));
    }
}
