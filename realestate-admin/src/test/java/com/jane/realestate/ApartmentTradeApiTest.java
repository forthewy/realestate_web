package com.jane.realestate;

import com.jane.realestate.service.TransactionApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@Slf4j
public class ApartmentTradeApiTest {

    @Autowired
    private TransactionApiService transactionApiService;
    @Autowired
    private ObjectMapper objectMapper;

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
}