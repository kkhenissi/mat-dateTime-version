package com.airbus.archivemanager.repository;

import com.airbus.archivemanager.domain.Run;
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
 * Spring Data  repository for the Run entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RunRepository extends JpaRepository<Run, Long>, JpaSpecificationExecutor<Run> {

    @Query("select run from Run run where run.owner.login = ?#{principal.username}")
    List<Run> findByOwnerIsCurrentUser();

    @Query(value = "select distinct run from Run run left join fetch run.toolVersions",
        countQuery = "select count(distinct run) from Run run")
    Page<Run> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct run from Run run left join fetch run.toolVersions")
    List<Run> findAllWithEagerRelationships();

    @Query("select run from Run run left join fetch run.toolVersions where run.id =:id")
    Optional<Run> findOneWithEagerRelationships(@Param("id") Long id);
}
