package com.jane.realestate.service;


import com.jane.realestate.dto.UserResponse;
import com.jane.realestate.entity.Region;
import com.jane.realestate.entity.Transaction;
import com.jane.realestate.repository.RegionRepository;
import com.jane.realestate.repository.TransactionRepository;
import com.jane.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
//import org.apache.poi.ss.usermodel.DataFormatter;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    public List<UserResponse> getUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .username(user.getUsername())
                        .phone(user.getPhone())
                        .role(user.getRole())
                        .createdAt(user.getCreatedAt())
                        .build())
                .toList();
    }


    //엑셀 업로드
    public void importExcel(MultipartFile file) {

        int skippedCount = 0;
        DataFormatter formatter = new DataFormatter();

        Map<String, String> regionMap = regionRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Region::getName,
                        Region::getCode
                ));

        List<Transaction> transactions = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 14; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String address = formatter.formatCellValue(row.getCell(1)).trim();

                int lastSpace = address.lastIndexOf(" ");

                String regionName = address.substring(0, lastSpace);
                String umdNm = address.substring(lastSpace + 1);

                String sggCd = regionMap.get(regionName);

                if (sggCd == null) {
                    skippedCount++;
                    continue;
                }

                String yearMonth =
                        formatter.formatCellValue(row.getCell(7)).trim();

                int day = Integer.parseInt(
                        formatter.formatCellValue(row.getCell(8)).trim()
                );

                LocalDate dealDate = LocalDate.of(
                        Integer.parseInt(yearMonth.substring(0, 4)),
                        Integer.parseInt(yearMonth.substring(4, 6)),
                        day
                );

                Transaction transaction = Transaction.builder()
                        .aptName(formatter.formatCellValue(row.getCell(5)).trim())
                        .area(Double.parseDouble(
                                formatter.formatCellValue(row.getCell(6)).trim()
                        ))
                        .dealAmount(Long.parseLong(
                                formatter.formatCellValue(row.getCell(9))
                                        .replace(",", "")
                                        .trim()
                        ))
                        .dong(formatter.formatCellValue(row.getCell(10)).trim())
                        .floor(Integer.parseInt(
                                formatter.formatCellValue(row.getCell(11)).trim()
                        ))
                        .buildYear(Integer.parseInt(
                                formatter.formatCellValue(row.getCell(14)).trim()
                        ))
                        .dealDate(dealDate)
                        .umdNm(umdNm)
                        .sggCd(sggCd)
                        .build();

                transactions.add(transaction);
            }

            transactionRepository.saveAll(transactions);

            System.out.println("저장: " + transactions.size() + "건");
            System.out.println("지역코드 미지원으로 제외: " + skippedCount + "건");

        } catch (IOException e) {
            throw new RuntimeException("Excel 파일을 읽을 수 없습니다.", e);
        }
    }
}