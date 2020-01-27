package com.airbus.archivemanager.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DataSet.
 */
@Entity
@Table(name = "data_set")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DataSet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "dataset")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ScenarioFile> inputFiles = new HashSet<>();

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

    public DataSet name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ScenarioFile> getInputFiles() {
        return inputFiles;
    }

    public DataSet inputFiles(Set<ScenarioFile> scenarioFiles) {
        this.inputFiles = scenarioFiles;
        return this;
    }

    public DataSet addInputFiles(ScenarioFile scenarioFile) {
        this.inputFiles.add(scenarioFile);
        scenarioFile.setDataset(this);
        return this;
    }

    public DataSet removeInputFiles(ScenarioFile scenarioFile) {
        this.inputFiles.remove(scenarioFile);
        scenarioFile.setDataset(null);
        return this;
    }

    public void setInputFiles(Set<ScenarioFile> scenarioFiles) {
        this.inputFiles = scenarioFiles;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataSet)) {
            return false;
        }
        return id != null && id.equals(((DataSet) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "DataSet{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
