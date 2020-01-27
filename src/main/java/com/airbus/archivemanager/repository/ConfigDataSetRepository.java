package com.airbus.archivemanager.repository;
import com.airbus.archivemanager.domain.ConfigDataSet;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ConfigDataSet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConfigDataSetRepository extends JpaRepository<ConfigDataSet, Long>, JpaSpecificationExecutor<ConfigDataSet> {

}
