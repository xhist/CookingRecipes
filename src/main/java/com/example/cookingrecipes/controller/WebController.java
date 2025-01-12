package com.example.cookingrecipes.controller;

import com.example.cookingrecipes.service.RecipeService;
import com.example.cookingrecipes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class WebController {
    private final RecipeService recipeService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recipes", recipeService.getAllRecipes());
        return "recipes/list";
    }

    @GetMapping("/recipes")
    public String listRecipes(Model model) {
        model.addAttribute("recipes", recipeService.getAllRecipes());
        return "recipes/list";
    }

    @GetMapping("/recipes/{id}")
    public String recipeDetail(@PathVariable Long id, Model model) {
        model.addAttribute("recipe", recipeService.getRecipeById(id));
        return "recipes/detail";
    }
}