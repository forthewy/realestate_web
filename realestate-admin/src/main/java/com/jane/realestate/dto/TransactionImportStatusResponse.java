package com.jane.realestate.dto;

public record TransactionImportStatusResponse(
        String yearMonth,
        boolean approvable,
        boolean approved
) {
}