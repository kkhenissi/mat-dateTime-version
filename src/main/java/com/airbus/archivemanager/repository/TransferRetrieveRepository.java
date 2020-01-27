package com.airbus.archivemanager.repository;


import com.airbus.archivemanager.domain.TransferRetrieve;



import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
 
/**
 * Spring Data  repository for the Transfer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransferRetrieveRepository extends JpaRepository<TransferRetrieve, Long> {

    @Query("select transfer from TransferRetrieve transfer where transfer.owner.login = ?#{principal.username}")
    List<TransferRetrieve> findByOwnerIsCurrentUser();

}
