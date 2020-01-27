package com.airbus.archivemanager.repository;

import com.airbus.archivemanager.domain.DataSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the DataSet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DataSetRepository extends JpaRepository<DataSet, Long>, JpaSpecificationExecutor<DataSet> {
}
