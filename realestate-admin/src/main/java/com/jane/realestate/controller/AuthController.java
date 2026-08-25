package com.jane.realestate.controller;

import com.jane.realestate.dto.*;
import com.jane.realestate.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        return ResponseEntity.ok(authService.login(request));
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    // 아이디 중복 체크
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(
                authService.checkUsername(username)
        );
    }


    // 핸드폰 번호 중복확인
    @GetMapping("/check-phone")
    public ResponseEntity<Boolean> checkPhone(
            @RequestParam String phone
    ) {
        return ResponseEntity.ok(
                authService.checkPhone(phone)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(
                authService.refresh(request.refreshToken())
        );
    }
}
