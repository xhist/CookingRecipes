package com.example.cookingrecipes.repository;

import com.example.cookingrecipes.model.Recipe;
import com.example.cookingrecipes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByUser(User user);
    List<Recipe> findByTagsContaining(String tag);
}