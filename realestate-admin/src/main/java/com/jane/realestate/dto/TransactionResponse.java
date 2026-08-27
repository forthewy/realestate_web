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

    // DB 용
    public static TransactionResponse from(Transaction transaction) {
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
                .build();
    }

    // API 호출용
    public static TransactionResponse from(
            TransactionApiItem item
    ) {
        return TransactionResponse.builder()
                .aptName(item.getAptName())
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
                .area(item.getArea())
                .floor(item.getFloor())
                .buildYear(item.getBuildYear())
                .dong(item.getDong())
                .umdNm(item.getUmdNm())
                .sggCd(item.getSggCd())
                .build();
    }
}
