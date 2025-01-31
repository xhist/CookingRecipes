package com.example.cookingrecipes.service;

import com.example.cookingrecipes.messages.AuthenticationRequest;
import com.example.cookingrecipes.messages.AuthenticationResponse;
import com.example.cookingrecipes.messages.RegisterRequest;
import com.example.cookingrecipes.exception.ResourceNotFoundException;
import com.example.cookingrecipes.mapper.UserMapper;
import com.example.cookingrecipes.model.enums.AccountStatus;
import com.example.cookingrecipes.model.enums.Role;
import com.example.cookingrecipes.model.User;
import com.example.cookingrecipes.repository.UserRepository;
import com.example.cookingrecipes.security.CustomUserDetails;
import com.example.cookingrecipes.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final FileService fileService;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username already exists");
        }

        var user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGender(request.getGender());
        user.setRole(Role.USER);
        user.setBio(request.getBio());
        user.setStatus(AccountStatus.ACTIVE);

        if (request.getImageUrl() != null && request.getImageUrl().startsWith("data:")) {
            String imageUrl = fileService.saveImage(request.getImageUrl(), null);
            user.setImageUrl(imageUrl);
        }

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(new CustomUserDetails(savedUser));

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(userMapper.toDto(savedUser))
                .build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is " + user.getStatus().toString().toLowerCase());
        }

        var jwtToken = jwtService.generateToken(new CustomUserDetails(user));

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(userMapper.toDto(user))
                .build();
    }
}