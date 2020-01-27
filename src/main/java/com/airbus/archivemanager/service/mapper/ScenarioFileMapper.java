package com.airbus.archivemanager.service.mapper;

import com.airbus.archivemanager.domain.ScenarioFile;
import com.airbus.archivemanager.service.dto.ScenarioFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link ScenarioFile} and its DTO {@link ScenarioFileDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, ScenarioMapper.class, TransferArchiveMapper.class, DataSetMapper.class, ConfigDataSetMapper.class})
public interface ScenarioFileMapper extends EntityMapper<ScenarioFileDTO, ScenarioFile> {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "dataset.id", target = "datasetId")
    @Mapping(source = "configDataset.id", target = "configDatasetId")
    ScenarioFileDTO toDto(ScenarioFile scenarioFile);

    @Mapping(source = "ownerId", target = "owner")
    @Mapping(target = "removeScenarios", ignore = true)
    @Mapping(source = "datasetId", target = "dataset")
    @Mapping(source = "configDatasetId", target = "configDataset")
    ScenarioFile toEntity(ScenarioFileDTO scenarioFileDTO);

    default ScenarioFile fromId(String relativePathInST) {
        if (relativePathInST == null) {
            return null;
        }
        ScenarioFile scenarioFile = new ScenarioFile();
        scenarioFile.setRelativePathInST(relativePathInST);
        return scenarioFile;
    }
}
