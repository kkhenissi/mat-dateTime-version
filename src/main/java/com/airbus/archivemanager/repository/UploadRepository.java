package com.airbus.archivemanager.repository;

import com.airbus.archivemanager.domain.Upload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Upload entity.
 */
@Repository
public interface UploadRepository extends JpaRepository<Upload, Long>, JpaSpecificationExecutor<Upload> {

    @Query("select upload from Upload upload where upload.owner.login = ?#{principal.username}")
    List<Upload> findByOwnerIsCurrentUser();
}
