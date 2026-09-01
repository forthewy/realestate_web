package com.jane.realestate.service;

import com.jane.realestate.dto.UserResponse;
import com.jane.realestate.dto.UserUpdateRequest;
import com.jane.realestate.entity.User;
import com.jane.realestate.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserResponse getMyInfo(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getPhone(),
                user.getRole()
        );
    }

    @Transactional
    public void updateUser(
            String username,
            UserUpdateRequest request
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!user.getPhone().equals(request.phone())
                && userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("이미 가입된 휴대폰 번호입니다.");
        }

        user.updatePhone(request.phone());

        if (request.password() != null
                && !request.password().isBlank()) {

            user.updatePassword(
                    passwordEncoder.encode(request.password())
            );
        }
    }

    // 전체 회원 조회
    public List<UserResponse> getUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getPhone(),
                        user.getRole()
                ))
                .toList();
    }
}