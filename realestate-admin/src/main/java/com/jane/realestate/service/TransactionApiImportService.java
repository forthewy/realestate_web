package com.jane.realestate.service;

import com.jane.realestate.dto.TransactionSaveData;
import com.jane.realestate.dto.api.TransactionApiItem;
import com.jane.realestate.dto.api.TransactionApiResponse;
import com.jane.realestate.entity.Region;
import com.jane.realestate.entity.Transaction;
import com.jane.realestate.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionApiImportService {

    private final RegionRepository regionRepository;
    private final TransactionApiService transactionApiService;
    private final TransactionApiReplaceService transactionApiReplaceService;

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
    public void collectRecentMonths(String sggCd) {

        YearMonth currentMonth = YearMonth.now(
                ZoneId.of("Asia/Seoul")
        );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMM");


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

                log.info("API 수집 완료 - 지역: {}, 거래연월: {}, 건수: {}",
                        sggCd, dealYmd, items.size());
            } catch (RuntimeException e) {
                log.error("API 수집 실패 - 지역: {}, 거래연월: {}", sggCd, dealYmd, e);
            }
        }
    }

    // 전국 지역 조회
    public void collectAllRegions() {

        List<Region> regions = regionRepository.findAll();

        if (regions.isEmpty()) {
            return;
        }

        // 상위 지역 코드 제외
        for (Region region : regions) {
            if (region.getCode().endsWith("000")) {
                continue;
            }

            collectRecentMonths(region.getCode());
        }
    }
}