package com.airbus.archivemanager.service.mapper;

import com.airbus.archivemanager.domain.Scenario;
import com.airbus.archivemanager.service.dto.ScenarioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Scenario} and its DTO {@link ScenarioDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ScenarioMapper extends EntityMapper<ScenarioDTO, Scenario> {

    @Mapping(source = "owner.id", target = "ownerId")
    ScenarioDTO toDto(Scenario scenario);

    @Mapping(target = "runs", ignore = true)
    @Mapping(target = "removeRuns", ignore = true)
    @Mapping(source = "ownerId", target = "owner")
    @Mapping(target = "scenarioFiles", ignore = true)
    @Mapping(target = "removeScenarioFiles", ignore = true)
    Scenario toEntity(ScenarioDTO scenarioDTO);

    default Scenario fromId(Long id) {
        if (id == null) {
            return null;
        }
        Scenario scenario = new Scenario();
        scenario.setId(id);
        return scenario;
    }
}
