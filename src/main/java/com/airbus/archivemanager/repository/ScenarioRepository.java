package com.airbus.archivemanager.repository;

import com.airbus.archivemanager.domain.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Scenario entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long>, JpaSpecificationExecutor<Scenario> {

    @Query("select scenario from Scenario scenario where scenario.owner.login = ?#{principal.username}")
    List<Scenario> findByOwnerIsCurrentUser();

}
