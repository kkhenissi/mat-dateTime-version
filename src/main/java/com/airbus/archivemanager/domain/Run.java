package com.airbus.archivemanager.domain;

import com.airbus.archivemanager.domain.enumeration.RunStatus;
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
 * A Run.
 */
@Entity
@Table(name = "run")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Run implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RunStatus status;

    @Column(name = "platform_hardware_info")
    private String platformHardwareInfo;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "run")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<OutputFile> outputFiles = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "run_tool_versions", joinColumns = @JoinColumn(name = "run_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tool_versions_id", referencedColumnName = "id"))
    private Set<ToolVersion> toolVersions = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("runs")
    private User owner;

    @ManyToOne
    @JsonIgnoreProperties("runs")
    private Scenario scenario;

    @OneToMany(mappedBy = "run")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Upload> uploads = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
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

    public Run startDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Run endDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public RunStatus getStatus() {
        return status;
    }

    public void setStatus(RunStatus status) {
        this.status = status;
    }

    public Run status(RunStatus status) {
        this.status = status;
        return this;
    }

    public String getPlatformHardwareInfo() {
        return platformHardwareInfo;
    }

    public void setPlatformHardwareInfo(String platformHardwareInfo) {
        this.platformHardwareInfo = platformHardwareInfo;
    }

    public Run platformHardwareInfo(String platformHardwareInfo) {
        this.platformHardwareInfo = platformHardwareInfo;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Run description(String description) {
        this.description = description;
        return this;
    }

    public Set<OutputFile> getOutputFiles() {
        return outputFiles;
    }

    public void setOutputFiles(Set<OutputFile> outputFiles) {
        this.outputFiles = outputFiles;
    }

    public Run outputFiles(Set<OutputFile> outputFiles) {
        this.outputFiles = outputFiles;
        return this;
    }

    public Run addOutputFiles(OutputFile outputFile) {
        this.outputFiles.add(outputFile);
        outputFile.setRun(this);
        return this;
    }

    public Run removeOutputFiles(OutputFile outputFile) {
        this.outputFiles.remove(outputFile);
        outputFile.setRun(null);
        return this;
    }

    public Set<ToolVersion> getToolVersions() {
        return toolVersions;
    }

    public void setToolVersions(Set<ToolVersion> toolVersions) {
        this.toolVersions = toolVersions;
    }

    public Run toolVersions(Set<ToolVersion> toolVersions) {
        this.toolVersions = toolVersions;
        return this;
    }

    public Run addToolVersions(ToolVersion toolVersion) {
        this.toolVersions.add(toolVersion);
        toolVersion.getRuns().add(this);
        return this;
    }

    public Run removeToolVersions(ToolVersion toolVersion) {
        this.toolVersions.remove(toolVersion);
        toolVersion.getRuns().remove(this);
        return this;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public Run owner(User user) {
        this.owner = user;
        return this;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public Run scenario(Scenario scenario) {
        this.scenario = scenario;
        return this;
    }

    public Set<Upload> getUploads() {
        return uploads;
    }

    public void setUploads(Set<Upload> uploads) {
        this.uploads = uploads;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Run)) {
            return false;
        }
        return id != null && id.equals(((Run) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Run{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", platformHardwareInfo='" + getPlatformHardwareInfo() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
