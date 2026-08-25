package com.jane.realestate.controller;

import com.jane.realestate.dto.UserResponse;
import com.jane.realestate.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.jane.realestate.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(
            Authentication authentication) {

        return ResponseEntity.ok(
                userService.getMyInfo(authentication.getName())
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateMe(
            Authentication authentication,
            @RequestBody UserUpdateRequest request
    ) {
        userService.updateUser(
                authentication.getName(),
                request
        );

        return ResponseEntity.noContent().build();
    }
}