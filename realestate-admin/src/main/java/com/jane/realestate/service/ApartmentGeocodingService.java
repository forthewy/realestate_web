package com.jane.realestate.service;

import com.jane.realestate.entity.Apartment;
import com.jane.realestate.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApartmentGeocodingService {

    private final ApartmentRepository apartmentRepository;
    private final KakaoGeocodingService kakaoGeocodingService;

    // 아파트 위도 경도 채우기
    @Transactional
    public int geocodeApartments() {

        List<Apartment> apartments =
                apartmentRepository.findTop100ByLatitudeIsNull();

        for (Apartment apartment : apartments) {
            // 카카오 api 조회
            KakaoGeocodingService.Coordinate coordinate =
                    kakaoGeocodingService.getCoordinate(
                            apartment.getAddress()
                    );

            if (coordinate == null) {
                continue;
            }

            apartment.updateCoordinate(
                    coordinate.latitude(),
                    coordinate.longitude()
            );
        }

        return apartments.size();
    }

    // 위도 경도 없는 아파트 갯수 조회
    public long getMissingCoordinateCount() {
        return apartmentRepository
                .countByLatitudeIsNull();
    }
}