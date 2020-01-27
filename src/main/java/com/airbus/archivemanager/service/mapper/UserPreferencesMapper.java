package com.airbus.archivemanager.service.mapper;

import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.service.dto.UserPreferencesDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserPreferences} and its DTO {@link UserPreferencesDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserPreferencesMapper extends EntityMapper<UserPreferencesDTO, UserPreferences> {

    @Mapping(source = "user.id", target = "userId")
    UserPreferencesDTO toDto(UserPreferences userPreferences);

    @Mapping(source = "userId", target = "user")
    UserPreferences toEntity(UserPreferencesDTO userPreferencesDTO);

    default UserPreferences fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setId(id);
        return userPreferences;
    }
}
