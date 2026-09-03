package com.jane.realestate.service;

import com.jane.realestate.dto.TransactionImportResponse;
import com.jane.realestate.dto.TransactionImportStatusResponse;
import com.jane.realestate.entity.TransactionImport;
import com.jane.realestate.entity.TransactionStoredMonth;
import com.jane.realestate.repository.TransactionImportRepository;
import com.jane.realestate.repository.TransactionStoredMonthRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final TransactionImportRepository transactionImportRepository;
    private final TransactionStoredMonthRepository transactionStoredMonthRepository;

    // ---------- Import 이력 관리 ----------
    // 월별 Import 이력 조회
    public List<TransactionImportResponse> getImports(String yearMonth) {

        YearMonth month = YearMonth.parse(yearMonth);

        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();

        return transactionImportRepository
                .findImportedMonth(monthStart, monthEnd)
                .stream()
                .map(transactionImport ->
                        new TransactionImportResponse(
                                transactionImport.getId(),
                                transactionImport.getStartDate(),
                                transactionImport.getEndDate(),
                                transactionImport.getTransactionCount(),
                                transactionImport.getSkippedCount(),
                                transactionImport.getImportedAt()
                        )
                )
                .toList();
    }



    // 해당 월의 데이터 등록 및 승인 상태 조회
    public TransactionImportStatusResponse getImportStatus(String yearMonth) {

        YearMonth month = YearMonth.parse(yearMonth);

        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();

        List<TransactionImport> imports =
                transactionImportRepository
                        .findImportedMonth(
                                monthStart,
                                monthEnd
                        );

        boolean approvable =
                isImportApprovable(month, imports);

        boolean approved =
                transactionStoredMonthRepository
                        .existsByYearMonth(yearMonth);

        return new TransactionImportStatusResponse(
                yearMonth,
                approvable,
                approved
        );
    }

    // 해당 월 전체 기간의 데이터가 등록되었는지 확인
    private boolean isImportApprovable(
            YearMonth month,
            List<TransactionImport> imports
    ) {
        if (imports.isEmpty()) {
            return false;
        }

        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();

        LocalDate coveredUntil = monthStart.minusDays(1);

        for (TransactionImport transactionImport : imports) {

            LocalDate startDate = transactionImport.getStartDate();
            LocalDate endDate = transactionImport.getEndDate();

            // 아직 채워진 날짜 다음보다 뒤에서 시작하면 중간에 빈 날짜가 있음
            if (startDate.isAfter(coveredUntil.plusDays(1))) {
                return false;
            }

            // 기존 범위보다 더 뒤까지 채우는 Import라면 범위 확장
            if (endDate.isAfter(coveredUntil)) {
                coveredUntil = endDate;
            }

            // 해당 월 마지막 날까지 도달
            if (!coveredUntil.isBefore(monthEnd)) {
                return true;
            }
        }

        return false;
    }

    // ---------- DB 승인 관리 ----------
    // 해당 월의 실거래 데이터를 조회 가능 상태로 승인
    @Transactional
    public void approveStoredMonth(String yearMonth) {

        YearMonth month = YearMonth.parse(yearMonth);

        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();

        List<TransactionImport> imports =
                transactionImportRepository.findImportedMonth(
                        monthStart,
                        monthEnd
                );

        if (!isImportApprovable(month, imports)) {
            throw new IllegalStateException(
                    "해당 월의 데이터가 모두 등록되지 않았습니다."
            );
        }

        if (transactionStoredMonthRepository.existsByYearMonth(yearMonth)) {
            return;
        }

        TransactionStoredMonth storedMonth =
                TransactionStoredMonth.builder()
                        .yearMonth(yearMonth)
                        .build();

        transactionStoredMonthRepository.save(storedMonth);
    }
    // 해당 월의 승인 취소
    @Transactional
    public void cancelStoredMonth(String yearMonth) {
        transactionStoredMonthRepository.deleteByYearMonth(yearMonth);
    }
}