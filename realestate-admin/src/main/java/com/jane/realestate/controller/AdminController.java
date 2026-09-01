package com.jane.realestate.controller;

import com.jane.realestate.dto.TransactionImportResponse;
import com.jane.realestate.dto.TransactionImportStatusResponse;
import com.jane.realestate.dto.UserResponse;
import com.jane.realestate.service.AdminService;
import com.jane.realestate.service.ApartmentGeocodingService;
import com.jane.realestate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ApartmentGeocodingService apartmentGeocodingService;
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {

        return ResponseEntity.ok(userService.getUsers());
    }

    // ------- Excel Import -------------
    // Excel 업로드
    @PostMapping("/import")
    public void importExcel(@RequestParam("file") MultipartFile file) {
        adminService.importExcel(file);
    }

    // 해당 월 임포트 이력 조회
    @GetMapping("/import")
    public ResponseEntity<List<TransactionImportResponse>> getImports(
            @RequestParam String yearMonth
    ) {
        return ResponseEntity.ok(
                adminService.getImports(yearMonth)
        );
    }


    @GetMapping("/import/status")
    public ResponseEntity<TransactionImportStatusResponse> getImportStatus(
            @RequestParam String yearMonth
    ) {
        return ResponseEntity.ok(
                adminService.getImportStatus(yearMonth)
        );
    }

    // DB 조회 승인
    @PostMapping("/import/approve")
    public void approveStoredMonth(
            @RequestParam String yearMonth
    ) {
        adminService.approveStoredMonth(yearMonth);
    }

    // DB 조회 승인 취소
    @DeleteMapping("/import/approve")
    public void cancelStoredMonth(@RequestParam String yearMonth) {
        adminService.cancelStoredMonth(yearMonth);
    }

    //------ 위도 경도 ------
    // 위도 경도 조회
    @PostMapping("/apartments/geocode")
    public ResponseEntity<Void> geocodeApartments() {

        apartmentGeocodingService.geocodeApartments();

        return ResponseEntity.noContent().build();
    }

    // 위도 경도 미입력된 아파트 조회
    @GetMapping("/apartments/geocode/status")
    public long getGeocodeStatus() {
        return apartmentGeocodingService.getMissingCoordinateCount();
    }
}