package com.airbus.archivemanager.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.airbus.archivemanager.domain.UserPreferences} entity.
 */
public class UserPreferencesDTO implements Serializable {

    private Long id;


    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserPreferencesDTO userPreferencesDTO = (UserPreferencesDTO) o;
        if (userPreferencesDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userPreferencesDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserPreferencesDTO{" +
            "id=" + getId() +
            ", user=" + getUserId() +
            "}";
    }
}
