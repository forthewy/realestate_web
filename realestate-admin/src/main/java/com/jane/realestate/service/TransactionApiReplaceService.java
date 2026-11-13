package com.jane.realestate.service;

import com.jane.realestate.dto.TransactionSaveData;
import com.jane.realestate.repository.TransactionBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionApiReplaceService {

    private final TransactionBatchRepository transactionBatchRepository;
    private final TransactionSaveService transactionSaveService;

    @Transactional
    public void replace(
            String sggCd,
            YearMonth dealMonth,
            List<TransactionSaveData> saveData
    ) {
        LocalDate startDate = dealMonth.atDay(1);
        LocalDate endDate = dealMonth.plusMonths(1).atDay(1);

        transactionBatchRepository.deleteByRegionAndPeriod(
                sggCd,
                startDate,
                endDate
        );

        transactionSaveService.save(saveData);
    }
}
