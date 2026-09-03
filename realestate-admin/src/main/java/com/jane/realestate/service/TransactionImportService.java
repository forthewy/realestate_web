package com.jane.realestate.service;


import com.jane.realestate.entity.Apartment;
import com.jane.realestate.entity.Region;
import com.jane.realestate.entity.Transaction;
import com.jane.realestate.entity.TransactionImport;
import com.jane.realestate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionImportService {

    private final RegionRepository regionRepository;
    private final TransactionBatchRepository transactionBatchRepository;
    private final ApartmentRepository apartmentRepository;
    private final TransactionImportRepository transactionImportRepository;


    // ---------- 실거래 데이터 Import ----------
    // 엑셀 업로드
    public void importExcel(MultipartFile file) {
        long startTime = System.nanoTime();

        int skippedCount = 0;
        int totalRows = 0;

        DataFormatter formatter = new DataFormatter();

        // 지역코드 메모리에 한 번 로드
        Map<String, String> regionMap = regionRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Region::getName,
                        Region::getCode
                ));

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

        List<Transaction> transactions = new ArrayList<>();
        List<Apartment> newApartments = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 실제 데이터 행 수
            totalRows = Math.max(0, sheet.getLastRowNum() - 13);

            // 조회 기간
            String searchPeriod =
                    formatter.formatCellValue(
                            sheet.getRow(8).getCell(0)
                    ).trim();

            String period = searchPeriod
                    .replace("계약일자", "")
                    .replace(":", "")
                    .trim();

            String[] dates = period.split("~");

            LocalDate startDate =
                    LocalDate.parse(dates[0].trim());

            LocalDate endDate =
                    LocalDate.parse(dates[1].trim());


            // 기존 Import 기간과 중복 검사
            boolean duplicated =
                    transactionImportRepository
                            .existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                    endDate,
                                    startDate
                            );

            if (duplicated) {
                throw new IllegalStateException(
                        "이미 등록된 기간과 겹칩니다."
                );
            }

            //---- 데이터 입력 시작 -----
            // 14번째 행부터 실제 데이터
            for (int i = 14; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                // 예: 서울특별시 동대문구 전농동
                String address =
                        formatter.formatCellValue(
                                row.getCell(1)
                        ).trim();

                String[] addressParts = address.split(" ");

                String umdNm = addressParts[addressParts.length - 1];

                String regionName = address;

                while (!regionMap.containsKey(regionName)) {

                    int lastSpace = regionName.lastIndexOf(" ");

                    if (lastSpace == -1) {
                        break;
                    }

                    regionName = regionName.substring(0, lastSpace);
                }

                String sggCd = regionMap.get(regionName);


                // 지원하지 않는 지역코드
                if (sggCd == null) {
                    log.warn(
                            "지원하지 않는 지역 - regionName: {}, address: {}",
                            regionName,
                            address
                    );
                    skippedCount++;
                    continue;
                }

                String jibun =
                        formatter.formatCellValue(
                                row.getCell(2)
                        ).trim();

                String aptName =
                        formatter.formatCellValue(
                                row.getCell(5)
                        ).trim();

                /*
                 * 아파트 저장
                 *
                 * address:
                 * 서울특별시 동대문구 전농동
                 *
                 * fullAddress:
                 * 서울특별시 동대문구 전농동 10
                 */
                String fullAddress =
                        address + " " + jibun;

                findOrCreateApartment(
                        apartmentMap,
                        newApartments,
                        aptName,
                        sggCd,
                        umdNm,
                        jibun,
                        fullAddress
                );

                // 거래 년월
                String yearMonth =
                        formatter.formatCellValue(
                                row.getCell(7)
                        ).trim();

                int day =
                        Integer.parseInt(
                                formatter.formatCellValue(
                                        row.getCell(8)
                                ).trim()
                        );

                LocalDate dealDate =
                        LocalDate.of(
                                Integer.parseInt(
                                        yearMonth.substring(0, 4)
                                ),
                                Integer.parseInt(
                                        yearMonth.substring(4, 6)
                                ),
                                day
                        );

                Transaction transaction =
                        Transaction.builder()
                                .aptName(aptName)
                                .area(
                                        Double.parseDouble(
                                                formatter
                                                        .formatCellValue(
                                                                row.getCell(6)
                                                        )
                                                        .trim()
                                        )
                                )
                                .dealAmount(
                                        Long.parseLong(
                                                formatter
                                                        .formatCellValue(
                                                                row.getCell(9)
                                                        )
                                                        .replace(",", "")
                                                        .trim()
                                        )
                                )
                                .dong(
                                        formatter
                                                .formatCellValue(
                                                        row.getCell(10)
                                                )
                                                .trim()
                                )
                                .floor(
                                        Integer.parseInt(
                                                formatter
                                                        .formatCellValue(
                                                                row.getCell(11)
                                                        )
                                                        .trim()
                                        )
                                )
                                .buildYear(
                                        Integer.parseInt(
                                                formatter
                                                        .formatCellValue(
                                                                row.getCell(14)
                                                        )
                                                        .trim()
                                        )
                                )
                                .dealDate(dealDate)
                                .umdNm(umdNm)
                                .sggCd(sggCd)
                                .jibun(jibun)
                                .dealType(
                                        formatter
                                                .formatCellValue(
                                                        row.getCell(17)
                                                )
                                                .trim()
                                )
                                .build();

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
                    "Save performance - transactions: {}건 / {} ms, apartments: {}건 / {} ms",
                    transactions.size(),
                    (transactionSaveEnd - transactionSaveStart) / 1_000_000.0,
                    newApartments.size(),
                    (apartmentSaveEnd - apartmentSaveStart) / 1_000_000.0
            );

            // Import 이력 저장
            TransactionImport transactionImport =
                    TransactionImport.builder()
                            .startDate(startDate)
                            .endDate(endDate)
                            .transactionCount(transactions.size())
                            .skippedCount(skippedCount)
                            .build();

            transactionImportRepository.save(transactionImport);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Excel 파일을 읽을 수 없습니다.",
                    e
            );

        } finally {

            long elapsedNanos =
                    System.nanoTime() - startTime;

            double elapsedSeconds =
                    elapsedNanos / 1_000_000_000.0;

            log.info(
                    """
                    Excel import completed
                    - file: {}
                    - rows: {}
                    - inserted transactions: {}
                    - inserted apartments: {}
                    - skipped: {}
                    - elapsed: {} sec
                    """,
                    file.getOriginalFilename(),
                    totalRows,
                    transactions.size(),
                    newApartments.size(),
                    skippedCount,
                    String.format("%.2f", elapsedSeconds)
            );
        }
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

}