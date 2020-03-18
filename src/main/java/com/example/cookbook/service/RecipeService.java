package com.example.cookbook.service;

import com.example.cookbook.domain.Recipe;
import com.example.cookbook.repo.RecipesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RecipeService {
    @Autowired
    private RecipesRepo recipesRepo;

    public void addRecipe(String title, String text) {
        Recipe recipe = new Recipe(title, text);
        recipesRepo.save(recipe);
    }

    public Iterable<Recipe> getRecipes() {
        Iterable<Recipe> recipes = recipesRepo.findAll();
        Collections.reverse((List<?>) recipes);
        return recipes;
    }
}
