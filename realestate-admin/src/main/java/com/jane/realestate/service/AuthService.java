package com.jane.realestate.service;

import com.jane.realestate.dto.LoginRequest;
import com.jane.realestate.dto.LoginResponse;
import com.jane.realestate.entity.User;
import com.jane.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return LoginResponse.builder()
                .accessToken("temp-token")
                .refreshToken("temp-refresh")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}