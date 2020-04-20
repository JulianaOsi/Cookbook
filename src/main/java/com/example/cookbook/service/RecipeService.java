package com.example.cookbook.service;

import com.example.cookbook.domain.*;
import com.example.cookbook.repo.IngredientRepo;
import com.example.cookbook.repo.IngredientTypeRepo;
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
    private IngredientTypeRepo ingredientTypeRepo;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private HibernateSearchService searchService;

    public void addRecipe(User author,
                          String title,
                          String text,
                          MultipartFile file,
                          String[] ingredientTypeNames,
                          int[] ingredientAmounts) throws IOException {
        Recipe recipe = new Recipe(author, title, text);

        photoService.savePhoto(recipe, file);
        recipeRepo.save(recipe);
        saveIngredients(recipe, ingredientTypeNames, ingredientAmounts);
    }

    public Iterable<Recipe> getRecipes(String text, List<Long> ingredientsId) {
        Iterable<Recipe> recipes;
        if (text != null && !text.equals("")) {
            recipes = searchService.search(text);
        } else if (ingredientsId != null && ingredientsId.size() != 0) {
            recipes = recipeRepo.findByIngredientTypesId(ingredientsId);
        } else
            recipes = recipeRepo.findAll();
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
            String[] ingredientTypeNames,
            int[] ingredientAmounts) throws IOException {
        if (isAuthor(userId, recipeId)) {

            Recipe updatingRecipe = recipeRepo.getOne(recipeId);
            photoService.savePhoto(updatingRecipe, file);

            updatingRecipe.setTitle(title);
            updatingRecipe.setText(text);
            updatingRecipe.setTime(LocalDate.now());
            recipeRepo.save(updatingRecipe);

            ingredientRepo.deleteAll(updatingRecipe.getIngredients());
            saveIngredients(updatingRecipe, ingredientTypeNames, ingredientAmounts);
        }
    }

    private void saveIngredients(Recipe recipe, String[] ingredientTypeNames, int[] ingredientAmounts) {
        for (int i = 0; i < ingredientTypeNames.length; i++) {
            String typeName = ingredientTypeNames[i];
            IngredientType ingredientType = ingredientTypeRepo.findByName(typeName);
            if (ingredientType == null) {
                ingredientType = new IngredientType(typeName);
                ingredientTypeRepo.save(ingredientType);
            }
            ingredientRepo.save(new Ingredient(
                    recipe,
                    ingredientType,
                    ingredientAmounts[i]));
        }
    }
}
