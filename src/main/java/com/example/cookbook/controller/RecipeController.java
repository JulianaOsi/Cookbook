package com.example.cookbook.controller;

import com.example.cookbook.domain.Recipe;
import com.example.cookbook.repo.RecipesRepo;
import com.example.cookbook.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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
    public RedirectView addRecipe(@RequestParam String title, @RequestParam String text) {
        recipeService.addRecipe(title, text);
        return new RedirectView("/recipes");
    }
}
