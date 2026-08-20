package com.jane.realestate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_tx_sgg", columnList = "sggCd"),
        @Index(name = "idx_tx_deal_date", columnList = "dealDate"),
        @Index(name = "idx_tx_umd", columnList = "umdNm")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aptName;

    @Column(nullable = false)
    private Long dealAmount;

    @Column(nullable = false)
    private LocalDate dealDate;

    private Double area;

    private Integer floor;

    private Integer buildYear;

    private String dong;

    private String jibun;

    @Column(nullable = false)
    private String umdNm;

    @Column(nullable = false, length = 5)
    private String sggCd;

    private String dealType;

    private String agentRegion;

    private LocalDate registrationDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
