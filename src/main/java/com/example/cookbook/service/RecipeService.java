package com.example.cookbook.service;

import com.example.cookbook.domain.*;
import com.example.cookbook.repo.IngredientRepo;
import com.example.cookbook.repo.IngredientTypeRepo;
import com.example.cookbook.repo.RecipeRepo;
import com.example.cookbook.service.exception.NotAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@Service
public final class RecipeService {
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
                          int[] ingredientAmounts,
                          String[] ingredientUnits) throws NotAuthorizedException {
        if (author == null) throw new NotAuthorizedException();
        final Recipe recipe = new Recipe(author, title, text);

        photoService.savePhoto(recipe, file);
        recipeRepo.save(recipe);
        saveIngredients(recipe, ingredientTypeNames, ingredientAmounts, ingredientUnits);
    }

    public Iterable<Recipe> getRecipes(String text, List<Long> ingredientsId) {
        Iterable<Recipe> recipes;
        if (text != null && !text.equals("")) {
            recipes = searchService.search(text);
        } else if (ingredientsId != null && ingredientsId.size() != 0) {
            recipes = recipeRepo.findByIngredientTypesId(ingredientsId);
        } else
            recipes = recipeRepo.findAllByOrderByTimeDesc();
        return recipes;
    }

    public Recipe getRecipe(long id) {
        return recipeRepo.getOne(id);
    }

    public boolean isAuthorOrAdmin(User user, long recipeId) {
        return recipeRepo
                .getOne(recipeId)
                .getAuthor()
                .getId().equals(user.getId())
                || user.getRoles().contains(Role.ADMIN);
    }

    public void deleteRecipe(User user, long recipeId) throws NotAuthorizedException {
        if (user == null) throw new NotAuthorizedException();
        if (isAuthorOrAdmin(user, recipeId)) {
            recipeRepo.deleteById(recipeId);
        }
    }

    public void updateRecipe(
            User user,
            long recipeId,
            String title,
            String text,
            MultipartFile file,
            String[] ingredientTypeNames,
            int[] ingredientAmounts,
            String[] ingredientUnits) throws NotAuthorizedException {
        if (user == null) throw new NotAuthorizedException();
        if (isAuthorOrAdmin(user, recipeId)) {
            final Recipe updatingRecipe = recipeRepo.getOne(recipeId);
            photoService.savePhoto(updatingRecipe, file);

            updatingRecipe.setTitle(title);
            updatingRecipe.setText(text);
            updatingRecipe.setTime(LocalDate.now());
            recipeRepo.save(updatingRecipe);

            ingredientRepo.deleteAll(updatingRecipe.getIngredients());
            saveIngredients(updatingRecipe, ingredientTypeNames, ingredientAmounts, ingredientUnits);
        }
    }

    private void saveIngredients(
            Recipe recipe,
            String[] ingredientTypeNames,
            int[] ingredientAmounts,
            String[] ingredientUnits) {
        for (int i = 0; i < ingredientTypeNames.length; i++) {
            final String typeName = ingredientTypeNames[i];
            IngredientType ingredientType = ingredientTypeRepo.findByName(typeName);
            if (ingredientType == null) {
                ingredientType = new IngredientType(typeName);
                ingredientTypeRepo.save(ingredientType);
            }
            ingredientRepo.save(new Ingredient(
                    recipe,
                    ingredientType,
                    ingredientAmounts[i],
                    ingredientUnits[i]));
        }
    }

    public List<Recipe> getUserRecipes(User author) {
        return recipeRepo.findByAuthor(author);
    }
}
