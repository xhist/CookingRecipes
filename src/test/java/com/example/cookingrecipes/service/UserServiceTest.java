package com.example.cookingrecipes.service;

import com.example.cookingrecipes.dto.UserDto;
import com.example.cookingrecipes.exception.ResourceNotFoundException;
import com.example.cookingrecipes.mapper.UserMapper;
import com.example.cookingrecipes.model.enums.AccountStatus;
import com.example.cookingrecipes.model.enums.Gender;
import com.example.cookingrecipes.model.enums.Role;
import com.example.cookingrecipes.model.User;
import com.example.cookingrecipes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setLoginName("testlogin");
        testUser.setPassword("password");
        testUser.setGender(Gender.MALE);
        testUser.setRole(Role.USER);
        testUser.setStatus(AccountStatus.ACTIVE);

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");
        testUserDto.setLoginName("testlogin");
        testUserDto.setPassword("password");
        testUserDto.setGender(Gender.MALE);
        testUserDto.setRole(Role.USER);
        testUserDto.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

        // Act
        List<UserDto> result = userService.getAllUsers();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo(testUserDto.getUsername());
        verify(userRepository).findAll();
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        // Arrange
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        // Act
        UserDto result = userService.createUser(testUserDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(testUserDto.getUsername());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(any());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.getUserById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }
}