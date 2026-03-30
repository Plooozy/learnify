package com.example.learning_platform.service;

import com.example.learning_platform.dto.UserRegistrationDto;
import com.example.learning_platform.model.User;

public interface UserService {
    User registerUser(UserRegistrationDto userData);
    User findByUsername(String username);
}