package com.jane.realestate.service;

import com.jane.realestate.dto.PageResponse;
import com.jane.realestate.dto.TransactionResponse;
import com.jane.realestate.repository.TransactionStoredMonthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionStoredMonthRepository transactionStoredMonthRepository;
    private final TransactionApiService transactionApiService;
    private final TransactionDbService transactionDbService;

    public PageResponse<TransactionResponse> getTransactions(
            String sggCd,
            String dealYmd,
            int pageNo
    ) {
        // 저장된 달이라면 DB 조회
        if (transactionStoredMonthRepository.existsByYearMonth(dealYmd)) {
            log.info("Transaction source: DB, yearMonth={}", dealYmd);
            return transactionDbService.getTransactions(
                    sggCd, dealYmd, pageNo
            );
        }

        // 그외에는 API 조회
        // API 조회시 날짜에 하이픈 제거
        String apiDealYmd = dealYmd.replace("-", "");
        log.info("Transaction source: API, yearMonth={}", dealYmd);
        // API 조회
        return transactionApiService.getTransactions(
                sggCd, apiDealYmd, pageNo
        );
    }
}
