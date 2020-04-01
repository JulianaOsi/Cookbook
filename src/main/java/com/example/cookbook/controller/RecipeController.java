package com.example.cookbook.controller;

import com.example.cookbook.domain.Ingredient;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.User;
import com.example.cookbook.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class RecipeController {
    @Autowired
    RecipeService recipeService;

    @GetMapping("/")
    public RedirectView redirectToRecipes() {
        return new RedirectView("/recipes");
    }

    @GetMapping("recipes")
    public ModelAndView getRecipes(Map<String, Object> model) {
        model.put("recipes", recipeService.getRecipes());
        return new ModelAndView("recipes", model);
    }

    @GetMapping("recipe/add")
    public ModelAndView addRecipeWindow() {
        return new ModelAndView("recipe/add");
    }

    @PostMapping("recipe/add")
    public RedirectView addRecipe(
            @AuthenticationPrincipal User user,
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam("file") MultipartFile file) throws IOException {

        recipeService.addRecipe(user, title, text, file);
        return new RedirectView("/recipes");
    }

    @GetMapping("recipe/page/{id}")
    public ModelAndView getRecipePage(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long id,
            Map<String, Object> model) {

        Recipe recipe = recipeService.getRecipe(id);
        model.put("recipe", recipe);

        boolean canDelete = user != null && recipeService.canDeleteRecipe(user.getId(), id);
        model.put("canDelete", canDelete);
        model.put("ingredients", recipe.getIngredients());

        return new ModelAndView("recipe/page");
    }

    @PostMapping("recipe/page/{id}/delete")
    public RedirectView delete(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long id) {
        recipeService.deleteRecipe(user.getId(), id);
        return new RedirectView("/recipes");
    }
}
