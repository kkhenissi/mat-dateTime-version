package com.airbus.archivemanager.service.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.airbus.archivemanager.domain.ToolVersion} entity.
 */
public class ToolVersionDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String version;


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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ToolVersionDTO toolVersionDTO = (ToolVersionDTO) o;
        if (toolVersionDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), toolVersionDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ToolVersionDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", version='" + getVersion() + "'" +
            "}";
    }
}
