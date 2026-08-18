package com.jane.realestate.controller;

import com.jane.realestate.dto.TransactionResponse;
import com.jane.realestate.dto.api.TransactionApiItem;
import com.jane.realestate.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @RequestParam(required = false) String sggCd,
            @RequestParam(required = false) String umdNm,
            @RequestParam(required = false) Long minAmount,
            @RequestParam(required = false) Long maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(
                transactionService.getTransactions(sggCd, umdNm, minAmount, maxAmount, fromDate, toDate)
        );
    }

    // API 호출
    // 공공데이터에서 거래 가져오기
    @GetMapping("/getTransactions")
    public ResponseEntity<?> getTransactionsFromApi(
            @RequestParam String sggCd,
            @RequestParam String dealYmd,
            @RequestParam(required = false) Long minAmount,
            @RequestParam(required = false) Long maxAmount
    ) {
        return ResponseEntity.ok(
                transactionService.getTransactionsFromApi(
                        sggCd,
                        dealYmd,
                        minAmount,
                        maxAmount
                )
        );
    }
}
