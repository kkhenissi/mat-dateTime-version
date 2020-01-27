package com.airbus.archivemanager.service.dto;

import com.airbus.archivemanager.domain.enumeration.SimulationModeType;
import com.airbus.archivemanager.service.dto.filter.LocalFilter;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link com.airbus.archivemanager.domain.DataSet} entity. This class is used
 * in {@link com.airbus.archivemanager.web.rest.DataSetResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /data-sets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link LocalFilter} class are used, we need to use
 * fix type specific filters.
 */
public class DataSetCriteria implements Serializable, Criteria {
    private static final long serialVersionUID = 1L;
    private LongFilter id;
    private StringFilter name;


    public DataSetCriteria() {
    }

    public DataSetCriteria(DataSetCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
    }

    @Override
    public DataSetCriteria copy() {
        return new DataSetCriteria(this);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DataSetCriteria that = (DataSetCriteria) o;
        return
            Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name
        );
    }

    @Override
    public String toString() {
        return "ScenarioCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
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
