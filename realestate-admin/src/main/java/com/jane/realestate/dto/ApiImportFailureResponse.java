package com.jane.realestate.dto;

import java.time.LocalDateTime;

public record ApiImportFailureResponse(
        Long id,
        String sggCd,
        String dealMonth,
        LocalDateTime failedAt,
        LocalDateTime resolvedAt,
        String errorMessage
) {}
