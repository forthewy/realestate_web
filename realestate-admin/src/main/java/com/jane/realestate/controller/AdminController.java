package com.jane.realestate.controller;

import com.jane.realestate.dto.*;
import com.jane.realestate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final TransactionImportService transactionImportService;
    private final ApartmentGeocodingService apartmentGeocodingService;
    private final AdminService adminService;
    private final UserService userService;
    private final ApiImportHistoryService apiImportHistoryService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {

        return ResponseEntity.ok(userService.getUsers());
    }

    // ------- Excel Import -------------
    // Excel 업로드
    @PostMapping("/import")
    public void importExcel(@RequestParam("file") MultipartFile file) {
        transactionImportService.importExcel(file);
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

    @GetMapping("/api-import/history")
    public List<ApiImportRunResponse> getApiImportRunResponseListTop10() {
        return apiImportHistoryService.getRecentRuns();
    }

    @GetMapping("/api-import/history/{runId}/failures")
    public List<ApiImportFailureResponse> getApiImportFailureResponse(@PathVariable Long runId) {
        return apiImportHistoryService.getFailuresByRunId(runId);
    }

    @GetMapping("/api-import/failures/unresolved/count")
    public long getUnresolvedFailureCount() {
        return apiImportHistoryService.getUnresolvedFailureCount();
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