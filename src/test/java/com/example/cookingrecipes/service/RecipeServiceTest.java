package com.example.cookingrecipes.service;

import com.example.cookingrecipes.dto.RecipeDto;
import com.example.cookingrecipes.exception.ResourceNotFoundException;
import com.example.cookingrecipes.exception.UnauthorizedAccessException;
import com.example.cookingrecipes.mapper.RecipeMapper;
import com.example.cookingrecipes.model.*;
import com.example.cookingrecipes.model.enums.Role;
import com.example.cookingrecipes.repository.RecipeRepository;
import com.example.cookingrecipes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {
    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe testRecipe;
    private RecipeDto testRecipeDto;
    private User author;
    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        // Setting up test users
        author = new User();
        author.setId(1L);
        author.setUsername("author");
        author.setRole(Role.USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);

        regularUser = new User();
        regularUser.setId(3L);
        regularUser.setUsername("regular");
        regularUser.setRole(Role.USER);

        // Setting up test recipe
        testRecipe = new Recipe();
        testRecipe.setId(1L);
        testRecipe.setName("Test Recipe");
        testRecipe.setAuthor(author);
        testRecipe.setIngredients(Arrays.asList("ing1", "ing2"));
        testRecipe.setPreparationTime(30);

        testRecipeDto = new RecipeDto();
        testRecipeDto.setId(1L);
        testRecipeDto.setName("Test Recipe");
        testRecipeDto.setPreparationTime(30);
    }

    @Test
    void getAllRecipes_ShouldReturnAllRecipes() {
        when(recipeRepository.findAll()).thenReturn(Arrays.asList(testRecipe));
        when(recipeMapper.toDto(any(Recipe.class))).thenReturn(testRecipeDto);

        List<RecipeDto> result = recipeService.getAllRecipes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(testRecipeDto.getName());
        verify(recipeRepository).findAll();
    }

    @Test
    void createRecipe_WithValidData_ShouldCreateRecipe() {
        when(userRepository.findByUsername(author.getUsername())).thenReturn(Optional.of(author));
        when(recipeMapper.toEntity(any(RecipeDto.class))).thenReturn(testRecipe);
        when(recipeMapper.toDto(any(Recipe.class))).thenReturn(testRecipeDto);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(testRecipe);

        RecipeDto result = recipeService.createRecipe(testRecipeDto, author.getUsername());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testRecipeDto.getName());
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void createRecipe_WithNonexistentUser_ShouldThrowException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.createRecipe(testRecipeDto, "nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void updateRecipe_ByAuthor_ShouldUpdateRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(userRepository.findByUsername(author.getUsername())).thenReturn(Optional.of(author));
        when(recipeMapper.toEntity(any(RecipeDto.class))).thenReturn(testRecipe);
        when(recipeMapper.toDto(any(Recipe.class))).thenReturn(testRecipeDto);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(testRecipe);

        RecipeDto result = recipeService.updateRecipe(testRecipeDto, author.getUsername());

        assertThat(result).isNotNull();
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void updateRecipe_ByAdmin_ShouldUpdateRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));
        when(recipeMapper.toEntity(any(RecipeDto.class))).thenReturn(testRecipe);
        when(recipeMapper.toDto(any(Recipe.class))).thenReturn(testRecipeDto);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(testRecipe);

        RecipeDto result = recipeService.updateRecipe(testRecipeDto, adminUser.getUsername());

        assertThat(result).isNotNull();
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void updateRecipe_ByUnauthorizedUser_ShouldThrowException() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(userRepository.findByUsername(regularUser.getUsername())).thenReturn(Optional.of(regularUser));

        assertThatThrownBy(() -> recipeService.updateRecipe(testRecipeDto, regularUser.getUsername()))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessage("You don't have permission to modify this recipe");
    }

    @Test
    void deleteRecipe_ByAuthor_ShouldDeleteRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(userRepository.findByUsername(author.getUsername())).thenReturn(Optional.of(author));

        recipeService.deleteRecipe(1L, author.getUsername());

        verify(recipeRepository).deleteById(1L);
    }

    @Test
    void deleteRecipe_ByAdmin_ShouldDeleteRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));

        recipeService.deleteRecipe(1L, adminUser.getUsername());

        verify(recipeRepository).deleteById(1L);
    }

    @Test
    void deleteRecipe_ByUnauthorizedUser_ShouldThrowException() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(userRepository.findByUsername(regularUser.getUsername())).thenReturn(Optional.of(regularUser));

        assertThatThrownBy(() -> recipeService.deleteRecipe(1L, regularUser.getUsername()))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    void deleteRecipe_NonexistentRecipe_ShouldThrowException() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.deleteRecipe(1L, author.getUsername()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}