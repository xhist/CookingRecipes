package com.example.cookingrecipes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RecipeDto {
    private Long id;

    private UserDto userDto;

    @Size(max = 80)
    @NotBlank
    private String title;

    @Size(max = 256)
    private String shortDescription;

    @Positive
    private Integer preparationTime;

    private List<String> ingredients;

    @NotBlank
    @Pattern(regexp = "^(data:|https?://|//).*$", message = "Invalid image URL format")
    private String imageUrl;

    @Size(max = 2048)
    private String description;

    private List<String> tags;

    private LocalDateTime created;

    private LocalDateTime modified;
}