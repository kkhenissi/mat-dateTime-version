package com.airbus.archivemanager.domain;

import com.airbus.archivemanager.domain.enumeration.SimulationModeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A Scenario.
 */
@Entity
@Table(name = "scenario")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Scenario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "simulation_mode")
    private SimulationModeType simulationMode;

    @Column(name = "start_simulated_date")
    private LocalDateTime startSimulatedDate;

    @Column(name = "end_simulated_date")
    private LocalDateTime simulation;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "scenario")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Run> runs = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("scenarios")
    private User owner;


    @ManyToMany(mappedBy = "scenarios")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<ScenarioFile> scenarioFiles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
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

    public Scenario name(String name) {
        this.name = name;
        return this;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Scenario creationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public SimulationModeType getSimulationMode() {
        return simulationMode;
    }

    public Scenario simulationMode(SimulationModeType simulationMode) {
        this.simulationMode = simulationMode;
        return this;
    }

    public LocalDateTime getStartSimulatedDate() {
        return startSimulatedDate;
    }

    public void setStartSimulatedDate(LocalDateTime startSimulatedDate) {
        this.startSimulatedDate = startSimulatedDate;
    }

    public Scenario startSimulatedDate(LocalDateTime startSimulatedDate) {
        this.startSimulatedDate = startSimulatedDate;
        return this;
    }

    public LocalDateTime getEndSimulatedDate() {
        return simulation;
    }

    public void setEndSimulatedDate(LocalDateTime simulation) {
        this.simulation = simulation;
    }

    public Scenario simulation(LocalDateTime simulation) {
        this.simulation = simulation;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Scenario description(String description) {
        this.description = description;
        return this;
    }

    public Set<Run> getRuns() {
        return runs;
    }

    public void setRuns(Set<Run> runs) {
        this.runs = runs;
    }

    public Scenario runs(Set<Run> runs) {
        this.runs = runs;
        return this;
    }

    public Scenario addRuns(Run run) {
        this.runs.add(run);
        run.setScenario(this);
        return this;
    }

    public Scenario removeRuns(Run run) {
        this.runs.remove(run);
        run.setScenario(null);
        return this;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public Scenario owner(User user) {
        this.owner = user;
        return this;
    }

    public Set<ScenarioFile> getScenarioFiles() {
        return scenarioFiles;
    }

    public void setScenarioFiles(Set<ScenarioFile> scenarioFiles) {
        this.scenarioFiles = scenarioFiles;
    }

    public Scenario scenarioFiles(Set<ScenarioFile> scenarioFiles) {
        this.scenarioFiles = scenarioFiles;
        return this;
    }

    public Scenario addScenarioFiles(ScenarioFile scenarioFile) {
        this.scenarioFiles.add(scenarioFile);
        scenarioFile.getScenarios().add(this);
        return this;
    }

    public Scenario removeScenarioFiles(ScenarioFile scenarioFile) {
        this.scenarioFiles.remove(scenarioFile);
        scenarioFile.getScenarios().remove(this);
        return this;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Scenario)) {
            return false;
        }
        return id != null && id.equals(((Scenario) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Scenario{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            ", simulationMode='" + getSimulationMode() + "'" +
            ", startSimulatedDate='" + getStartSimulatedDate() + "'" +
            ", simulation='" + getEndSimulatedDate() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
