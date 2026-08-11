package com.jane.realestate.controller;

import com.jane.realestate.dto.LoginRequest;
import com.jane.realestate.dto.LoginResponse;
import com.jane.realestate.dto.RegisterRequest;
import com.jane.realestate.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        System.out.println(request.getUsername());

        return ResponseEntity.ok(authService.login(request));
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        System.out.println(request.getUsername());

        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(
                authService.checkUsername(username)
        );
    }

    @GetMapping("/check-phone")
    public ResponseEntity<Boolean> checkPhone(
            @RequestParam String phone
    ) {
        return ResponseEntity.ok(
                authService.checkPhone(phone)
        );
    }
}
