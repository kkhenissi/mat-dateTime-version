package com.airbus.archivemanager.repository;

import com.airbus.archivemanager.domain.ScenarioFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the ScenarioFile entity.
 */
@Repository
public interface ScenarioFileRepository extends JpaRepository<ScenarioFile, String>, JpaSpecificationExecutor<ScenarioFile> {
    
    @Query("select scenarioFile from ScenarioFile scenarioFile where scenarioFile.owner.login = ?#{principal.username}")
    List<ScenarioFile> findByOwnerIsCurrentUser();

    @Query(value = "select distinct scenarioFile from ScenarioFile scenarioFile left join fetch scenarioFile.scenarios",
        countQuery = "select count(distinct scenarioFile) from ScenarioFile scenarioFile")
    Page<ScenarioFile> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct scenarioFile from ScenarioFile scenarioFile left join fetch scenarioFile.scenarios")
    List<ScenarioFile> findAllWithEagerRelationships();

    @Query("select scenarioFile from ScenarioFile scenarioFile left join fetch scenarioFile.scenarios where scenarioFile.relativePathInST =:relativePathInST")
    Optional<ScenarioFile> findOneWithEagerRelationships(@Param("relativePathInST") String relativePathInST);

}
