package com.airbus.archivemanager.service.dto;

import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link com.airbus.archivemanager.domain.ToolVersion} entity. This class is used
 * in {@link com.airbus.archivemanager.web.rest.ToolVersionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tool-versions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ToolVersionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter version;

    private LongFilter runId;

    public ToolVersionCriteria() {
    }

    public ToolVersionCriteria(ToolVersionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.version = other.version == null ? null : other.version.copy();
        this.runId = other.runId == null ? null : other.runId.copy();
    }

    @Override
    public ToolVersionCriteria copy() {
        return new ToolVersionCriteria(this);
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

    public StringFilter getVersion() {
        return version;
    }

    public void setVersion(StringFilter version) {
        this.version = version;
    }

    public LongFilter getRunId() {
        return runId;
    }

    public void setRunId(LongFilter runId) {
        this.runId = runId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ToolVersionCriteria that = (ToolVersionCriteria) o;
        return
            Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(version, that.version) &&
                Objects.equals(runId, that.runId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            version,
            runId
        );
    }

    @Override
    public String toString() {
        return "ToolVersionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (version != null ? "version=" + version + ", " : "") +
            (runId != null ? "runId=" + runId + ", " : "") +
            "}";
    }

}
