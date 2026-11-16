package com.jane.realestate.repository;

import com.jane.realestate.entity.ApiImportFailure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiImportFailureRepository extends JpaRepository<ApiImportFailure,Long> {

    List<ApiImportFailure> findBySggCdAndDealMonthAndResolvedAtIsNull(
            String sggCd,
            String dealMonth
    );

    List<ApiImportFailure> findByImportRunId(Long runId);
}
