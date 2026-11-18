package com.jane.realestate.dto;

import com.jane.realestate.enums.ApiImportStatus;
import java.time.LocalDateTime;

public record ApiImportRunResponse(
        Long id,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        ApiImportStatus status,
        int successCount,
        int failureCount
) {}