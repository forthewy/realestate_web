package com.jane.realestate.repository;

import com.jane.realestate.entity.TransactionStoredMonth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionStoredMonthRepository
        extends JpaRepository<TransactionStoredMonth, Long> {

    boolean existsByYearMonth(String yearMonth);

    void deleteByYearMonth(String yearMonth);
}