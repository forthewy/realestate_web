package com.jane.realestate.service;

import com.jane.realestate.dto.TransactionSaveData;
import com.jane.realestate.entity.Apartment;
import com.jane.realestate.entity.Transaction;
import com.jane.realestate.repository.ApartmentRepository;
import com.jane.realestate.repository.TransactionBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionSaveService {

    private final TransactionBatchRepository transactionBatchRepository;
    private final ApartmentRepository apartmentRepository;

    // 저장
    @Transactional
    public void save(
            List<TransactionSaveData> saveDataList
    ) {

        // Import 중 반복적인 DB 조회를 방지하기 위해 기존 아파트를 Map으로 로드
        Map<String, Apartment> apartmentMap =
                apartmentRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                apartment ->
                                        createApartmentKey(
                                                apartment.getSggCd(),
                                                apartment.getUmdNm(),
                                                apartment.getJibun()
                                        ),
                                apartment -> apartment,
                                (existing, duplicate) -> existing
                        ));

        List<Apartment> newApartments = new ArrayList<>();
        List<Transaction> transactions = new ArrayList<>();

        // 저장할 데이터 가공
        for (TransactionSaveData data : saveDataList) {

            Transaction transaction = data.transaction();

            // 신규 아파트 확인 및 생성
            findOrCreateApartment(
                    apartmentMap,
                    newApartments,
                    transaction.getAptName(),
                    transaction.getSggCd(),
                    transaction.getUmdNm(),
                    transaction.getJibun(),
                    data.fullAddress()
            );

            transactions.add(transaction);
        }



        // 거래 일괄 저장
        long transactionSaveStart = System.nanoTime();

        transactionBatchRepository.batchInsert(transactions);

        long transactionSaveEnd = System.nanoTime();


        // 신규 아파트 일괄 저장
        long apartmentSaveStart = System.nanoTime();

        apartmentRepository.saveAll(newApartments);

        long apartmentSaveEnd = System.nanoTime();

        log.info(
                "저장 시간 - transactions: {}건 / {} ms, apartments: {}건 / {} ms",
                transactions.size(),
                (transactionSaveEnd - transactionSaveStart) / 1_000_000.0,
                newApartments.size(),
                (apartmentSaveEnd - apartmentSaveStart) / 1_000_000.0
        );
    }


    // 아파트 중복 판별 Key
    private String createApartmentKey(
            String sggCd,
            String umdNm,
            String jibun
    ) {

        return sggCd
                + "|"
                + umdNm
                + "|"
                + jibun;
    }

    // 아파트 조회 / 신규 생성
    private void findOrCreateApartment(
            Map<String, Apartment> apartmentMap,
            List<Apartment> newApartments,
            String aptName,
            String sggCd,
            String umdNm,
            String jibun,
            String fullAddress
    ) {
        String apartmentKey =
                createApartmentKey(sggCd, umdNm, jibun);

        if (apartmentMap.containsKey(apartmentKey)) {
            return;
        }

        Apartment apartment =
                createApartment(
                        aptName,
                        sggCd,
                        umdNm,
                        jibun,
                        fullAddress
                );

        apartmentMap.put(apartmentKey, apartment);
        newApartments.add(apartment);
    }

    // 신규 아파트 생성
    private Apartment createApartment(
            String aptName,
            String sggCd,
            String umdNm,
            String jibun,
            String fullAddress
    ) {

        return Apartment.builder()
                .aptName(aptName)
                .sggCd(sggCd)
                .umdNm(umdNm)
                .jibun(jibun)
                .address(fullAddress)
                .build();
    }
}

