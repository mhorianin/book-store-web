package com.example.bookstoreweb.service.impl;

import com.example.bookstoreweb.dto.user.UserRegistrationRequestDto;
import com.example.bookstoreweb.dto.user.UserResponseDto;
import com.example.bookstoreweb.exception.RegistrationException;
import com.example.bookstoreweb.mapper.UserMapper;
import com.example.bookstoreweb.model.User;
import com.example.bookstoreweb.repository.UserRepository;
import com.example.bookstoreweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto registrationRequestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(registrationRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register user");
        }
        User user = userMapper.toModel(registrationRequestDto);
        return userMapper.toDto(userRepository.save(user));
    }
}
