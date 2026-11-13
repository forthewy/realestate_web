package com.jane.realestate.service;

import com.jane.realestate.dto.PageResponse;
import com.jane.realestate.dto.TransactionResponse;
import com.jane.realestate.dto.api.TransactionApiItem;
import com.jane.realestate.dto.api.TransactionApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.dataformat.xml.XmlMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionApiService {

    @Value("${public-data.service-key}")
    private String serviceKey;

    private static final int PAGE_SIZE = 1000;

    public PageResponse<TransactionResponse> getTransactions(
            String sggCd,
            String dealYmd,
            int pageNo
    ) {
        TransactionApiResponse apiResponse =
                getApiResponse(sggCd, dealYmd, pageNo);

        List<TransactionApiItem> items =
                apiResponse.getBody().getItems() == null
                        ? List.of()
                        : apiResponse.getBody().getItems().getItem();

        if (items == null) {
            items = List.of();
        }

        List<TransactionResponse> transactions =
                items.stream()
                        .map(TransactionResponse::from)
                        .toList();

        return PageResponse.<TransactionResponse>builder()
                .items(transactions)
                .pageNo(apiResponse.getBody().getPageNo())
                .pageSize(apiResponse.getBody().getNumOfRows())
                .totalCount(apiResponse.getBody().getTotalCount())
                .build();
    }

    public TransactionApiResponse getApiResponse(
            String sggCd,
            String dealYmd,
            int pageNo
    ) {
        HttpURLConnection connection = null;

        try {


            String url =
                    "https://apis.data.go.kr"
                            + "/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade"
                            + "?serviceKey=" + serviceKey
                            + "&LAWD_CD=" + sggCd
                            + "&DEAL_YMD=" + dealYmd
                            + "&numOfRows=" + PAGE_SIZE
                            + "&pageNo=" + pageNo;

            connection =
                    (HttpURLConnection) URI.create(url)
                            .toURL()
                            .openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

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

            return xmlMapper.readValue(
                    response,
                    TransactionApiResponse.class
            );

        } catch (Exception e) {
            throw new RuntimeException("공공데이터 API 호출 실패", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }



}