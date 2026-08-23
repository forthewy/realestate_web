package com.jane.realestate.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank String phone,
        @NotBlank String username,
        @NotBlank String password
) {}