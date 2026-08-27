package com.jane.realestate.controller;

import com.jane.realestate.dto.ApartmentMapResponse;
import com.jane.realestate.dto.TransactionResponse;
import com.jane.realestate.dto.api.TransactionApiItem;
import com.jane.realestate.service.TransactionApiService;
import com.jane.realestate.service.TransactionDbService;
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

    // DB / API 분기
    private final TransactionService transactionService;

    // DB 직접 조회 (지도용)
    private final TransactionDbService transactionDbService;

    // 실거래 조회
    @GetMapping("/getTransactions")
    public ResponseEntity<?> getTransactions(
            @RequestParam String sggCd,
            @RequestParam String dealYmd,
            @RequestParam(defaultValue = "1") int pageNo
    ) {
        return ResponseEntity.ok(
                transactionService.getTransactions(
                        sggCd,
                        dealYmd,
                        pageNo
                )
        );
    }

    // 지도용 DB 조회
    @GetMapping("/map")
    public ResponseEntity<List<ApartmentMapResponse>> getMapTransactions(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLng,
            @RequestParam Double maxLng,
            @RequestParam(required = false) Long minAmount,
            @RequestParam(required = false) Long maxAmount
    ) {
        return ResponseEntity.ok(
                transactionDbService.getMapTransactions(
                        minLat,
                        maxLat,
                        minLng,
                        maxLng,
                        minAmount,
                        maxAmount
                )
        );
    }
}