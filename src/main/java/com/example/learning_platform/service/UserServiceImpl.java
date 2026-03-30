package com.example.learning_platform.service;

import com.example.learning_platform.dto.UserRegistrationDto;
import com.example.learning_platform.model.User;
import com.example.learning_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRegistrationDto userData) {
        if (userRepository.findByUsername(userData.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(userData.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userData.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}