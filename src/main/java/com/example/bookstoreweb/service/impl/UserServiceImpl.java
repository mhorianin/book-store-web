package com.example.bookstoreweb.service.impl;

import com.example.bookstoreweb.dto.user.UserRegistrationRequestDto;
import com.example.bookstoreweb.dto.user.UserResponseDto;
import com.example.bookstoreweb.exception.RegistrationException;
import com.example.bookstoreweb.mapper.UserMapper;
import com.example.bookstoreweb.model.Role;
import com.example.bookstoreweb.model.ShoppingCart;
import com.example.bookstoreweb.model.User;
import com.example.bookstoreweb.repository.RoleRepository;
import com.example.bookstoreweb.repository.ShoppingCartRepository;
import com.example.bookstoreweb.repository.UserRepository;
import com.example.bookstoreweb.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto registrationRequestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(registrationRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register user");
        }
        User user = userMapper.toModel(registrationRequestDto);
        user.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));

        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRoles(Set.of(userRole));

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Can't find user by email:" + email));
    }
}
