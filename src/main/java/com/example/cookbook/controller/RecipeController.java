package com.example.cookbook.controller;

import com.example.cookbook.domain.Ingredient;
import com.example.cookbook.domain.Reaction;
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

import java.io.IOException;
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
    public ModelAndView getRecipes(
            @RequestParam(required = false) String search,
            Map<String, Object> model) {
        model.put("recipes", recipeService.getRecipes(search));
        return new ModelAndView("recipes", model);
    }

    @GetMapping("recipe/add")
    public ModelAndView addRecipeWindow(Map<String, Object> model) {
        model.put("ingredients", Ingredient.IngredientType.values());
        return new ModelAndView("recipe/add", model);
    }

    @PostMapping("recipe/add")
    public RedirectView addRecipe(
            @AuthenticationPrincipal User user,
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam(value = "select[]") String[] ingredientNames,
            @RequestParam(value = "counter[]") int[] ingredientAmounts,
            @RequestParam("file") MultipartFile file) throws IOException {
        recipeService.addRecipe(user, title, text, file, ingredientNames, ingredientAmounts);
        return new RedirectView("/recipes");
    }

    @GetMapping("recipe/page/{id}")
    public ModelAndView getRecipePage(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long id,
            Map<String, Object> model) {

        Recipe recipe = recipeService.getRecipe(id);
        model.put("recipe", recipe);

        boolean isAccess = user != null && recipeService.isAccess(user.getId(), id);
        model.put("isAccess", isAccess);
        model.put("ingredients", recipe.getIngredients());
        List<Reaction> reactionList = recipe.getReactions();
        model.put("reactions", reactionList);

        return new ModelAndView("recipe/page");
    }

    @PostMapping("recipe/page/{id}/reaction/add")
    public void addReaction(
            @PathVariable("id") long recipeId,
            @RequestParam String reaction) {
        // TODO
        //recipeService.addReaction(recipeId, reaction);
    }

    @GetMapping("/reactions")
    public void getAllReactions() {
        // TODO
        //recipeService.getAllReactions();
    }

    @PostMapping("recipe/page/{id}/delete")
    public RedirectView deleteRecipe(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId) {
        recipeService.deleteRecipe(user.getId(), recipeId);
        return new RedirectView("/recipes");
    }

    @GetMapping("recipe/page/{id}/update")
    public ModelAndView updateRecipeWindow(
            @PathVariable("id") long recipeId,
            Map<String, Object> model) {
        model.put("recipe", recipeService.getRecipe(recipeId));
        model.put("ingredient_types", Ingredient.IngredientType.values());
        return new ModelAndView("/recipe/update");
    }

    @PostMapping("recipe/page/{id}/update")
    public RedirectView updateRecipe(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId,
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "select[]") String[] ingredientNames,
            @RequestParam(value = "counter[]") int[] ingredientAmounts) throws IOException {
        recipeService.updateRecipe(user.getId(), recipeId, title, text, file, ingredientNames, ingredientAmounts);
        return new RedirectView("/recipe/page/{id}");
    }
}
