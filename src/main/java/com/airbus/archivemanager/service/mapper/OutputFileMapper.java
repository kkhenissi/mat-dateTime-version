package com.airbus.archivemanager.service.mapper;

import com.airbus.archivemanager.domain.OutputFile;
import com.airbus.archivemanager.service.dto.OutputFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link OutputFile} and its DTO {@link OutputFileDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, RunMapper.class, TransferArchiveMapper.class})
public interface OutputFileMapper extends EntityMapper<OutputFileDTO, OutputFile> {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "run.id", target = "runId")
    OutputFileDTO toDto(OutputFile outputFile);

    @Mapping(source = "ownerId", target = "owner")
    @Mapping(source = "runId", target = "run")
    OutputFile toEntity(OutputFileDTO outputFileDTO);

    default OutputFile fromId(String relativePathInST) {
        if (relativePathInST == null) {
            return null;
        }
        OutputFile outputFile = new OutputFile();
        outputFile.setRelativePathInST(relativePathInST);
        return outputFile;
    }
}
