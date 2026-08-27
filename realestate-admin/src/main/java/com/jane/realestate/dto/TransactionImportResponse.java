package com.jane.realestate.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionImportResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        Integer transactionCount,
        Integer skippedCount,
        LocalDateTime importedAt
) {
}