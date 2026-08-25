package com.jane.realestate.service;

import tools.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class KakaoGeocodingService {

    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    private final RestClient restClient = RestClient.create();

    public Coordinate getCoordinate(String address) {

        JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("dapi.kakao.com")
                        .path("/v2/local/search/address.json")
                        .queryParam("query", address)
                        .build()
                )
                .header(
                        "Authorization",
                        "KakaoAK " + restApiKey
                )
                .retrieve()
                .body(JsonNode.class);

        if (response == null
                || !response.has("documents")
                || response.get("documents").isEmpty()) {
            return null;
        }

        JsonNode document = response.get("documents").get(0);

        double longitude =
                Double.parseDouble(document.get("x").asText());

        double latitude =
                Double.parseDouble(document.get("y").asText());

        return new Coordinate(latitude, longitude);
    }

    public record Coordinate(
            double latitude,
            double longitude
    ) {
    }
}