package com.example.cookingrecipes.mapper;

import com.example.cookingrecipes.dto.RecipeDto;
import com.example.cookingrecipes.model.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeMapper {
    private final UserMapper userMapper;

    public RecipeDto toDto(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        RecipeDto dto = new RecipeDto();
        dto.setId(recipe.getId());
        dto.setAuthor(userMapper.toDto(recipe.getAuthor()));
        dto.setName(recipe.getName());
        dto.setShortDescription(recipe.getShortDescription());
        dto.setPreparationTime(recipe.getPreparationTime());
        dto.setIngredients(recipe.getIngredients());
        dto.setImage(recipe.getImage());
        dto.setDescription(recipe.getDescription());
        dto.setTags(recipe.getTags());
        dto.setCreated(recipe.getCreated());
        dto.setModified(recipe.getModified());

        return dto;
    }

    public Recipe toEntity(RecipeDto dto) {
        if (dto == null) {
            return null;
        }

        Recipe recipe = new Recipe();
        recipe.setId(dto.getId());
        recipe.setName(dto.getName());
        recipe.setShortDescription(dto.getShortDescription());
        recipe.setPreparationTime(dto.getPreparationTime());
        recipe.setIngredients(dto.getIngredients());
        recipe.setImage(dto.getImage());
        recipe.setDescription(dto.getDescription());
        recipe.setTags(dto.getTags());

        return recipe;
    }
}