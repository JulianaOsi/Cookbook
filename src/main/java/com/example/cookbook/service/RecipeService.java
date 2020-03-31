package com.example.cookbook.service;

import com.example.cookbook.domain.Ingredient;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.User;
import com.example.cookbook.repo.IngredientRepo;
import com.example.cookbook.repo.RecipesRepo;
import com.example.cookbook.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class RecipeService {
    @Autowired
    private RecipesRepo recipesRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private IngredientRepo ingredientRepo;

    public void addRecipe(User author, String title, String text, MultipartFile file) throws IOException {
        Recipe recipe = new Recipe(author, title, text);

        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
            byte [] fileInBytes = file.getBytes();
            InputStream in = new ByteArrayInputStream(fileInBytes);
            BufferedImage image = ImageIO.read(in);
            try {
                if (image.getWidth() > 400 && image.getHeight() > 400) {
                    BufferedImage croppedImage = image.getSubimage(Math.round(image.getWidth()/2) - 200, Math.round(image.getHeight()/2) - 200, 400, 400);
                    ImageIO.write(croppedImage, "jpg", new File(uploadPath + "/" + fileName));
                }
                else {
                    ImageIO.write(image,"jpg", new File(uploadPath + "/" + fileName));
                }
                recipe.setFilename(fileName);
            }

            catch (java.lang.NullPointerException ignored) {

            }
        }

        Ingredient ingredient1 = new Ingredient(recipe, Ingredient.IngredientType.CARROT, 5);
        Ingredient ingredient2 = new Ingredient(recipe, Ingredient.IngredientType.POTATO, 3);
        recipesRepo.save(recipe);
        ingredientRepo.save(ingredient1);
        ingredientRepo.save(ingredient2);
    }

    public Iterable<Recipe> getRecipes() {
        Iterable<Recipe> recipes = recipesRepo.findAll();
        Collections.reverse((List<?>) recipes);
        return recipes;
    }

    public Recipe getRecipe(long id) {
        return recipesRepo.getOne(id);
    }

    public boolean canDeleteRecipe(long userId, long recipeId) {
        return recipesRepo
                .getOne(recipeId)
                .getAuthor()
                .getId() == userId;
    }

    public void deleteRecipe (long userId, long recipeId) {
        if (canDeleteRecipe(userId, recipeId)) {
            recipesRepo.deleteById(recipeId);
        }
    }
}
