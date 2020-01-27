package com.airbus.archivemanager.service.dto;

import com.airbus.archivemanager.domain.enumeration.SimulationModeType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.airbus.archivemanager.domain.Scenario} entity.
 */
public class ScenarioDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private LocalDateTime creationDate;

    private SimulationModeType simulationMode;

    private LocalDateTime startSimulatedDate;

    private LocalDateTime simulation;

    private String description;


    private Long ownerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public SimulationModeType getSimulationMode() {
        return simulationMode;
    }

    public void setSimulationMode(SimulationModeType simulationMode) {
        this.simulationMode = simulationMode;
    }

    public LocalDateTime getStartSimulatedDate() {
        return startSimulatedDate;
    }

    public void setStartSimulatedDate(LocalDateTime startSimulatedDate) {
        this.startSimulatedDate = startSimulatedDate;
    }

    public LocalDateTime getEndSimulatedDate() {
        return simulation;
    }

    public void setEndSimulatedDate(LocalDateTime simulation) {
        this.simulation = simulation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long userId) {
        this.ownerId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScenarioDTO scenarioDTO = (ScenarioDTO) o;
        if (scenarioDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), scenarioDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ScenarioDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            ", simulationMode='" + getSimulationMode() + "'" +
            ", startSimulatedDate='" + getStartSimulatedDate() + "'" +
            ", simulation='" + getEndSimulatedDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", owner=" + getOwnerId() +
            "}";
    }
}
