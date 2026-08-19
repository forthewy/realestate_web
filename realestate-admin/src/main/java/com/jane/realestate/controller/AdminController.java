package com.jane.realestate.controller;

import com.jane.realestate.dto.UserResponse;
import com.jane.realestate.service.AdminService;
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

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {

        return ResponseEntity.ok(adminService.getUsers());
    }

    @PostMapping("/import")
    public void importExcel(@RequestParam("file") MultipartFile file) {
        adminService.importExcel(file);
    }
}