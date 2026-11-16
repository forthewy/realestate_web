package com.jane.realestate.repository;

import com.jane.realestate.entity.ApiImportRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiImportRunRepository extends JpaRepository<ApiImportRun, Long> {

    List<ApiImportRun> findTop10ByOrderByStartedAtDesc();
}
