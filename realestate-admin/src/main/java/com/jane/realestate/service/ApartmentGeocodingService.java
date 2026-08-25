package com.jane.realestate.service;

import com.jane.realestate.entity.Apartment;
import com.jane.realestate.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentGeocodingService {

    private final ApartmentRepository apartmentRepository;
    private final KakaoGeocodingService kakaoGeocodingService;

    @Transactional
    public void updateCoordinates() {

        List<Apartment> apartments =
                apartmentRepository.findAll();

        int successCount = 0;
        int failCount = 0;

        for (Apartment apartment : apartments) {

            // 이미 좌표가 있으면 다시 호출하지 않음
            if (apartment.getLatitude() != null
                    && apartment.getLongitude() != null) {
                continue;
            }

            KakaoGeocodingService.Coordinate coordinate =
                    kakaoGeocodingService.getCoordinate(
                            apartment.getAddress()
                    );

            if (coordinate == null) {
                System.out.println(
                        "좌표 변환 실패: "
                                + apartment.getAddress()
                );

                failCount++;
                continue;
            }

            apartment.updateCoordinate(
                    coordinate.latitude(),
                    coordinate.longitude()
            );

            successCount++;
        }

        System.out.println(
                "좌표 저장 성공: " + successCount + "건"
        );

        System.out.println(
                "좌표 변환 실패: " + failCount + "건"
        );
    }
}