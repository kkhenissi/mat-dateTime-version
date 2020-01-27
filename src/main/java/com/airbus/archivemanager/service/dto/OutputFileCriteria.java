package com.airbus.archivemanager.service.dto;

import com.airbus.archivemanager.domain.enumeration.SecurityLevel;
import com.airbus.archivemanager.service.dto.filter.LocalDateTimeFilter;
import com.airbus.archivemanager.service.dto.filter.LocalFilter;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;


/**
 * Criteria class for the {@link com.airbus.archivemanager.domain.OutputFile} entity. This class is used
 * in {@link com.airbus.archivemanager.web.rest.OutputFileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /output-files?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link LocalFilter} class are used, we need to use
 * fix type specific filters.
 */
public class OutputFileCriteria implements Serializable, Criteria {
    private static final long serialVersionUID = 1L;
    private StringFilter relativePathInST;
    private LocalDateTimeFilter lTInsertionDate;
    private StringFilter pathInLT;
    private StringFilter fileType;
    private StringFilter format;
    private StringFilter subSystemAtOriginOfData;
    private SecurityLevelFilter securityLevel;
    private StringFilter crc;
    private LongFilter ownerId;
    private LongFilter runId;

    public OutputFileCriteria() {
    }

    public OutputFileCriteria(OutputFileCriteria other) {
        this.relativePathInST = other.relativePathInST == null ? null : other.relativePathInST.copy();
        this.lTInsertionDate = other.lTInsertionDate == null ? null : other.lTInsertionDate.copy();
        this.pathInLT = other.pathInLT == null ? null : other.pathInLT.copy();
        this.fileType = other.fileType == null ? null : other.fileType.copy();
        this.format = other.format == null ? null : other.format.copy();
        this.subSystemAtOriginOfData = other.subSystemAtOriginOfData == null ? null : other.subSystemAtOriginOfData.copy();
        this.securityLevel = other.securityLevel == null ? null : other.securityLevel.copy();
        this.crc = other.crc == null ? null : other.crc.copy();
        this.ownerId = other.ownerId == null ? null : other.ownerId.copy();
        this.runId = other.runId == null ? null : other.runId.copy();
    }

    @Override
    public OutputFileCriteria copy() {
        return new OutputFileCriteria(this);
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

    public LongFilter getRunId() {
        return runId;
    }

    public void setRunId(LongFilter runId) {
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
        final OutputFileCriteria that = (OutputFileCriteria) o;
        return
            Objects.equals(relativePathInST, that.relativePathInST) &&
                Objects.equals(lTInsertionDate, that.lTInsertionDate) &&
                Objects.equals(pathInLT, that.pathInLT) &&
                Objects.equals(fileType, that.fileType) &&
                Objects.equals(format, that.format) &&
                Objects.equals(subSystemAtOriginOfData, that.subSystemAtOriginOfData) &&
                Objects.equals(securityLevel, that.securityLevel) &&
                Objects.equals(crc, that.crc) &&
                Objects.equals(ownerId, that.ownerId) &&
                Objects.equals(runId, that.runId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            relativePathInST,
            lTInsertionDate,
            pathInLT,
            fileType,
            format,
            subSystemAtOriginOfData,
            securityLevel,
            crc,
            ownerId,
            runId
        );
    }

    @Override
    public String toString() {
        return "OutputFileCriteria{" +
            (relativePathInST != null ? "relativePathInST=" + relativePathInST + ", " : "") +
            (lTInsertionDate != null ? "lTInsertionDate=" + lTInsertionDate + ", " : "") +
            (pathInLT != null ? "pathInLT=" + pathInLT + ", " : "") +
            (fileType != null ? "fileType=" + fileType + ", " : "") +
            (format != null ? "format=" + format + ", " : "") +
            (subSystemAtOriginOfData != null ? "subSystemAtOriginOfData=" + subSystemAtOriginOfData + ", " : "") +
            (securityLevel != null ? "securityLevel=" + securityLevel + ", " : "") +
            (crc != null ? "crc=" + crc + ", " : "") +
            (ownerId != null ? "ownerId=" + ownerId + ", " : "") +
            (runId != null ? "runId=" + runId + ", " : "") +
            "}";
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
