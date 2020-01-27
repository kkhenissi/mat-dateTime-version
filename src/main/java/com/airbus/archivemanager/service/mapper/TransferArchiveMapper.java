package com.airbus.archivemanager.service.mapper;

import com.airbus.archivemanager.domain.TransferArchive;
import com.airbus.archivemanager.service.dto.TransferArchiveDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.undertow.util.Transfer;

/**
 * Mapper for the entity {@link TransferArchive} and its DTO {@link TransferArchiveDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TransferArchiveMapper extends EntityMapper<TransferArchiveDTO, TransferArchive> {

    @Mapping(source = "owner.id", target = "ownerId")
    TransferArchiveDTO toDto(TransferArchive transferArchive);

    @Mapping(source = "ownerId", target = "owner")
    TransferArchive toEntity(TransferArchiveDTO transferArchiveDTO);

    default TransferArchive fromId(Long id) {
        if (id == null) {
            return null;
        }
        TransferArchive transferArchive = new TransferArchive();
        transferArchive.setId(id);
        return transferArchive;
    }
}
