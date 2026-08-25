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

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() ->
                        new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return createLoginResponse(user);
    }

    public LoginResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("이미 가입된 휴대폰 번호입니다.");
        }

        User user = User.builder()
                .name(request.name())
                .phone(request.phone())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
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
}