package com.jane.realestate.service;

import com.jane.realestate.dto.AccessTokenResponse;
import com.jane.realestate.dto.LoginRequest;
import com.jane.realestate.dto.LoginResponse;
import com.jane.realestate.dto.RegisterRequest;
import com.jane.realestate.entity.User;
import com.jane.realestate.enums.Role;
import com.jane.realestate.exception.LoginFailedException;
import com.jane.realestate.exception.TokenException;
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

    // 로그인
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() ->
                        new LoginFailedException(
                                "아이디 또는 비밀번호가 올바르지 않습니다."
                        ));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new LoginFailedException(
                    "아이디 또는 비밀번호가 올바르지 않습니다."
            );
        }

        return createLoginResponse(user);
    }

    // 회원가입
    public LoginResponse register(RegisterRequest request) {

        // 핸드폰 번호 하이픈 제거
        String phone = request.phone().replace("-", "");

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("이미 가입된 휴대폰 번호입니다.");
        }

        User user = User.builder()
                .name(request.name())
                .phone(phone)
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        return createLoginResponse(user);
    }


    // 공통 코드 . 회원가입 후 자동 로그인.
    private LoginResponse createLoginResponse(User user) {
        String accessToken =
                jwtProvider.createAccessToken(
                        user.getUsername(),
                        user.getRole().name());

        String refreshToken =
                jwtProvider.createRefreshToken(
                        user.getUsername());

        return new LoginResponse(
                accessToken,
                refreshToken,
                user.getUsername(),
                user.getRole()
        );
    }
    // 아이디 중복 확인
    public boolean checkUsername(String username) {
        return !userRepository.existsByUsername(username);
    }

    // 핸드폰 번호 중복 확인
    public boolean checkPhone(String phone) {
        return !userRepository.existsByPhone(phone);
    }

    public AccessTokenResponse refresh(String refreshToken) {

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new TokenException("유효하지 않거나 만료된 Refresh Token입니다.");
        }

        if (!"refresh".equals(jwtProvider.getTokenType(refreshToken))) {
            throw new TokenException("Refresh Token이 아닙니다.");
        }

        String username = jwtProvider.getUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtProvider.createAccessToken(
                user.getUsername(),
                user.getRole().name()
        );

        return new AccessTokenResponse(newAccessToken);
    }
}