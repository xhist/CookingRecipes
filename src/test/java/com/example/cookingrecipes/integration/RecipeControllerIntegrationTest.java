package com.example.cookingrecipes.integration;

import com.example.cookingrecipes.dto.RecipeDto;
import com.example.cookingrecipes.model.User;
import com.example.cookingrecipes.repository.RecipeRepository;
import com.example.cookingrecipes.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RecipeControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    private RecipeDto testRecipeDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testRecipeDto = createTestRecipeDto();
    }

    @Test
    @WithMockUser
    void getAllRecipes_ShouldReturnRecipes() throws Exception {
        mockMvc.perform(get("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void createRecipe_WithValidData_ShouldCreateRecipe() throws Exception {
        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRecipeDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setLoginName("testlogin");
        user.setPassword("password");
        return userRepository.save(user);
    }

    private RecipeDto createTestRecipeDto() {
        RecipeDto dto = new RecipeDto();
        dto.setName("Test Recipe");
        dto.setShortDescription("Test Description");
        dto.setPreparationTime(30);
        dto.setIngredients(Arrays.asList("ingredient1", "ingredient2"));
        dto.setImage("https://example.com/image.jpg");
        return dto;
    }
}