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
 * Criteria class for the {@link com.airbus.archivemanager.domain.Upload} entity. This class is used
 * in {@link com.airbus.archivemanager.web.rest.UploadResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /config-data-sets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link LocalFilter} class are used, we need to use
 * fix type specific filters.
 */
public class UploadCriteria implements Serializable, Criteria {
    private static final long serialVersionUID = 1L;
    private LongFilter id;
    private StringFilter relativePathInST;
    private LocalDateTimeFilter lTInsertionDate;
    private StringFilter pathInLT;
    private LongFilter ownerId;
    private StringFilter inputType;

    public UploadCriteria() {
    }

    public UploadCriteria(UploadCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.relativePathInST = other.relativePathInST == null ? null : other.relativePathInST.copy();
        this.lTInsertionDate = other.lTInsertionDate == null ? null : other.lTInsertionDate.copy();
        this.pathInLT = other.pathInLT == null ? null : other.pathInLT.copy();
        this.ownerId = other.ownerId == null ? null : other.ownerId.copy();
        this.inputType = other.inputType == null ? null : other.inputType.copy();
    }

    @Override
    public UploadCriteria copy() {
        return new UploadCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
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

    public LongFilter getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(LongFilter ownerId) {
        this.ownerId = ownerId;
    }

    public StringFilter getInputType() {
        return inputType;
    }

    public void setInputType(StringFilter inputType) {
        this.inputType = inputType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UploadCriteria that = (UploadCriteria) o;
        return
            Objects.equals(id, that.id) &&
                Objects.equals(relativePathInST, that.relativePathInST) &&
                Objects.equals(lTInsertionDate, that.lTInsertionDate) &&
                Objects.equals(pathInLT, that.pathInLT) &&
                Objects.equals(inputType, that.inputType) &&
                Objects.equals(ownerId, that.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            relativePathInST,
            lTInsertionDate,
            pathInLT,
            inputType,
            ownerId
        );
    }

    @Override
    public String toString() {
        return "UploadCriteria{" +
            (id != null ? "inputType=" + id + ", " : "") +
            (relativePathInST != null ? "relativePathInST=" + relativePathInST + ", " : "") +
            (lTInsertionDate != null ? "lTInsertionDate=" + lTInsertionDate + ", " : "") +
            (pathInLT != null ? "pathInLT=" + pathInLT + ", " : "") +
            (inputType != null ? "pathInLT=" + inputType + ", " : "") +
            (ownerId != null ? "ownerId=" + ownerId + ", " : "") +
            "}";
    }

    /**
     * Class for filtering SecurityLevel
     */
    public static class SecurityLevelFilter extends LocalFilter<SecurityLevel> {

        public SecurityLevelFilter() {
        }

        public SecurityLevelFilter(UploadCriteria.SecurityLevelFilter filter) {
            super(filter);
        }

        @Override
        public UploadCriteria.SecurityLevelFilter copy() {
            return new UploadCriteria.SecurityLevelFilter(this);
        }
    }
}
