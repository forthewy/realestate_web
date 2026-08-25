package com.jane.realestate.service;


import com.jane.realestate.dto.UserResponse;
import com.jane.realestate.entity.Apartment;
import com.jane.realestate.entity.Region;
import com.jane.realestate.entity.Transaction;
import com.jane.realestate.repository.ApartmentRepository;
import com.jane.realestate.repository.RegionRepository;
import com.jane.realestate.repository.TransactionRepository;
import com.jane.realestate.repository.UserRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final TransactionRepository transactionRepository;
    private final ApartmentRepository apartmentRepository;
    private final KakaoGeocodingService kakaoGeocodingService;

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

        int skippedCount = 0;
        DataFormatter formatter = new DataFormatter();

        Map<String, String> regionMap = regionRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Region::getName,
                        Region::getCode
                ));

        Map<String, Apartment> apartmentMap = apartmentRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        apartment ->
                                apartment.getSggCd()
                                        + "|"
                                        + apartment.getUmdNm()
                                        + "|"
                                        + apartment.getJibun(),
                        apartment -> apartment
                ));

        List<Transaction> transactions = new ArrayList<>();
        List<Apartment> newApartments = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

//            for (int i = 14; i <= sheet.getLastRowNum(); i++) {
            for (int i = 14; i <= 24; i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                // 서울특별시 동대문구 전농동
                String address =
                        formatter.formatCellValue(row.getCell(1)).trim();

                int lastSpace = address.lastIndexOf(" ");

                if (lastSpace == -1) {
                    skippedCount++;
                    continue;
                }

                String regionName =
                        address.substring(0, lastSpace);

                String umdNm =
                        address.substring(lastSpace + 1);

                String sggCd =
                        regionMap.get(regionName);

                if (sggCd == null) {
                    skippedCount++;
                    continue;
                }

                // 아파트 관련 값
                String jibun =
                        formatter.formatCellValue(row.getCell(2)).trim();

                String aptName =
                        formatter.formatCellValue(row.getCell(5)).trim();

                String fullAddress =
                        address + " " + jibun;

                /*
                 * 아파트 식별 키
                 * 예:
                 * 11230|전농동|620-56
                 */
                String apartmentKey =
                        sggCd
                                + "|"
                                + umdNm
                                + "|"
                                + jibun;

                Apartment apartment =
                        apartmentMap.get(apartmentKey);

                if (apartment == null) {

                    KakaoGeocodingService.Coordinate coordinate =
                            kakaoGeocodingService.getCoordinate(fullAddress);

                    apartment = Apartment.builder()
                            .aptName(aptName)
                            .sggCd(sggCd)
                            .umdNm(umdNm)
                            .jibun(jibun)
                            .address(fullAddress)
                            .latitude(
                                    coordinate != null
                                            ? coordinate.latitude()
                                            : null
                            )
                            .longitude(
                                    coordinate != null
                                            ? coordinate.longitude()
                                            : null
                            )
                            .build();

                    apartmentMap.put(apartmentKey, apartment);
                    newApartments.add(apartment);
                }

                // 거래 날짜
                String yearMonth =
                        formatter.formatCellValue(row.getCell(7)).trim();

                int day = Integer.parseInt(
                        formatter.formatCellValue(
                                row.getCell(8)
                        ).trim()
                );

                LocalDate dealDate = LocalDate.of(
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

            apartmentRepository.saveAll(
                    newApartments
            );

            transactionRepository.saveAll(
                    transactions
            );

            System.out.println(
                    "신규 아파트 저장: "
                            + newApartments.size()
                            + "건"
            );

            System.out.println(
                    "거래 저장: "
                            + transactions.size()
                            + "건"
            );

            System.out.println(
                    "지역코드 미지원으로 제외: "
                            + skippedCount
                            + "건"
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Excel 파일을 읽을 수 없습니다.",
                    e
            );
        }
    }
}