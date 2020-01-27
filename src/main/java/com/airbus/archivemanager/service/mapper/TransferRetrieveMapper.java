package com.airbus.archivemanager.service.mapper;

import com.airbus.archivemanager.domain.TransferRetrieve;
import com.airbus.archivemanager.service.dto.TransferRetrieveDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.undertow.util.Transfer;

/**
 * Mapper for the entity {@link TransferArchive} and its DTO {@link TransferRetrieveDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TransferRetrieveMapper extends EntityMapper<TransferRetrieveDTO, TransferRetrieve> {

    @Mapping(source = "owner.id", target = "ownerId")
    TransferRetrieveDTO toDto(TransferRetrieve transferRetrieve);

    @Mapping(source = "ownerId", target = "owner")
    TransferRetrieve toEntity(TransferRetrieveDTO transferRetrieveDTO);

    default TransferRetrieve fromId(Long id) {
        if (id == null) {
            return null;
        }
        TransferRetrieve transferRetrieve = new TransferRetrieve();
        transferRetrieve.setId(id);
        return transferRetrieve;
    }
}
