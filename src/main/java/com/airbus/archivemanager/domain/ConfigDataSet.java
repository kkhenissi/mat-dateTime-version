package com.airbus.archivemanager.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A ConfigDataSet.
 */
@Entity
@Table(name = "config_data_set")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ConfigDataSet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @OneToMany(mappedBy = "configDataset")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ScenarioFile> configFiles = new HashSet<>();

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

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

    public ConfigDataSet name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ScenarioFile> getConfigFiles() {
        return configFiles;
    }

    public ConfigDataSet configFiles(Set<ScenarioFile> scenarioFiles) {
        this.configFiles = scenarioFiles;
        return this;
    }

    public ConfigDataSet addConfigFiles(ScenarioFile scenarioFile) {
        this.configFiles.add(scenarioFile);
        scenarioFile.setConfigDataset(this);
        return this;
    }

    public ConfigDataSet removeConfigFiles(ScenarioFile scenarioFile) {
        this.configFiles.remove(scenarioFile);
        scenarioFile.setConfigDataset(null);
        return this;
    }

    public void setConfigFiles(Set<ScenarioFile> scenarioFiles) {
        this.configFiles = scenarioFiles;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigDataSet)) {
            return false;
        }
        return id != null && id.equals(((ConfigDataSet) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ConfigDataSet{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
