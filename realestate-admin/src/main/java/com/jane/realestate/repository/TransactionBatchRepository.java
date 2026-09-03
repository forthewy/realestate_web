package com.jane.realestate.repository;

import com.jane.realestate.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<Transaction> transactions) {

        String sql = """
                INSERT INTO transactions (
                    apt_name,
                    deal_amount,
                    deal_date,
                    area,
                    floor,
                    build_year,
                    dong,
                    jibun,
                    umd_nm,
                    sgg_cd,
                    deal_type,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        LocalDateTime createdAt = LocalDateTime.now();

        jdbcTemplate.batchUpdate(
                sql,
                transactions,
                500,
                (ps, transaction) -> {
                    ps.setString(1, transaction.getAptName());
                    ps.setLong(2, transaction.getDealAmount());
                    ps.setDate(3, Date.valueOf(transaction.getDealDate()));
                    ps.setObject(4, transaction.getArea());
                    ps.setObject(5, transaction.getFloor());
                    ps.setObject(6, transaction.getBuildYear());
                    ps.setString(7, transaction.getDong());
                    ps.setString(8, transaction.getJibun());
                    ps.setString(9, transaction.getUmdNm());
                    ps.setString(10, transaction.getSggCd());
                    ps.setString(11, transaction.getDealType());
                    ps.setTimestamp(12, Timestamp.valueOf(createdAt));
                }
        );
    }
}