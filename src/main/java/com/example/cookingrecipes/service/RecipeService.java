package com.example.cookingrecipes.service;

import com.example.cookingrecipes.dto.RecipeDto;
import com.example.cookingrecipes.exception.ResourceNotFoundException;
import com.example.cookingrecipes.exception.UnauthorizedAccessException;
import com.example.cookingrecipes.mapper.RecipeMapper;
import com.example.cookingrecipes.model.Recipe;
import com.example.cookingrecipes.model.enums.Role;
import com.example.cookingrecipes.model.User;
import com.example.cookingrecipes.repository.RecipeRepository;
import com.example.cookingrecipes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeMapper recipeMapper;
    private final FileService fileService;

    public List<RecipeDto> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecipeDto createRecipe(RecipeDto recipeDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (recipeDto.getImageUrl() != null && recipeDto.getImageUrl().startsWith("data:")) {
            String imageUrl = fileService.saveImage(recipeDto.getImageUrl(), null);
            recipeDto.setImageUrl(imageUrl);
        }

        Recipe recipe = recipeMapper.toEntity(recipeDto);
        recipe.setUser(user);

        return recipeMapper.toDto(recipeRepository.save(recipe));
    }

    public RecipeDto getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .map(recipeMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
    }

    public RecipeDto updateRecipe(RecipeDto recipeDto, String username) {
        Recipe existingRecipe = recipeRepository.findById(recipeDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!canModifyRecipe(existingRecipe, user)) {
            throw new UnauthorizedAccessException("You don't have permission to modify this recipe");
        }

        if (recipeDto.getImageUrl() != null && recipeDto.getImageUrl().startsWith("data:")) {
            String imageUrl = fileService.saveImage(recipeDto.getImageUrl(), existingRecipe.getImageUrl());
            recipeDto.setImageUrl(imageUrl);
        }

        Recipe recipe = recipeMapper.toEntity(recipeDto);
        recipe.setUser(existingRecipe.getUser());

        return recipeMapper.toDto(recipeRepository.save(recipe));
    }

    public void deleteRecipe(Long id, String username) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!canModifyRecipe(recipe, user)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this recipe");
        }

        recipeRepository.deleteById(id);
    }

    private boolean canModifyRecipe(Recipe recipe, User user) {
        return user.getRole() == Role.ADMIN || recipe.getUser().getId().equals(user.getId());
    }
}