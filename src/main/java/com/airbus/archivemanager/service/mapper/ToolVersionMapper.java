package com.airbus.archivemanager.service.mapper;

import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.service.dto.ToolVersionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link ToolVersion} and its DTO {@link ToolVersionDTO}.
 */
@Mapper(componentModel = "spring", uses = {RunMapper.class})
public interface ToolVersionMapper extends EntityMapper<ToolVersionDTO, ToolVersion> {

    ToolVersionDTO toDto(ToolVersion toolVersion);

    @Mapping(target = "runs", ignore = true)
    @Mapping(target = "removeRuns", ignore = true)
    ToolVersion toEntity(ToolVersionDTO toolVersionDTO);

    default ToolVersion fromId(Long id) {
        if (id == null) {
            return null;
        }
        ToolVersion toolVersion = new ToolVersion();
        toolVersion.setId(id);
        return toolVersion;
    }
}
