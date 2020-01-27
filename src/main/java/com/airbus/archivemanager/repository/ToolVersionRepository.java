package com.airbus.archivemanager.repository;
import com.airbus.archivemanager.domain.ToolVersion;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ToolVersion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ToolVersionRepository extends JpaRepository<ToolVersion, Long>, JpaSpecificationExecutor<ToolVersion> {

}
