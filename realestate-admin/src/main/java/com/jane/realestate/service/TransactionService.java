package com.jane.realestate.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jane.realestate.dto.TransactionResponse;
import com.jane.realestate.dto.api.TransactionApiItem;
import com.jane.realestate.dto.api.TransactionApiResponse;
import com.jane.realestate.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Value("${public-data.service-key}")
    private String serviceKey;



    // API 호출
    // 공공데이터에서 거래 가져오기
//    public String getTransactionsFromApi(
//            String sggCd,
//            String dealYmd)
//    {
//        RestClient restClient = RestClient.create();

//      String response = restClient.get()
//                    .uri(uriBuilder -> uriBuilder
//                            .scheme("http")
//                            .host("apis.data.go.kr")
//                            .path("/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade")
//                            .queryParam("serviceKey", serviceKey)
//                            .queryParam("LAWD_CD", sggCd)
//                            .queryParam("DEAL_YMD", dealYmd)
//                            .queryParam("pageNo", 1)
//                            .queryParam("numOfRows", 100)
//                            .build())
//                    .retrieve()
//                    .body(String.class);
//        return response;
//    }
//
    public List<TransactionApiItem> getTransactionsFromApi(
            String sggCd,
            String dealYmd,
            Long minAmount,
            Long maxAmount
    ) {
        try {
            String url =
                    "https://apis.data.go.kr"
                            + "/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade"
                            + "?serviceKey=" + serviceKey
                            + "&LAWD_CD=" + sggCd
                            + "&DEAL_YMD=" + dealYmd;

            HttpURLConnection connection =
                    (HttpURLConnection) URI.create(url)
                            .toURL()
                            .openConnection();

            connection.setRequestMethod("GET");

            String response;

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            )) {
                response = reader.lines()
                        .collect(Collectors.joining("\n"));
            }
            XmlMapper xmlMapper = new XmlMapper();

            TransactionApiResponse apiResponse =
                    xmlMapper.readValue(
                            response,
                            TransactionApiResponse.class
                    );

            return apiResponse
                    .getBody()
                    .getItems()
                    .getItem();

        } catch (Exception e) {
            throw new RuntimeException("공공데이터 API 호출 실패", e);
        }
    }

    // 기존 DB 데이터 호출
    public List<TransactionResponse> getTransactions(
            String sggCd,
            String umdNm,
            Long minAmount,
            Long maxAmount,
            LocalDate fromDate,
            LocalDate toDate) {
        return List.of();
    }
}
