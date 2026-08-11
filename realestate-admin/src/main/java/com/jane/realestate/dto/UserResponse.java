package com.jane.realestate.dto;

import com.jane.realestate.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String username;
    private String phone;
    private Role role;
    private LocalDateTime createdAt;
}
