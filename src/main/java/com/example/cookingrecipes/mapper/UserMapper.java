package com.example.cookingrecipes.mapper;

import com.example.cookingrecipes.dto.UserDto;
import com.example.cookingrecipes.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setLoginName(user.getLoginName());
        dto.setGender(user.getGender());
        dto.setRole(user.getRole());
        dto.setProfileImage(user.getProfileImage());
        dto.setBio(user.getBio());
        dto.setStatus(user.getStatus());
        dto.setCreated(user.getCreated());
        dto.setModified(user.getModified());

        return dto;
    }

    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setLoginName(dto.getLoginName());
        user.setPassword(dto.getPassword());
        user.setGender(dto.getGender());
        user.setRole(dto.getRole());
        user.setProfileImage(dto.getProfileImage());
        user.setBio(dto.getBio());
        user.setStatus(dto.getStatus());

        return user;
    }
}