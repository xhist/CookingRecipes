package com.example.cookingrecipes.mapper;

import com.example.cookingrecipes.dto.RecipeDto;
import com.example.cookingrecipes.model.Recipe;
import com.example.cookingrecipes.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeMapper {
    private final UserMapper userMapper;
    private final FileService fileService;

    @Value("${app.upload.dir}")
    private String uploadDir;


    public RecipeDto toDto(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        RecipeDto dto = RecipeDto.builder()
                .id(recipe.getId())
                .userDto(userMapper.toDto(recipe.getUser()))
                .title(recipe.getTitle())
                .shortDescription(recipe.getShortDescription())
                .preparationTime(recipe.getPreparationTime())
                .ingredients(recipe.getIngredients())
                .description(recipe.getDescription())
                .tags(recipe.getTags())
                .created(recipe.getCreated())
                .modified(recipe.getModified())
                .build();

        dto.setImageUrl(recipe.getImageUrl() != null && !recipe.getImageUrl().startsWith("data:")
                ? fileService.encodeImageToBase64(recipe.getImageUrl())
                : recipe.getImageUrl());

        return dto;
    }

    public Recipe toEntity(RecipeDto dto) {
        if (dto == null) {
            return null;
        }

        Recipe recipe = new Recipe();
        recipe.setId(dto.getId());
        recipe.setTitle(dto.getTitle());
        recipe.setShortDescription(dto.getShortDescription());
        recipe.setPreparationTime(dto.getPreparationTime());
        recipe.setIngredients(dto.getIngredients());
        recipe.setImageUrl(dto.getImageUrl());
        recipe.setDescription(dto.getDescription());
        recipe.setTags(dto.getTags());

        return recipe;
    }
}