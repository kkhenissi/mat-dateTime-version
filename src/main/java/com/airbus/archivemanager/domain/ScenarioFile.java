package com.airbus.archivemanager.domain;

import com.airbus.archivemanager.domain.enumeration.FileType;
import com.airbus.archivemanager.domain.enumeration.SecurityLevel;
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
 * A ScenarioFile.
 */
@Entity
@Table(name = "scenario_file")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ScenarioFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "relative_path_in_st")
    private String relativePathInST;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "input_type", nullable = false, updatable= false)
    private FileType inputType;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "l_t_insertion_date")
    private LocalDateTime lTInsertionDate;

    @Column(name = "path_in_lt")
    private String pathInLT;

    @Column(name = "format")
    private String format;

    @Column(name = "sub_system_at_origin_of_data")
    private String subSystemAtOriginOfData;

    @Column(name = "time_of_data")
    private LocalDateTime timeOfData;

    @Enumerated(EnumType.STRING)
    @Column(name = "security_level")
    private SecurityLevel securityLevel;

    @Column(name = "crc")
    private String crc;

    @ManyToOne
    @JsonIgnoreProperties("scenarioFiles")
    private User owner;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "scenario_file_scenarios", joinColumns = @JoinColumn(name = "scenario_file_relative_path_in_st", referencedColumnName = "relative_path_in_st"), inverseJoinColumns = @JoinColumn(name = "scenarios_id", referencedColumnName = "id"))
    private Set<Scenario> scenarios = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("inputFiles")
    private DataSet dataset;

    @ManyToOne
    @JsonIgnoreProperties("configFiles")
    private ConfigDataSet configDataset;

    public FileType getInputType() {
        return inputType;
    }

    public void setInputType(FileType inputType) {
        this.inputType = inputType;
    }

    public ScenarioFile inputType(FileType inputType) {
        this.inputType = inputType;
        return this;
    }

    public String getRelativePathInST() {
        return relativePathInST;
    }

    public void setRelativePathInST(String relativePathInST) {
        this.relativePathInST = relativePathInST;
    }

    public ScenarioFile relativePathInST(String relativePathInST) {
        this.relativePathInST = relativePathInST;
        return this;
    }

    public LocalDateTime getlTInsertionDate() {
        return lTInsertionDate;
    }

    public void setlTInsertionDate(LocalDateTime lTInsertionDate) {
        this.lTInsertionDate = lTInsertionDate;
    }

    public ScenarioFile lTInsertionDate(LocalDateTime lTInsertionDate) {
        this.lTInsertionDate = lTInsertionDate;
        return this;
    }

    public String getPathInLT() {
        return pathInLT;
    }

    public void setPathInLT(String pathInLT) {
        this.pathInLT = pathInLT;
    }

    public ScenarioFile pathInLT(String pathInLT) {
        this.pathInLT = pathInLT;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public ScenarioFile fileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public ScenarioFile format(String format) {
        this.format = format;
        return this;
    }

    public String getSubSystemAtOriginOfData() {
        return subSystemAtOriginOfData;
    }

    public void setSubSystemAtOriginOfData(String subSystemAtOriginOfData) {
        this.subSystemAtOriginOfData = subSystemAtOriginOfData;
    }

    public ScenarioFile subSystemAtOriginOfData(String subSystemAtOriginOfData) {
        this.subSystemAtOriginOfData = subSystemAtOriginOfData;
        return this;
    }

    public LocalDateTime getTimeOfData() {
        return timeOfData;
    }

    public void setTimeOfData(LocalDateTime timeOfData) {
        this.timeOfData = timeOfData;
    }

    public ScenarioFile timeOfData(LocalDateTime timeOfData) {
        this.timeOfData = timeOfData;
        return this;
    }

    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    public ScenarioFile securityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
        return this;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public ScenarioFile crc(String crc) {
        this.crc = crc;
        return this;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public ScenarioFile owner(User user) {
        this.owner = user;
        return this;
    }

    public Set<Scenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(Set<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    public ScenarioFile scenarios(Set<Scenario> scenarios) {
        this.scenarios = scenarios;
        return this;
    }

    public ScenarioFile addScenarios(Scenario scenario) {
        this.scenarios.add(scenario);
        scenario.getScenarioFiles().add(this);
        return this;
    }

    public ScenarioFile removeScenarios(Scenario scenario) {
        this.scenarios.remove(scenario);
        scenario.getScenarioFiles().remove(this);
        return this;
    }

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataSet) {
        this.dataset = dataSet;
    }

    public ScenarioFile dataset(DataSet dataSet) {
        this.dataset = dataSet;
        return this;
    }

    public ConfigDataSet getConfigDataset() {
        return configDataset;
    }

    public void setConfigDataset(ConfigDataSet configDataSet) {
        this.configDataset = configDataSet;
    }

    public ScenarioFile configDataset(ConfigDataSet configDataSet) {
        this.configDataset = configDataSet;
        return this;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScenarioFile)) {
            return false;
        }
        return relativePathInST != null && relativePathInST.equals(((ScenarioFile) o).relativePathInST);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ScenarioFile{" +
            ", inputType='" + getInputType() + "'" +
            ", relativePathInST='" + getRelativePathInST() + "'" +
            ", lTInsertionDate='" + getlTInsertionDate() + "'" +
            ", pathInLT='" + getPathInLT() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", format='" + getFormat() + "'" +
            ", subSystemAtOriginOfData='" + getSubSystemAtOriginOfData() + "'" +
            ", timeOfData='" + getTimeOfData() + "'" +
            ", securityLevel='" + getSecurityLevel() + "'" +
            ", crc='" + getCrc() + "'" +
            "}";
    }
}
