package com.example.cookingrecipes.messages;

import com.example.cookingrecipes.model.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;

    @Pattern(regexp = "\\w{1,15}", message = "Login name must be up to 15 word characters")
    private String loginName;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[^A-Za-z0-9])(?=\\S+$).{8,}$",
            message = "Password must have at least 8 characters, one digit and one special character")
    private String password;

    @NotNull
    private Gender gender;

    @Pattern(regexp = "^$|^(data:|https?://|//).*$", message = "Invalid image URL format")
    private String imageUrl;

    @Size(max = 512)
    private String bio;
}