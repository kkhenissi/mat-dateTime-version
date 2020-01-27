package com.airbus.archivemanager.service.dto;

import com.airbus.archivemanager.domain.enumeration.SimulationModeType;
import com.airbus.archivemanager.service.dto.filter.LocalDateTimeFilter;
import com.airbus.archivemanager.service.dto.filter.LocalFilter;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link com.airbus.archivemanager.domain.Scenario} entity. This class is used
 * in {@link com.airbus.archivemanager.web.rest.ScenarioResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /scenarios?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link LocalFilter} class are used, we need to use
 * fix type specific filters.
 */
public class ScenarioCriteria implements Serializable, Criteria {
    private static final long serialVersionUID = 1L;
    private LongFilter id;
    private StringFilter name;
    private LocalDateTimeFilter creationDate;
    private SimulationModeTypeFilter simulationMode;
    private LocalDateTimeFilter startSimulatedDate;
    private LocalDateTimeFilter simulation;
    private StringFilter description;
    private LongFilter runsId;
    private LongFilter ownerId;
    private StringFilter scenarioFilesRelativePathInST;

    public ScenarioCriteria() {
    }

    public ScenarioCriteria(ScenarioCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.creationDate = other.creationDate == null ? null : other.creationDate.copy();
        this.simulationMode = other.simulationMode == null ? null : other.simulationMode.copy();
        this.startSimulatedDate = other.startSimulatedDate == null ? null : other.startSimulatedDate.copy();
        this.simulation = other.simulation == null ? null : other.simulation.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.runsId = other.runsId == null ? null : other.runsId.copy();
        this.ownerId = other.ownerId == null ? null : other.ownerId.copy();
        this.scenarioFilesRelativePathInST = other.scenarioFilesRelativePathInST == null ? null : other.scenarioFilesRelativePathInST.copy();
    }

    @Override
    public ScenarioCriteria copy() {
        return new ScenarioCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LocalDateTimeFilter getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTimeFilter creationDate) {
        this.creationDate = creationDate;
    }

    public SimulationModeTypeFilter getSimulationMode() {
        return simulationMode;
    }

    public void setSimulationMode(SimulationModeTypeFilter simulationMode) {
        this.simulationMode = simulationMode;
    }

    public LocalDateTimeFilter getStartSimulatedDate() {
        return startSimulatedDate;
    }

    public void setStartSimulatedDate(LocalDateTimeFilter startSimulatedDate) {
        this.startSimulatedDate = startSimulatedDate;
    }

    public LocalDateTimeFilter getEndSimulatedDate() {
        return simulation;
    }

    public void setEndSimulatedDate(LocalDateTimeFilter simulation) {
        this.simulation = simulation;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public LongFilter getRunsId() {
        return runsId;
    }

    public void setRunsId(LongFilter runsId) {
        this.runsId = runsId;
    }

    public LongFilter getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(LongFilter ownerId) {
        this.ownerId = ownerId;
    }

    public StringFilter getScenarioFilesRelativePathInST() {
        return scenarioFilesRelativePathInST;
    }

    public void setScenarioFilesRelativePathInST(StringFilter scenarioFilesRelativePathInST) {
        this.scenarioFilesRelativePathInST = scenarioFilesRelativePathInST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ScenarioCriteria that = (ScenarioCriteria) o;
        return
            Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(simulationMode, that.simulationMode) &&
                Objects.equals(startSimulatedDate, that.startSimulatedDate) &&
                Objects.equals(simulation, that.simulation) &&
                Objects.equals(description, that.description) &&
                Objects.equals(runsId, that.runsId) &&
                Objects.equals(ownerId, that.ownerId) &&
                Objects.equals(scenarioFilesRelativePathInST, that.scenarioFilesRelativePathInST);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            creationDate,
            simulationMode,
            startSimulatedDate,
            simulation,
            description,
            runsId,
            ownerId,
            scenarioFilesRelativePathInST
        );
    }

    @Override
    public String toString() {
        return "ScenarioCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (creationDate != null ? "creationDate=" + creationDate + ", " : "") +
            (simulationMode != null ? "simulationMode=" + simulationMode + ", " : "") +
            (startSimulatedDate != null ? "startSimulatedDate=" + startSimulatedDate + ", " : "") +
            (simulation != null ? "simulation=" + simulation + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (runsId != null ? "runsId=" + runsId + ", " : "") +
            (ownerId != null ? "ownerId=" + ownerId + ", " : "") +
            (scenarioFilesRelativePathInST != null ? "scenarioFilesId=" + scenarioFilesRelativePathInST + ", " : "") +
            "}";
    }

    /**
     * Class for filtering SimulationModeType
     */
    public static class SimulationModeTypeFilter extends LocalFilter<SimulationModeType> {

        public SimulationModeTypeFilter() {
        }

        public SimulationModeTypeFilter(SimulationModeTypeFilter filter) {
            super(filter);
        }

        @Override
        public SimulationModeTypeFilter copy() {
            return new SimulationModeTypeFilter(this);
        }

    }

}
