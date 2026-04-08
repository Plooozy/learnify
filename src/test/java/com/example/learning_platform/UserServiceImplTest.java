package com.example.learning_platform;

import com.example.learning_platform.dto.UserRegistrationDto;
import com.example.learning_platform.model.User;
import com.example.learning_platform.repository.UserRepository;
import com.example.learning_platform.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_Success() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("nick");
        dto.setPassword("12345");

        when(userRepository.findByUsername("nick")).thenReturn(null);
        when(passwordEncoder.encode("12345")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.registerUser(dto);

        assertNotNull(result);
        assertEquals("nick", result.getUsername());
        assertEquals("hashedPassword", result.getPasswordHash());
    }

    @Test
    void registerUser_UsernameAlreadyExists() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("nick");
        dto.setPassword("12345");

        when(userRepository.findByUsername("nick")).thenReturn(new User());

        assertThrows(RuntimeException.class, () -> userService.registerUser(dto));
    }

    @Test
    void findByUsername_UserExists() {
        User user = new User();
        user.setUsername("nick");

        when(userRepository.findByUsername("nick")).thenReturn(user);

        User result = userService.findByUsername("nick");

        assertNotNull(result);
        assertEquals("nick", result.getUsername());
    }

    @Test
    void findByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        User result = userService.findByUsername("unknown");

        assertNull(result);
    }
}