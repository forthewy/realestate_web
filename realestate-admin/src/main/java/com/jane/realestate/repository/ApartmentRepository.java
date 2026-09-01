package com.jane.realestate.repository;

import com.jane.realestate.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    Optional<Apartment> findBySggCdAndUmdNmAndJibun(
            String sggCd,
            String umdNm,
            String jibun
    );

    @Query("""
        select a
        from Apartment a
        where a.latitude between :minLat and :maxLat
          and a.longitude between :minLng and :maxLng
        """)
    List<Apartment> findByBounds(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng,
            @Param("maxLng") Double maxLng
    );


    // 위도 경도 없는 아파트 리스트
    List<Apartment> findTop100ByLatitudeIsNull();


    // 위도 경도 없는 아파트 갯수
    long countByLatitudeIsNull();
}