package com.jane.realestate;

import com.jane.realestate.dto.TransactionSaveData;
import com.jane.realestate.dto.api.TransactionApiItem;
import com.jane.realestate.dto.api.TransactionApiResponse;
import com.jane.realestate.repository.TransactionBatchRepository;
import com.jane.realestate.service.TransactionApiImportService;
import com.jane.realestate.service.TransactionApiReplaceService;
import com.jane.realestate.service.TransactionApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.anyList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
public class ApartmentTradeApiTest {

    @Autowired
    private TransactionApiService transactionApiService;
    @Autowired
    private TransactionApiImportService transactionApiImportService;
    @Autowired
    private TransactionApiReplaceService transactionApiReplaceService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @MockitoSpyBean
    private TransactionBatchRepository transactionBatchRepository;


    @Test
    void 공공API_응답_확인() {
        String sggCd = "26350";
        String dealYmd = "202501";

        var result = transactionApiService
                .getTransactions(sggCd, dealYmd,3);

        String json = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(result);


        log.info("조회 결과: \n{}", json);
    }

    @Test
    void testPageSize() {
        TransactionApiResponse response =
                transactionApiService.getApiResponse(
                        "11710",
                        "202501",
                        1
                );

        int totalCount = response.getBody().getTotalCount();

        int returnedCount =
                response.getBody().getItems() == null
                        || response.getBody().getItems().getItem() == null
                        ? 0
                        : response.getBody().getItems().getItem().size();

        System.out.println("전체 거래 건수: " + totalCount);
        System.out.println("요청 건수: 1000");
        System.out.println("응답 numOfRows: "
                + response.getBody().getNumOfRows());
        System.out.println("실제 반환 건수: " + returnedCount);
    }

    @Test
    void testFetchAll() {
        List<TransactionApiItem> items =
                transactionApiImportService.fetchAll(
                        "11710",
                        "202510"
                );

        System.out.println("전체 수집 건수: " + items.size());
    }

    @Test
    void testConvertApiData() {
        List<TransactionApiItem> items =
                transactionApiImportService.fetchAll(
                        "11710", "202510"
                );

        List<TransactionSaveData> saveData =
                transactionApiImportService.convert(items);

        System.out.println("API 수집 건수: " + items.size());
        System.out.println("변환 건수: " + saveData.size());

        if (!saveData.isEmpty()) {
            System.out.println(
                    "첫 번째 거래 주소: "
                            + saveData.get(0).fullAddress()
            );
        }
    }

    @Test
    @Transactional
    void testCollectRecentMonths() {
        transactionApiImportService.collectRecentMonths("11710");
    }

    @Test
    void testCollectAllRegions() {
        transactionApiImportService.collectAllRegions();
    }

    @Test
    @Transactional
    void testReplaceOneMonth() {
        String sggCd = "11710";
        YearMonth month = YearMonth.of(2026, 10);

        List<TransactionApiItem> items =
                transactionApiImportService.fetchAll(sggCd, "202610");

        List<TransactionSaveData> saveData =
                transactionApiImportService.convert(items);

        transactionApiReplaceService.replace(
                sggCd, month, saveData
        );

        // 교체 직후 DB 거래 건수 확인
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM transactions
                WHERE sgg_cd = ?
                  AND deal_date >= ?
                  AND deal_date < ?
                """,
                Integer.class,
                sggCd,
                Date.valueOf(month.atDay(1)),
                Date.valueOf(month.plusMonths(1).atDay(1))
        );


        log.info("교체 직후 DB 거래 건수: {}건", count);
        assertEquals(saveData.size(), count);
    }

    @Test
    void replaceRollbackTest() {
        String sggCd = "11710";
        YearMonth month = YearMonth.of(2026, 10);

        // 기존 DB 거래 건수
        Integer beforeCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM transactions
                WHERE sgg_cd = ?
                  AND deal_date >= ?
                  AND deal_date < ?
                """,
                Integer.class,
                sggCd,
                Date.valueOf(month.atDay(1)),
                Date.valueOf(month.plusMonths(1).atDay(1))
        );

        List<TransactionApiItem> items =
                transactionApiImportService.fetchAll(sggCd, "202610");

        List<TransactionSaveData> saveData =
                transactionApiImportService.convert(items);

        // INSERT 단계에서 강제로 예외 발생
        doThrow(new RuntimeException("롤백 테스트"))
                .when(transactionBatchRepository)
                .batchInsert(anyList());

        // 교체 실행 시 예외가 발생하는지 확인
        assertThrows(RuntimeException.class, () ->
                transactionApiReplaceService.replace(
                        sggCd,
                        month,
                        saveData
                )
        );

        // 롤백 후 DB 거래 건수
        Integer afterCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM transactions
                WHERE sgg_cd = ?
                  AND deal_date >= ?
                  AND deal_date < ?
                """,
                Integer.class,
                sggCd,
                Date.valueOf(month.atDay(1)),
                Date.valueOf(month.plusMonths(1).atDay(1))
        );

        log.info("롤백 테스트 - 이전: {}건, 이후: {}건",
                beforeCount, afterCount);

        assertEquals(beforeCount, afterCount);
    }

}