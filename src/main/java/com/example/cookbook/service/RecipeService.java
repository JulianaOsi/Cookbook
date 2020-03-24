package com.example.cookbook.service;

import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.User;
import com.example.cookbook.repo.RecipesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class RecipeService {
    @Autowired
    private RecipesRepo recipesRepo;

    @Value("${upload.path}")
    private String uploadPath;

    public void addRecipe(User author, String title, String text, MultipartFile file) throws IOException {
        Recipe recipe = new Recipe(author, title, text);

        if (file != null) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + fileName));
            recipe.setFilename(fileName);
        }

        recipesRepo.save(recipe);
    }

    public Iterable<Recipe> getRecipes() {
        Iterable<Recipe> recipes = recipesRepo.findAll();
        Collections.reverse((List<?>) recipes);
        return recipes;
    }
}
