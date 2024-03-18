package com.example.bookstoreweb.controller;

import com.example.bookstoreweb.dto.user.UserLoginRequestDto;
import com.example.bookstoreweb.dto.user.UserLoginResponseDto;
import com.example.bookstoreweb.dto.user.UserRegistrationRequestDto;
import com.example.bookstoreweb.dto.user.UserResponseDto;
import com.example.bookstoreweb.exception.RegistrationException;
import com.example.bookstoreweb.security.AuthenticationService;
import com.example.bookstoreweb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RestController
@RequestMapping(value = "/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Registration a new user", description = "Registration a new user")
    public UserResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto registrationRequestDto
    ) throws RegistrationException {
        return userService.register(registrationRequestDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
