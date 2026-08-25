package com.jane.realestate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "apartments")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aptName;

    @Column(nullable = false, length = 5)
    private String sggCd;

    @Column(nullable = false)
    private String umdNm;

    @Column(nullable = false)
    private String jibun;

    @Column(nullable = false)
    private String address;

    private Double latitude;

    private Double longitude;

    // 위도 경도 추가
    public void updateCoordinate(
            Double latitude,
            Double longitude
    ) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}