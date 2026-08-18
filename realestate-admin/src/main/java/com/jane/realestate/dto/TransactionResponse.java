package com.jane.realestate.dto;

import com.jane.realestate.entity.Transaction;
import com.jane.realestate.dto.api.TransactionApiItem;
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

    // DB 용
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
                .build();
    }

    // API 호출용
    public static TransactionResponse from(TransactionApiItem item) {
        return TransactionResponse.builder()
                .aptName(item.getAptNm())
                .dealAmount(
                        Long.parseLong(
                                item.getDealAmount()
                                        .replace(",", "")
                                        .trim()
                        )
                )
                .dealDate(
                        LocalDate.of(
                                item.getDealYear(),
                                item.getDealMonth(),
                                item.getDealDay()
                        )
                )
                .area(item.getExcluUseAr())
                .floor(item.getFloor())
                .buildYear(item.getBuildYear())
                .umdNm(item.getUmdNm())
                .build();
    }
}
