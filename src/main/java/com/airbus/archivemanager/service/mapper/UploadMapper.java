package com.airbus.archivemanager.service.mapper;

import com.airbus.archivemanager.domain.Upload;
import com.airbus.archivemanager.service.dto.UploadDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Upload} and its DTO {@link UploadDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UploadMapper extends EntityMapper<UploadDTO, Upload> {

    @Mapping(source = "owner.id", target = "ownerId")
    UploadDTO toDto(Upload upload);

    @Mapping(source = "ownerId", target = "owner")
    Upload toEntity(UploadDTO uploadDTO);

    default Upload fromId(Long id) {
        if (id == null) {
            return null;
        }
        Upload upload = new Upload();
        upload.setId(id);
        return upload;
    }
}
