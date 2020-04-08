package com.example.cookbook.service;

import com.example.cookbook.domain.Ingredient;
import com.example.cookbook.domain.Reaction;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.User;
import com.example.cookbook.repo.IngredientRepo;
import com.example.cookbook.repo.ReactionRepo;
import com.example.cookbook.repo.RecipesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class RecipeService {
    @Autowired
    private RecipesRepo recipesRepo;

    @Autowired
    private IngredientRepo ingredientRepo;

    @Autowired
    private ReactionRepo reactionRepo;

    @Autowired
    FileService fileService;

    @Autowired
    HibernateSearchService searchService;

    public void addRecipe(User author,
                          String title,
                          String text,
                          MultipartFile file,
                          String[] ingredientNames,
                          int[] ingredientAmounts) throws IOException {
        Recipe recipe = new Recipe(author, title, text);

        fileService.savePhoto(recipe, file);
        recipesRepo.save(recipe);

        for (int i = 0; i < ingredientNames.length; i++) {
            ingredientRepo.save(new Ingredient(
                    recipe,
                    Ingredient.IngredientType.valueOf(ingredientNames[i].toUpperCase()),
                    ingredientAmounts[i]));
        }
    }

    public Iterable<Recipe> getRecipes(String text) {
        Iterable<Recipe> recipes = text == null
                ? recipesRepo.findAll()
                : searchService.search(text);
        Collections.reverse((List<?>) recipes);
        return recipes;
    }

    public Recipe getRecipe(long id) {
        return recipesRepo.getOne(id);
    }

    public boolean isAccess(long userId, long recipeId) {
        return recipesRepo
                .getOne(recipeId)
                .getAuthor()
                .getId() == userId;
    }

    public void deleteRecipe(long userId, long recipeId) {
        if (isAccess(userId, recipeId)) {
            recipesRepo.deleteById(recipeId);
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
        if (isAccess(userId, recipeId)) {

            Recipe updatingRecipe = recipesRepo.getOne(recipeId);
            fileService.savePhoto(updatingRecipe, file);

            updatingRecipe.setTitle(title);
            updatingRecipe.setText(text);
            updatingRecipe.setTime(LocalDate.now());
            recipesRepo.save(updatingRecipe);

            ingredientRepo.deleteAll(updatingRecipe.getIngredients());
            for (int i = 0; i < ingredientNames.length; i++) {
                ingredientRepo.save(new Ingredient(
                        updatingRecipe,
                        Ingredient.IngredientType.valueOf(ingredientNames[i].toUpperCase()),
                        ingredientAmounts[i]));
            }
        }
    }

    public void addReaction(long recipeId, String reaction) {
        Recipe recipe = recipesRepo.getOne(recipeId);
        Reaction.ReactionType reactionType = Reaction.ReactionType.valueOf(reaction.substring(0, reaction.length() - 4).toUpperCase());

        for (Reaction r : recipe.getReactions()) {
            if (r.getType() == reactionType) {
                r.incrementCount();
                //reactionRepo.deleteById(r.getId());
                reactionRepo.save(r);
                return;
            }
        }
        reactionRepo.save(new Reaction(recipe, reactionType, 1));
    }
}
