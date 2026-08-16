package com.jane.realestate.service;

import com.jane.realestate.dto.TransactionResponse;
import com.jane.realestate.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Value("${public-data.service-key}")
    private String serviceKey;


    // API 호출
    // 공공데이터에서 거래 가져오기
    public void fetchTransactions(String sggCd, String dealYmd) {

    }

    // 기존 DB 데이터 호출
    public List<TransactionResponse> getTransactions(
            String sggCd,
            String umdNm,
            Long minAmount,
            Long maxAmount,
            LocalDate fromDate,
            LocalDate toDate) {
        return List.of();
    }
}
