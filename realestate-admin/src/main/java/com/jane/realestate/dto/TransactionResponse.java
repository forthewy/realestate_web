package com.jane.realestate.dto;

import com.jane.realestate.entity.Transaction;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TransactionResponse {

    private Long id;
    private String aptName;
    private Long dealAmount;
    private LocalDate dealDate;
    private Double area;
    private Integer floor;
    private Integer buildYear;
    private String dong;
    private String umdNm;
    private String sggCd;
    private String sggName;
    private String roadName;

    public static TransactionResponse from(Transaction transaction, String sggName) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .aptName(transaction.getAptName())
                .dealAmount(transaction.getDealAmount())
                .dealDate(transaction.getDealDate())
                .area(transaction.getArea())
                .floor(transaction.getFloor())
                .buildYear(transaction.getBuildYear())
                .dong(transaction.getDong())
                .umdNm(transaction.getUmdNm())
                .sggCd(transaction.getSggCd())
                .sggName(sggName)
                .roadName(transaction.getRoadName())
                .build();
    }
}
