package com.airbus.archivemanager.service.dto;

import com.airbus.archivemanager.domain.enumeration.RunStatus;
import com.airbus.archivemanager.service.dto.filter.LocalDateTimeFilter;
import com.airbus.archivemanager.service.dto.filter.LocalFilter;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link com.airbus.archivemanager.domain.Run} entity. This class is used
 * in {@link com.airbus.archivemanager.web.rest.RunResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /runs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link LocalFilter} class are used, we need to use
 * fix type specific filters.
 */
public class RunCriteria implements Serializable, Criteria {
    private static final long serialVersionUID = 1L;
    private LongFilter id;
    private LocalDateTimeFilter startDate;
    private LocalDateTimeFilter endDate;
    private RunStatusFilter status;
    private StringFilter platformHardwareInfo;
    private StringFilter description;
    private StringFilter outputFilesRelativePathInST;
    private LongFilter toolVersionsId;
    private LongFilter ownerId;
    private LongFilter scenarioId;

    public RunCriteria() {
    }

    public RunCriteria(RunCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.endDate = other.endDate == null ? null : other.endDate.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.platformHardwareInfo = other.platformHardwareInfo == null ? null : other.platformHardwareInfo.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.outputFilesRelativePathInST = other.outputFilesRelativePathInST == null ? null : other.outputFilesRelativePathInST.copy();
        this.toolVersionsId = other.toolVersionsId == null ? null : other.toolVersionsId.copy();
        this.ownerId = other.ownerId == null ? null : other.ownerId.copy();
        this.scenarioId = other.scenarioId == null ? null : other.scenarioId.copy();
    }

    @Override
    public RunCriteria copy() {
        return new RunCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateTimeFilter getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTimeFilter startDate) {
        this.startDate = startDate;
    }

    public LocalDateTimeFilter getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTimeFilter endDate) {
        this.endDate = endDate;
    }

    public RunStatusFilter getStatus() {
        return status;
    }

    public void setStatus(RunStatusFilter status) {
        this.status = status;
    }

    public StringFilter getPlatformHardwareInfo() {
        return platformHardwareInfo;
    }

    public void setPlatformHardwareInfo(StringFilter platformHardwareInfo) {
        this.platformHardwareInfo = platformHardwareInfo;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getOutputFilesRelativePathInST() {
        return outputFilesRelativePathInST;
    }

    public void setOutputFilesRelativePathInST(StringFilter outputFilesId) {
        this.outputFilesRelativePathInST = outputFilesId;
    }

    public LongFilter getToolVersionsId() {
        return toolVersionsId;
    }

    public void setToolVersionsId(LongFilter toolVersionsId) {
        this.toolVersionsId = toolVersionsId;
    }

    public LongFilter getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(LongFilter ownerId) {
        this.ownerId = ownerId;
    }

    public LongFilter getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(LongFilter scenarioId) {
        this.scenarioId = scenarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RunCriteria that = (RunCriteria) o;
        return
            Objects.equals(id, that.id) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(status, that.status) &&
                Objects.equals(platformHardwareInfo, that.platformHardwareInfo) &&
                Objects.equals(description, that.description) &&
                Objects.equals(outputFilesRelativePathInST, that.outputFilesRelativePathInST) &&
                Objects.equals(toolVersionsId, that.toolVersionsId) &&
                Objects.equals(ownerId, that.ownerId) &&
                Objects.equals(scenarioId, that.scenarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            startDate,
            endDate,
            status,
            platformHardwareInfo,
            description,
            outputFilesRelativePathInST,
            toolVersionsId,
            ownerId,
            scenarioId
        );
    }

    @Override
    public String toString() {
        return "RunCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (platformHardwareInfo != null ? "platformHardwareInfo=" + platformHardwareInfo + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (outputFilesRelativePathInST != null ? "outputFilesRelativePathInST=" + outputFilesRelativePathInST + ", " : "") +
            (toolVersionsId != null ? "toolVersionsId=" + toolVersionsId + ", " : "") +
            (ownerId != null ? "ownerId=" + ownerId + ", " : "") +
            (scenarioId != null ? "scenarioId=" + scenarioId + ", " : "") +
            "}";
    }

    /**
     * Class for filtering RunStatus
     */
    public static class RunStatusFilter extends Filter<RunStatus> {

        public RunStatusFilter() {
        }

        public RunStatusFilter(RunStatusFilter filter) {
            super(filter);
        }

        @Override
        public RunStatusFilter copy() {
            return new RunStatusFilter(this);
        }

    }

}
