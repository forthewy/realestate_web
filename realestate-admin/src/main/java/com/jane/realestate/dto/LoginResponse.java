package com.jane.realestate.dto;

import com.jane.realestate.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String username;
    private final Role role;

}
