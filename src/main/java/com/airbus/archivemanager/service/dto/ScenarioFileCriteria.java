package com.airbus.archivemanager.service.dto;

import com.airbus.archivemanager.domain.enumeration.FileType;
import com.airbus.archivemanager.domain.enumeration.SecurityLevel;
import com.airbus.archivemanager.service.dto.filter.LocalDateTimeFilter;
import com.airbus.archivemanager.service.dto.filter.LocalFilter;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;


/**
 * Criteria class for the {@link com.airbus.archivemanager.domain.ScenarioFile} entity. This class is used
 * in {@link com.airbus.archivemanager.web.rest.ScenarioFileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /scenario-files?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link LocalFilter} class are used, we need to use
 * fix type specific filters.
 */
public class ScenarioFileCriteria implements Serializable, Criteria {
    private static final long serialVersionUID = 1L;
    private FileTypeFilter inputType;
    private StringFilter relativePathInST;
    private LocalDateTimeFilter lTInsertionDate;
    private StringFilter pathInLT;
    private StringFilter fileType;
    private StringFilter format;
    private StringFilter subSystemAtOriginOfData;
    private LocalDateTimeFilter timeOfData;
    private SecurityLevelFilter securityLevel;
    private StringFilter crc;
    private LongFilter ownerId;
    private LongFilter scenariosId;
    private LongFilter datasetId;
    private LongFilter configDatasetId;

    public ScenarioFileCriteria() {
    }

    public ScenarioFileCriteria(ScenarioFileCriteria other) {
        this.inputType = other.inputType == null ? null : other.inputType.copy();
        this.relativePathInST = other.relativePathInST == null ? null : other.relativePathInST.copy();
        this.lTInsertionDate = other.lTInsertionDate == null ? null : other.lTInsertionDate.copy();
        this.pathInLT = other.pathInLT == null ? null : other.pathInLT.copy();
        this.fileType = other.fileType == null ? null : other.fileType.copy();
        this.format = other.format == null ? null : other.format.copy();
        this.subSystemAtOriginOfData = other.subSystemAtOriginOfData == null ? null : other.subSystemAtOriginOfData.copy();
        this.timeOfData = other.timeOfData == null ? null : other.timeOfData.copy();
        this.securityLevel = other.securityLevel == null ? null : other.securityLevel.copy();
        this.crc = other.crc == null ? null : other.crc.copy();
        this.ownerId = other.ownerId == null ? null : other.ownerId.copy();
        this.scenariosId = other.scenariosId == null ? null : other.scenariosId.copy();
        this.datasetId = other.datasetId == null ? null : other.datasetId.copy();
        this.configDatasetId = other.configDatasetId == null ? null : other.configDatasetId.copy();
    }

    @Override
    public ScenarioFileCriteria copy() {
        return new ScenarioFileCriteria(this);
    }

    public FileTypeFilter getInputType() {
        return inputType;
    }

    public void setInputType(FileTypeFilter inputType) {
        this.inputType = inputType;
    }

    public StringFilter getRelativePathInST() {
        return relativePathInST;
    }

    public void setRelativePathInST(StringFilter relativePathInST) {
        this.relativePathInST = relativePathInST;
    }

    public LocalDateTimeFilter getlTInsertionDate() {
        return lTInsertionDate;
    }

    public void setlTInsertionDate(LocalDateTimeFilter lTInsertionDate) {
        this.lTInsertionDate = lTInsertionDate;
    }

    public StringFilter getPathInLT() {
        return pathInLT;
    }

    public void setPathInLT(StringFilter pathInLT) {
        this.pathInLT = pathInLT;
    }

    public StringFilter getFileType() {
        return fileType;
    }

    public void setFileType(StringFilter fileType) {
        this.fileType = fileType;
    }

    public StringFilter getFormat() {
        return format;
    }

    public void setFormat(StringFilter format) {
        this.format = format;
    }

    public StringFilter getSubSystemAtOriginOfData() {
        return subSystemAtOriginOfData;
    }

    public void setSubSystemAtOriginOfData(StringFilter subSystemAtOriginOfData) {
        this.subSystemAtOriginOfData = subSystemAtOriginOfData;
    }

    public LocalDateTimeFilter getTimeOfData() {
        return timeOfData;
    }

    public void setTimeOfData(LocalDateTimeFilter timeOfData) {
        this.timeOfData = timeOfData;
    }

    public SecurityLevelFilter getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(SecurityLevelFilter securityLevel) {
        this.securityLevel = securityLevel;
    }

    public StringFilter getCrc() {
        return crc;
    }

    public void setCrc(StringFilter crc) {
        this.crc = crc;
    }

    public LongFilter getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(LongFilter ownerId) {
        this.ownerId = ownerId;
    }

    public LongFilter getScenariosId() {
        return scenariosId;
    }

    public void setScenariosId(LongFilter scenariosId) {
        this.scenariosId = scenariosId;
    }

    public LongFilter getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(LongFilter datasetId) {
        this.datasetId = datasetId;
    }

    public LongFilter getConfigDatasetId() {
        return configDatasetId;
    }

    public void setConfigDatasetId(LongFilter configDatasetId) {
        this.configDatasetId = configDatasetId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ScenarioFileCriteria that = (ScenarioFileCriteria) o;
        return
            Objects.equals(inputType, that.inputType) &&
                Objects.equals(relativePathInST, that.relativePathInST) &&
                Objects.equals(lTInsertionDate, that.lTInsertionDate) &&
                Objects.equals(pathInLT, that.pathInLT) &&
                Objects.equals(fileType, that.fileType) &&
                Objects.equals(format, that.format) &&
                Objects.equals(subSystemAtOriginOfData, that.subSystemAtOriginOfData) &&
                Objects.equals(timeOfData, that.timeOfData) &&
                Objects.equals(securityLevel, that.securityLevel) &&
                Objects.equals(crc, that.crc) &&
                Objects.equals(ownerId, that.ownerId) &&
                Objects.equals(scenariosId, that.scenariosId) &&
                Objects.equals(datasetId, that.datasetId) &&
                Objects.equals(configDatasetId, that.configDatasetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            inputType,
            relativePathInST,
            lTInsertionDate,
            pathInLT,
            fileType,
            format,
            subSystemAtOriginOfData,
            timeOfData,
            securityLevel,
            crc,
            ownerId,
            scenariosId,
            datasetId,
            configDatasetId
        );
    }

    @Override
    public String toString() {
        return "ScenarioFileCriteria{" +
            (inputType != null ? "inputType=" + inputType + ", " : "") +
            (relativePathInST != null ? "relativePathInST=" + relativePathInST + ", " : "") +
            (lTInsertionDate != null ? "lTInsertionDate=" + lTInsertionDate + ", " : "") +
            (pathInLT != null ? "pathInLT=" + pathInLT + ", " : "") +
            (fileType != null ? "fileType=" + fileType + ", " : "") +
            (format != null ? "format=" + format + ", " : "") +
            (subSystemAtOriginOfData != null ? "subSystemAtOriginOfData=" + subSystemAtOriginOfData + ", " : "") +
            (timeOfData != null ? "timeOfData=" + timeOfData + ", " : "") +
            (securityLevel != null ? "securityLevel=" + securityLevel + ", " : "") +
            (crc != null ? "crc=" + crc + ", " : "") +
            (ownerId != null ? "ownerId=" + ownerId + ", " : "") +
            (scenariosId != null ? "scenariosId=" + scenariosId + ", " : "") +
            (datasetId != null ? "datasetId=" + datasetId + ", " : "") +
            (configDatasetId != null ? "configDatasetId=" + configDatasetId + ", " : "") +
            "}";
    }

    /**
     * Class for filtering FileType
     */
    public static class FileTypeFilter extends LocalFilter<FileType> {

        public FileTypeFilter() {
        }

        public FileTypeFilter(FileTypeFilter filter) {
            super(filter);
        }

        @Override
        public FileTypeFilter copy() {
            return new FileTypeFilter(this);
        }

    }

    /**
     * Class for filtering SecurityLevel
     */
    public static class SecurityLevelFilter extends LocalFilter<SecurityLevel> {

        public SecurityLevelFilter() {
        }

        public SecurityLevelFilter(SecurityLevelFilter filter) {
            super(filter);
        }

        @Override
        public SecurityLevelFilter copy() {
            return new SecurityLevelFilter(this);
        }

    }

}
