package com.airbus.archivemanager.repository;

import com.airbus.archivemanager.domain.OutputFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the OutputFile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OutputFileRepository extends JpaRepository<OutputFile, String>, JpaSpecificationExecutor<OutputFile> {

    @Query("select outputFile from OutputFile outputFile where outputFile.owner.login = ?#{principal.username}")
    List<OutputFile> findByOwnerIsCurrentUser();

    Optional<OutputFile> findByRelativePathInST(String relativePathInST);

}
