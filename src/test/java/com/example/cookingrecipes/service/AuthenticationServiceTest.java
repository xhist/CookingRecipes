package com.example.cookingrecipes.service;

import com.example.cookingrecipes.messages.AuthenticationRequest;
import com.example.cookingrecipes.messages.AuthenticationResponse;
import com.example.cookingrecipes.messages.RegisterRequest;
import com.example.cookingrecipes.mapper.UserMapper;
import com.example.cookingrecipes.model.User;
import com.example.cookingrecipes.model.enums.AccountStatus;
import com.example.cookingrecipes.repository.UserRepository;
import com.example.cookingrecipes.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("testpass123!");
        registerRequest.setLoginName("testlogin");

        authRequest = new AuthenticationRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("testpass123!");

        user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void register_WithValidData_ShouldRegisterUser() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByLoginName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn("token");

        AuthenticationResponse response = authenticationService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WithExistingUsername_ShouldThrowException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.register(registerRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Username already exists");
    }

    @Test
    void register_WithExistingLoginName_ShouldThrowException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByLoginName(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.register(registerRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Login name already exists");
    }

    @Test
    void login_WithValidCredentials_ShouldLoginUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("token");

        AuthenticationResponse response = authenticationService.login(authRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_WithSuspendedAccount_ShouldThrowException() {
        user.setStatus(AccountStatus.SUSPENDED);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(null);

        assertThatThrownBy(() -> authenticationService.login(authRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Account is suspended");
    }
}