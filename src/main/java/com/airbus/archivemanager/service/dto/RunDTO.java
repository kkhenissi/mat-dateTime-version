package com.airbus.archivemanager.service.dto;

import com.airbus.archivemanager.domain.enumeration.RunStatus;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.airbus.archivemanager.domain.Run} entity.
 */
public class RunDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private RunStatus status;

    private String platformHardwareInfo;

    private String description;

    private Set<ToolVersionDTO> toolVersions = new HashSet<>();

    private Long ownerId;

    private Long scenarioId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public RunStatus getStatus() {
        return status;
    }

    public void setStatus(RunStatus status) {
        this.status = status;
    }

    public String getPlatformHardwareInfo() {
        return platformHardwareInfo;
    }

    public void setPlatformHardwareInfo(String platformHardwareInfo) {
        this.platformHardwareInfo = platformHardwareInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ToolVersionDTO> getToolVersions() {
        return toolVersions;
    }

    public void setToolVersions(Set<ToolVersionDTO> toolVersions) {
        this.toolVersions = toolVersions;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long userId) {
        this.ownerId = userId;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
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

        RunDTO runDTO = (RunDTO) o;
        if (runDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), runDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RunDTO{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", platformHardwareInfo='" + getPlatformHardwareInfo() + "'" +
            ", description='" + getDescription() + "'" +
            ", owner=" + getOwnerId() +
            ", scenario=" + getScenarioId() +
            "}";
    }
}
