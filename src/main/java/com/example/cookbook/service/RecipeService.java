package com.example.cookbook.service;

import com.example.cookbook.domain.*;
import com.example.cookbook.repo.IngredientRepo;
import com.example.cookbook.repo.RecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class RecipeService {
    @Autowired
    private RecipeRepo recipeRepo;

    @Autowired
    private IngredientRepo ingredientRepo;

    @Autowired
    PhotoService photoService;

    @Autowired
    HibernateSearchService searchService;

    public void addRecipe(User author,
                          String title,
                          String text,
                          MultipartFile file,
                          String[] ingredientNames,
                          int[] ingredientAmounts) throws IOException {
        Recipe recipe = new Recipe(author, title, text);

        photoService.savePhoto(recipe, file);
        recipeRepo.save(recipe);

        for (int i = 0; i < ingredientNames.length; i++) {
            ingredientRepo.save(new Ingredient(
                    recipe,
                    Ingredient.IngredientType.valueOf(ingredientNames[i].toUpperCase()),
                    ingredientAmounts[i]));
        }
    }

    public Iterable<Recipe> getRecipes(String text) {
        Iterable<Recipe> recipes = text == null
                ? recipeRepo.findAll()
                : searchService.search(text);
        Collections.reverse((List<?>) recipes);
        return recipes;
    }

    public Recipe getRecipe(long id) {
        return recipeRepo.getOne(id);
    }

    public boolean isAuthor(long userId, long recipeId) {
        return recipeRepo
                .getOne(recipeId)
                .getAuthor()
                .getId() == userId;
    }

    public void deleteRecipe(long userId, long recipeId) {
        if (isAuthor(userId, recipeId)) {
            recipeRepo.deleteById(recipeId);
        }
    }

    public void updateRecipe(
            long userId,
            long recipeId,
            String title,
            String text,
            MultipartFile file,
            String[] ingredientNames,
            int[] ingredientAmounts) throws IOException {
        if (isAuthor(userId, recipeId)) {

            Recipe updatingRecipe = recipeRepo.getOne(recipeId);
            photoService.savePhoto(updatingRecipe, file);

            updatingRecipe.setTitle(title);
            updatingRecipe.setText(text);
            updatingRecipe.setTime(LocalDate.now());
            recipeRepo.save(updatingRecipe);

            ingredientRepo.deleteAll(updatingRecipe.getIngredients());
            for (int i = 0; i < ingredientNames.length; i++) {
                ingredientRepo.save(new Ingredient(
                        updatingRecipe,
                        Ingredient.IngredientType.valueOf(ingredientNames[i].toUpperCase()),
                        ingredientAmounts[i]));
            }
        }
    }


}
