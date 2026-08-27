package com.jane.realestate.service;

import com.jane.realestate.dto.ApartmentMapResponse;
import com.jane.realestate.dto.PageResponse;
import com.jane.realestate.dto.TransactionResponse;
import com.jane.realestate.entity.Transaction;
import com.jane.realestate.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionDbService {

    private final TransactionRepository transactionRepository;

    // 실거래 DB 조회
    public PageResponse<TransactionResponse> getTransactions(
            String sggCd,
            String dealYmd,
            int pageNo
    ) {
        YearMonth yearMonth = YearMonth.parse(dealYmd);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Pageable pageable = PageRequest.of(
                pageNo - 1,
                30
        );

        Page<Transaction> page =
                transactionRepository.findBySggCdAndDealDateBetween(
                        sggCd,
                        startDate,
                        endDate,
                        pageable
                );

        List<TransactionResponse> transactions =
                page.getContent()
                        .stream()
                        .map(TransactionResponse::from)
                        .toList();

        return PageResponse.<TransactionResponse>builder()
                .items(transactions)
                .pageNo(pageNo)
                .pageSize(page.getSize())
                .totalCount((int) page.getTotalElements())
                .build();
    }


    // 지도용 DB 조회
    public List<ApartmentMapResponse> getMapTransactions(
            Double minLat,
            Double maxLat,
            Double minLng,
            Double maxLng,
            Long minAmount,
            Long maxAmount
    ) {
        return transactionRepository.findMapTransactions(
                        minLat,
                        maxLat,
                        minLng,
                        maxLng,
                        minAmount,
                        maxAmount
                )
                .stream()
                .map(row -> new ApartmentMapResponse(
                        (Long) row[0],
                        (String) row[1],
                        (Double) row[2],
                        (Double) row[3],
                        (Long) row[4],
                        (Long) row[5],
                        (Double) row[6],
                        (Integer) row[7]
                ))
                .toList();
    }
}