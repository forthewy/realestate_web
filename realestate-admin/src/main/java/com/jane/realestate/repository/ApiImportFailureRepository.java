package com.jane.realestate.repository;

import com.jane.realestate.entity.ApiImportFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApiImportFailureRepository extends JpaRepository<ApiImportFailure,Long> {

    List<ApiImportFailure> findBySggCdAndDealMonthAndResolvedAtIsNull(
            String sggCd,
            String dealMonth
    );

    List<ApiImportFailure> findByImportRunId(Long runId);

    @Query("""
    SELECT COUNT(DISTINCT CONCAT(f.sggCd, '-', f.dealMonth))
    FROM ApiImportFailure f
    WHERE f.resolvedAt IS NULL
""")
    long countUnresolvedRegionMonths();
}
