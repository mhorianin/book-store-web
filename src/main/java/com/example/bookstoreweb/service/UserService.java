package com.example.bookstoreweb.service;

import com.example.bookstoreweb.dto.user.UserRegistrationRequestDto;
import com.example.bookstoreweb.dto.user.UserResponseDto;
import com.example.bookstoreweb.exception.RegistrationException;
import com.example.bookstoreweb.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto registrationRequestDto)
            throws RegistrationException;

    UserResponseDto getByEmail(String email);

    User getUserByEmail(String email);
}
