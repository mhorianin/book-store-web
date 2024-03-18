package com.example.bookstoreweb.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotEmpty
        @Size(min = 6, max = 20)
        @Email
        String email,
        @NotEmpty
        @Size(min = 6, max = 20)
        String password
) {
}