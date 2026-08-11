package com.jane.realestate.service;

import com.jane.realestate.dto.LoginRequest;
import com.jane.realestate.dto.LoginResponse;
import com.jane.realestate.dto.RegisterRequest;
import com.jane.realestate.entity.User;
import com.jane.realestate.enums.Role;
import com.jane.realestate.repository.UserRepository;
import com.jane.realestate.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return createLoginResponse(user);
    }

    public LoginResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("이미 가입된 휴대폰 번호입니다.");
        }

        User user = User.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        return createLoginResponse(user);
    }


    private LoginResponse createLoginResponse(User user) {
        String accessToken =
                jwtProvider.createAccessToken(
                        user.getUsername(),
                        user.getRole().name());

        String refreshToken =
                jwtProvider.createRefreshToken(
                        user.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public boolean checkUsername(String username) {
        return !userRepository.existsByUsername(username);
    }

    public boolean checkPhone(String phone) {
        return !userRepository.existsByPhone(phone);
    }
}