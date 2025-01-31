package com.example.cookingrecipes.mapper;

import com.example.cookingrecipes.dto.UserDto;
import com.example.cookingrecipes.model.User;
import com.example.cookingrecipes.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final FileService fileService;

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .gender(user.getGender())
                .role(user.getRole())
                .bio(user.getBio())
                .status(user.getStatus())
                .created(user.getCreated())
                .modified(user.getModified())
                .build();

        dto.setImageUrl(user.getImageUrl() != null && !user.getImageUrl().startsWith("data:")
                ? fileService.encodeImageToBase64(user.getImageUrl())
                : user.getImageUrl());

        return dto;
    }

    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setGender(dto.getGender());
        user.setRole(dto.getRole());
        user.setImageUrl(dto.getImageUrl());
        user.setBio(dto.getBio());
        user.setStatus(dto.getStatus());

        return user;
    }
}