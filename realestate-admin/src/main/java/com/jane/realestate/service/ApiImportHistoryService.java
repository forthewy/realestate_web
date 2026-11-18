package com.jane.realestate.service;

import com.jane.realestate.dto.ApiImportFailureResponse;
import com.jane.realestate.dto.ApiImportRunResponse;
import com.jane.realestate.entity.ApiImportFailure;
import com.jane.realestate.entity.ApiImportRun;
import com.jane.realestate.repository.ApiImportFailureRepository;
import com.jane.realestate.repository.ApiImportRunRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiImportHistoryService {

    private final ApiImportFailureRepository failureRepository;
    private final ApiImportRunRepository runRepository;

    @Transactional
    public void resolveFailures(String sggCd, String dealYmd) {

        List<ApiImportFailure> failures =
                failureRepository
                        .findBySggCdAndDealMonthAndResolvedAtIsNull(
                                sggCd, dealYmd
                        );

        for (ApiImportFailure failure : failures) {
            failure.resolve();
        }
    }

    @Transactional
    public void recordFailure(Long runId, String sggCd, String dealYmd, Exception e) {
        ApiImportRun run = runRepository.getReferenceById(runId);

        ApiImportFailure failure = ApiImportFailure.builder()
                .importRun(run)
                .sggCd(sggCd)
                .dealMonth(dealYmd)
                .failedAt(LocalDateTime.now())
                .errorMessage(e.getMessage())
                .build();

        failureRepository.save(failure);
    }

    @Transactional(readOnly = true)
    public List<ApiImportRunResponse> getRecentRuns() {

        return runRepository.findTop10ByOrderByStartedAtDesc()
                .stream()
                .map(run -> new ApiImportRunResponse(
                        run.getId(),
                        run.getStartedAt(),
                        run.getFinishedAt(),
                        run.getStatus(),
                        run.getSuccessCount(),
                        run.getFailureCount()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApiImportFailureResponse> getFailuresByRunId(Long runId) {
        return failureRepository.findByImportRunId(runId)
                .stream()
                .map(failure -> new ApiImportFailureResponse(
                        failure.getId(),
                        failure.getSggCd(),
                        failure.getDealMonth(),
                        failure.getFailedAt(),
                        failure.getResolvedAt(),
                        failure.getErrorMessage()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnresolvedFailureCount() {
        return failureRepository.countUnresolvedRegionMonths();
    }
}
