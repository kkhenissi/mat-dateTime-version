package com.airbus.archivemanager.service.mapper;

import com.airbus.archivemanager.domain.*;
import com.airbus.archivemanager.service.dto.DataSetDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link DataSet} and its DTO {@link DataSetDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DataSetMapper extends EntityMapper<DataSetDTO, DataSet> {


    @Mapping(target = "inputFiles", ignore = true)
    @Mapping(target = "removeInputFiles", ignore = true)
    DataSet toEntity(DataSetDTO dataSetDTO);

    default DataSet fromId(Long id) {
        if (id == null) {
            return null;
        }
        DataSet dataSet = new DataSet();
        dataSet.setId(id);
        return dataSet;
    }
}
