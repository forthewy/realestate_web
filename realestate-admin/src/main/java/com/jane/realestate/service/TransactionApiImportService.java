package com.jane.realestate.service;

import com.jane.realestate.dto.TransactionSaveData;
import com.jane.realestate.dto.api.TransactionApiItem;
import com.jane.realestate.dto.api.TransactionApiResponse;
import com.jane.realestate.entity.ApiImportRun;
import com.jane.realestate.entity.Region;
import com.jane.realestate.entity.Transaction;
import com.jane.realestate.enums.ApiImportStatus;
import com.jane.realestate.repository.ApiImportRunRepository;
import com.jane.realestate.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionApiImportService {

    private final RegionRepository regionRepository;
    private final TransactionApiService transactionApiService;
    private final TransactionApiReplaceService transactionApiReplaceService;
    private final ApiImportRunRepository apiImportRunRepository;
    private final ApiImportHistoryService apiImportHistoryService;


    public List<TransactionSaveData> convert(
            List<TransactionApiItem> items
    ) {
        // 지역코드 → 지역명
        Map<String, String> regionMap =
                regionRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                Region::getCode,
                                Region::getName
                        ));

        return items.stream()
                .map(item -> {
                    Transaction transaction = item.toEntity();

                    String regionName =
                            regionMap.get(transaction.getSggCd());

                    if (regionName == null) {
                        throw new IllegalArgumentException(
                                "등록되지 않은 지역코드: "
                                        + transaction.getSggCd()
                        );
                    }

                    String fullAddress =
                            regionName + " "
                                    + transaction.getUmdNm() + " "
                                    + transaction.getJibun();

                    return new TransactionSaveData(
                            transaction,
                            fullAddress
                    );
                })
                .toList();
    }

    public List<TransactionApiItem> fetchAll(
            String sggCd,
            String dealYmd
    ) {
        List<TransactionApiItem> allItems = new ArrayList<>();

        int pageNo = 1;

        while (true) {
            TransactionApiResponse response =
                    transactionApiService.getApiResponse(
                            sggCd, dealYmd, pageNo
                    );

            var body = response.getBody();

            if (body == null || body.getTotalCount() == null) {
                throw new IllegalStateException("API 응답 정보가 없습니다.");
            }

            List<TransactionApiItem> items =
                    body.getItems() == null
                            || body.getItems().getItem() == null
                            ? List.of()
                            : body.getItems().getItem();

            if (items.isEmpty()) {
                if (allItems.size() < body.getTotalCount()) {
                    throw new IllegalStateException(
                            "전체 거래 수집 전 빈 페이지 반환: " + pageNo
                    );
                }
                break;
            }

            allItems.addAll(items);

            if (allItems.size() >= body.getTotalCount()) {
                break;
            }

            pageNo++;
        }

        return allItems;
    }

    // 최근 2개월 조회
    public ImportResult collectRecentMonths(String sggCd, ApiImportRun run) {

        YearMonth currentMonth = YearMonth.now(
                ZoneId.of("Asia/Seoul")
        );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMM");

        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < 2; i++) {
            String dealYmd = currentMonth.minusMonths(i)
                    .format(formatter);
            try {
                // API 조회
                List<TransactionApiItem> items =
                        fetchAll(sggCd, dealYmd);

                // 저장 데이터 변환
                List<TransactionSaveData> saveData =
                        convert(items);


                // 기존 거래 삭제 후 새 거래 저장
                transactionApiReplaceService.replace(
                        sggCd,
                        currentMonth.minusMonths(i),
                        saveData
                );

                successCount++;

                log.info("API 수집 완료 - 지역: {}, 거래연월: {}, 건수: {}",
                        sggCd, dealYmd, items.size());



            } catch (RuntimeException e) {
                failureCount++;
                log.error("API 수집 실패 - 지역: {}, 거래연월: {}", sggCd, dealYmd, e);

                // 실패 기록 저장
                try {
                    apiImportHistoryService.recordFailure(
                            run.getId(), sggCd, dealYmd, e
                    );
                } catch (RuntimeException historyException) {
                    log.error("API 실패 이력 저장 실패 - 지역: {}, 거래연월: {}",
                            sggCd, dealYmd, historyException);
                }

                continue;
            }
            // 2. 거래 저장 성공 후 이전 실패 기록 복구
            try {
                apiImportHistoryService.resolveFailures(sggCd, dealYmd);
            } catch (RuntimeException e) {
                log.error("API 실패 이력 복구 처리 실패 - 지역: {}, 거래연월: {}",
                        sggCd, dealYmd, e);
            }
        }

        return new ImportResult(successCount, failureCount);
    }

    // 전국 지역 조회
    public void collectAllRegions() {

        ApiImportRun run = ApiImportRun.builder()
                .startedAt(LocalDateTime.now())
                .status(ApiImportStatus.RUNNING)
                .build();

        run = apiImportRunRepository.save(run);

        int successCount = 0;
        int failureCount = 0;

        try {
            List<Region> regions = regionRepository.findAll();

            if (regions.isEmpty()) {
                throw new IllegalStateException("수집할 지역 정보가 없습니다.");
            }

            // 상위 지역 코드 제외
            for (Region region : regions) {
                if (region.getCode().endsWith("000")) {
                    continue;
                }

                ImportResult result =
                        collectRecentMonths(region.getCode(), run);

                successCount += result.successCount();
                failureCount += result.failureCount();

            }
            run.finish(successCount, failureCount);
        } catch (RuntimeException e) {
            run.fail(successCount, failureCount);
            log.error("전국 API 수집 중단", e);
        } finally {
            apiImportRunRepository.save(run);
        }
    }

    public record ImportResult(int successCount, int failureCount) {
    }

}