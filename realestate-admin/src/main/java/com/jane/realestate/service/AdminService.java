package com.jane.realestate.service;

import com.jane.realestate.dto.UserResponse;
import com.jane.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

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

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 13; i <= 19; i++) {
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                System.out.println(
                        "시군구: " + row.getCell(1)
                                + " / 단지명: " + row.getCell(5)
                                + " / 전용면적: " + row.getCell(6)
                                + " / 계약년월: " + row.getCell(7)
                                + " / 계약일: " + row.getCell(8)
                                + " / 거래금액: " + row.getCell(9)
                );
            }

        } catch (IOException e) {
            throw new RuntimeException("Excel 파일을 읽을 수 없습니다.", e);
        }
    }
}