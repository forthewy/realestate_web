package com.jane.realestate.dto;

import com.jane.realestate.enums.Role;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String username,
        Role role
) {
}