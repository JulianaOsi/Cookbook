package com.example.cookbook.controller;

import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.User;
import com.example.cookbook.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    RecipeService recipeService;

    @GetMapping("/user/profile")
    public ModelAndView getProfile(
            @AuthenticationPrincipal User user,
            Map<String, Object> model) {
        model.put("user", user);
        List<Recipe> recipes = recipeService.getUserRecipes(user);
        model.put("recipes", recipes);
        model.put("recipesSize", recipes.size());
        return new ModelAndView("user/profile");
    }
}
