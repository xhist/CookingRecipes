package com.example.cookingrecipes.dto;

import com.example.cookingrecipes.model.enums.AccountStatus;
import com.example.cookingrecipes.model.enums.Gender;
import com.example.cookingrecipes.model.enums.Role;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private Long id;

    @Pattern(regexp = "\\w{1,15}", message = "Username must be up to 15 word characters")
    private String username;

    private String password;

    private Gender gender;

    private Role role;

    @Pattern(regexp = "^(data:|https?://|//).*$", message = "Invalid image URL format")
    private String imageUrl;

    @Size(max = 512)
    private String bio;

    private AccountStatus status;

    private LocalDateTime created;

    private LocalDateTime modified;
}