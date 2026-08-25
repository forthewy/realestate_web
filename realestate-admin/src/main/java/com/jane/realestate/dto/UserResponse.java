package com.jane.realestate.dto;

import com.jane.realestate.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String name,
        String phone,
        Role role
) {}