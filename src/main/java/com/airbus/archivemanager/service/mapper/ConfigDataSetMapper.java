package com.airbus.archivemanager.service.mapper;

import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.service.dto.ConfigDataSetDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link ConfigDataSet} and its DTO {@link ConfigDataSetDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ConfigDataSetMapper extends EntityMapper<ConfigDataSetDTO, ConfigDataSet> {


    @Mapping(target = "configFiles", ignore = true)
    @Mapping(target = "removeConfigFiles", ignore = true)
    ConfigDataSet toEntity(ConfigDataSetDTO configDataSetDTO);

    default ConfigDataSet fromId(Long id) {
        if (id == null) {
            return null;
        }
        ConfigDataSet configDataSet = new ConfigDataSet();
        configDataSet.setId(id);
        return configDataSet;
    }
}
