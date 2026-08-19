package com.jane.realestate.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionApiItem {

    @JacksonXmlProperty(localName = "aptNm")
    private String aptName;

    @JacksonXmlProperty(localName = "dealAmount")
    private String dealAmount;

    @JacksonXmlProperty(localName = "dealYear")
    private Integer dealYear;

    @JacksonXmlProperty(localName = "dealMonth")
    private Integer dealMonth;

    @JacksonXmlProperty(localName = "dealDay")
    private Integer dealDay;

    @JacksonXmlProperty(localName = "excluUseAr")
    private Double area;

    @JacksonXmlProperty(localName = "floor")
    private Integer floor;

    @JacksonXmlProperty(localName = "buildYear")
    private Integer buildYear;

    @JacksonXmlProperty(localName = "aptDong")
    private String dong;

    @JacksonXmlProperty(localName = "umdNm")
    private String umdNm;

    @JacksonXmlProperty(localName = "sggCd")
    private String sggCd;
}