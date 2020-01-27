package com.airbus.archivemanager.service.mapper;

import com.airbus.archivemanager.domain.Run;
import com.airbus.archivemanager.service.dto.RunDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Run} and its DTO {@link RunDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, ScenarioMapper.class})
public interface RunMapper extends EntityMapper<RunDTO, Run> {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "scenario.id", target = "scenarioId")
    RunDTO toDto(Run run);

    @Mapping(target = "outputFiles", ignore = true)
    @Mapping(target = "removeOutputFiles", ignore = true)
    @Mapping(target = "removeToolVersions", ignore = true)
    @Mapping(source = "ownerId", target = "owner")
    @Mapping(source = "scenarioId", target = "scenario")
    Run toEntity(RunDTO runDTO);

    default Run fromId(Long id) {
        if (id == null) {
            return null;
        }
        Run run = new Run();
        run.setId(id);
        return run;
    }
}
