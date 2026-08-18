package com.jane.realestate.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TransactionApiItem {

    private String aptNm;
    private String dealAmount;

    private Integer dealYear;
    private Integer dealMonth;
    private Integer dealDay;

    private Double excluUseAr;
    private Integer floor;
    private Integer buildYear;

    private String umdNm;
    private String roadNm;
}