package com.jane.realestate.scheduler;

import com.jane.realestate.service.TransactionApiImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionApiScheduler {

    private final TransactionApiImportService transactionApiImportService;

    // 매주 월요일 새벽 3시 실행
    @Scheduled(cron = "0 0 3 * * MON", zone = "Asia/Seoul")
    public void collectTransactions() {

        log.info("주간 실거래 데이터 수집 시작");

        transactionApiImportService.collectAllRegions();

        log.info("주간 실거래 데이터 수집 종료");
    }
}