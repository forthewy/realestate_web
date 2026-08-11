package com.jane.realestate.service;

import com.jane.realestate.dto.UserResponse;
import com.jane.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserResponse> getUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .username(user.getUsername())
                        .phone(user.getPhone())
                        .role(user.getRole())
                        .createdAt(user.getCreatedAt())
                        .build())
                .toList();
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