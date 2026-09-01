package com.jane.realestate.repository;

import com.jane.realestate.entity.TransactionImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionImportRepository
        extends JpaRepository<TransactionImport, Long> {

    @Query("""
    SELECT i
    FROM TransactionImport i
    WHERE i.startDate <= :monthEnd
      AND i.endDate >= :monthStart
    ORDER BY i.startDate ASC
    """)
    List<TransactionImport> findImportedMonth(
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd
    );

    boolean existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate endDate,
            LocalDate startDate
    );
}