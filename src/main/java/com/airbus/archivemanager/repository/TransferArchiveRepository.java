package com.airbus.archivemanager.repository;


import com.airbus.archivemanager.domain.TransferArchive;



import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
 
/**
 * Spring Data  repository for the Transfer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransferArchiveRepository extends JpaRepository<TransferArchive, Long> {

    @Query("select transfer from TransferArchive transfer where transfer.owner.login = ?#{principal.username}")
    List<TransferArchive> findByOwnerIsCurrentUser();

}
