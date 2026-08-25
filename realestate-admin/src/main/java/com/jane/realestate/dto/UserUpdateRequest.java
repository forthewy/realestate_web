package com.jane.realestate.dto;

public record UserUpdateRequest(
        String phone,
        String password
) {}