package com.jane.realestate.repository;

import com.jane.realestate.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
            SELECT t FROM Transaction t
            WHERE (:sggCd IS NULL OR t.sggCd = :sggCd)
              AND (:umdNm IS NULL OR t.umdNm LIKE CONCAT('%', :umdNm, '%'))
              AND (:minAmount IS NULL OR t.dealAmount >= :minAmount)
              AND (:maxAmount IS NULL OR t.dealAmount <= :maxAmount)
              AND (:fromDate IS NULL OR t.dealDate >= :fromDate)
              AND (:toDate IS NULL OR t.dealDate <= :toDate)
            ORDER BY t.dealDate DESC, t.dealAmount DESC
            """)
    List<Transaction> findByFilters(
            @Param("sggCd") String sggCd,
            @Param("umdNm") String umdNm,
            @Param("minAmount") Long minAmount,
            @Param("maxAmount") Long maxAmount,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            SELECT t.sggCd, COUNT(t), AVG(t.dealAmount), SUM(t.dealAmount)
            FROM Transaction t
            WHERE (:sggCd IS NULL OR t.sggCd = :sggCd)
              AND (:minAmount IS NULL OR t.dealAmount >= :minAmount)
              AND (:maxAmount IS NULL OR t.dealAmount <= :maxAmount)
              AND (:fromDate IS NULL OR t.dealDate >= :fromDate)
              AND (:toDate IS NULL OR t.dealDate <= :toDate)
            GROUP BY t.sggCd
            ORDER BY COUNT(t) DESC
            """)
    List<Object[]> aggregateByRegion(
            @Param("sggCd") String sggCd,
            @Param("minAmount") Long minAmount,
            @Param("maxAmount") Long maxAmount,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    boolean existsByAptNameAndDealDateAndDealAmountAndUmdNm(
            String aptName, LocalDate dealDate, Long dealAmount, String umdNm
    );
}
