package com.jane.realestate.service;


import com.jane.realestate.dto.TransactionImportResponse;
import com.jane.realestate.dto.TransactionImportStatusResponse;
import com.jane.realestate.dto.UserResponse;
import com.jane.realestate.entity.*;
import com.jane.realestate.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final TransactionRepository transactionRepository;
    private final ApartmentRepository apartmentRepository;
    private final KakaoGeocodingService kakaoGeocodingService;
    private final TransactionImportRepository transactionImportRepository;
    private final TransactionStoredMonthRepository transactionStoredMonthRepository;


    public List<UserResponse> getUsers() {

        return (List<UserResponse>) userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getPhone(),
                        user.getRole()
                ))
                .toList();
    }


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

        // 기존 아파트 메모리에 한 번 로드
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

                if (sggCd == null) {
                    log.warn("지역코드 조회 실패 - {}", address);
                    skippedCount++;
                    continue;
                }

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
            transactionRepository.saveAll(transactions);

            // 신규 아파트 일괄 저장
            apartmentRepository.saveAll(newApartments);

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
    private Apartment findOrCreateApartment(
            Map<String, Apartment> apartmentMap,
            List<Apartment> newApartments,
            String aptName,
            String sggCd,
            String umdNm,
            String jibun,
            String fullAddress
    ) {

        String apartmentKey =
                createApartmentKey(
                        sggCd,
                        umdNm,
                        jibun
                );

        Apartment apartment =
                apartmentMap.get(apartmentKey);

        // 이미 존재하는 아파트
        if (apartment != null) {
            return apartment;
        }

        // 신규 아파트
        apartment =
                createApartment(
                        aptName,
                        sggCd,
                        umdNm,
                        jibun,
                        fullAddress
                );

        // 같은 Excel 안에서 다시 생성되는 것 방지
        apartmentMap.put(
                apartmentKey,
                apartment
        );

        newApartments.add(apartment);

        return apartment;
    }


    // 아파트 생성
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

    public List<TransactionImportResponse> getImports(String yearMonth) {

        YearMonth month = YearMonth.parse(yearMonth);

        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();

        return transactionImportRepository
                .findImportedMonth(monthStart, monthEnd)
                .stream()
                .map(transactionImport ->
                        new TransactionImportResponse(
                                transactionImport.getId(),
                                transactionImport.getStartDate(),
                                transactionImport.getEndDate(),
                                transactionImport.getTransactionCount(),
                                transactionImport.getSkippedCount(),
                                transactionImport.getImportedAt()
                        )
                )
                .toList();
    }

    public TransactionImportStatusResponse getImportStatus(String yearMonth) {

        YearMonth month = YearMonth.parse(yearMonth);

        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();

        List<TransactionImport> imports =
                transactionImportRepository
                        .findImportedMonth(
                                monthStart,
                                monthEnd
                        );

        boolean approvable =
                isImportApprovable(month, imports);

        boolean approved =
                transactionStoredMonthRepository
                        .existsByYearMonth(yearMonth);

        return new TransactionImportStatusResponse(
                yearMonth,
                approvable,
                approved
        );
    }

    private boolean isImportApprovable(
            YearMonth month,
            List<TransactionImport> imports
    ) {
        if (imports.isEmpty()) {
            return false;
        }

        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();

        LocalDate coveredUntil = monthStart.minusDays(1);

        for (TransactionImport transactionImport : imports) {

            LocalDate startDate = transactionImport.getStartDate();
            LocalDate endDate = transactionImport.getEndDate();

            // 아직 채워진 날짜 다음보다 뒤에서 시작하면 중간에 빈 날짜가 있음
            if (startDate.isAfter(coveredUntil.plusDays(1))) {
                return false;
            }

            // 기존 범위보다 더 뒤까지 채우는 Import라면 범위 확장
            if (endDate.isAfter(coveredUntil)) {
                coveredUntil = endDate;
            }

            // 해당 월 마지막 날까지 도달
            if (!coveredUntil.isBefore(monthEnd)) {
                return true;
            }
        }

        return false;
    }

    @Transactional
    public void approveStoredMonth(String yearMonth) {

        YearMonth month = YearMonth.parse(yearMonth);

        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();

        List<TransactionImport> imports =
                transactionImportRepository.findImportedMonth(
                        monthStart,
                        monthEnd
                );

        if (!isImportApprovable(month, imports)) {
            throw new IllegalStateException(
                    "해당 월의 데이터가 모두 등록되지 않았습니다."
            );
        }

        if (transactionStoredMonthRepository.existsByYearMonth(yearMonth)) {
            return;
        }

        TransactionStoredMonth storedMonth =
                TransactionStoredMonth.builder()
                        .yearMonth(yearMonth)
                        .build();

        transactionStoredMonthRepository.save(storedMonth);
    }

    @Transactional
    public void cancelStoredMonth(String yearMonth) {
        transactionStoredMonthRepository.deleteByYearMonth(yearMonth);
    }

    @Transactional
    public int geocodeApartments() {

        List<Apartment> apartments =
                apartmentRepository.findTop100ByLatitudeIsNull();

        for (Apartment apartment : apartments) {
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

    public long getMissingCoordinateCount() {
        return apartmentRepository
                .countByLatitudeIsNull();
    }
}