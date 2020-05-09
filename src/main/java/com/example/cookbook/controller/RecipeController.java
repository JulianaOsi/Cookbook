package com.example.cookbook.controller;

import com.example.cookbook.domain.*;
import com.example.cookbook.service.CommentService;
import com.example.cookbook.service.IngredientTypeService;
import com.example.cookbook.service.ReactionService;
import com.example.cookbook.service.RecipeService;
import com.example.cookbook.service.exception.NoAccessException;
import com.example.cookbook.service.exception.NotAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Controller
public final class RecipeController {
    @Autowired
    RecipeService recipeService;

    @Autowired
    ReactionService reactionService;

    @Autowired
    IngredientTypeService ingredientTypeService;

    @Autowired
    CommentService commentService;

    @GetMapping("/")
    public RedirectView redirectToRecipes() {
        return new RedirectView("/recipes");
    }

    @GetMapping("recipes")
    public ModelAndView getRecipes(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, value = "ingredientsId[]") List<Long> ingredientsId,
            Map<String, Object> model) {

        final boolean isUserAuthorized = user != null;
        model.put("isUserAuthorized", isUserAuthorized);

        model.put("ingredientTypes", ingredientTypeService.getIngredientTypes());
        model.put("recipes", recipeService.getRecipes(search, ingredientsId));
        return new ModelAndView("recipes", model);
    }

    @GetMapping("recipe/add")
    public ModelAndView getRecipeAddForm(
            @AuthenticationPrincipal User user,
            Map<String, Object> model) {
        model.put("ingredientTypes", ingredientTypeService.getIngredientTypes());
        final boolean isUserAuthorized = user != null;
        model.put("isUserAuthorized", isUserAuthorized);
        model.put("units", Ingredient.Unit.getUnits().entrySet());
        return new ModelAndView("recipe/add", model);
    }

    @PostMapping("recipe/add")
    public RedirectView addRecipe(
            @AuthenticationPrincipal User user,
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam(value = "ingredients[]") String[] ingredientTypeNames,
            @RequestParam(value = "amounts[]") int[] ingredientAmounts,
            @RequestParam(value = "units[]") String[] ingredientUnits,
            @RequestParam("file") MultipartFile file) throws NotAuthorizedException {
        recipeService.addRecipe(user, title, text, file, ingredientTypeNames, ingredientAmounts, ingredientUnits);
        return new RedirectView("/recipes");
    }

    @GetMapping("recipe/page/{id}")
    public ModelAndView getRecipePage(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long id,
            Map<String, Object> model) {

        final Recipe recipe = recipeService.getRecipe(id);
        model.put("recipe", recipe);

        final boolean isUserAuthorized = user != null;
        model.put("isUserAuthorized", isUserAuthorized);

        final boolean isAuthorOrAdmin = isUserAuthorized && recipeService.isAuthorOrAdmin(user, id);
        model.put("isAuthorOrAdmin", isAuthorOrAdmin);

        model.put("ingredients", recipe.getIngredients());
        final Map<Comment, Boolean> comments = new LinkedHashMap<>();
        commentService
                .getRecipeComments(recipe)
                .forEach(comment -> {
                    boolean hasAccess = isUserAuthorized && commentService.isAuthorOrAdmin(user, comment.getId());
                    comments.put(comment, hasAccess);
                });
        model.put("comments", comments.entrySet());

        model.put("reactionsTypes", Reaction.ReactionType.values());
        model.put("reactions", reactionService.getRecipeReactions(recipe));

        return new ModelAndView("recipe/page");
    }

    @PostMapping("recipe/page/{id}/reaction/add")
    public RedirectView addReaction(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId,
            @RequestParam String reactionTypeName) throws NotAuthorizedException {
        reactionService.addReaction(user, recipeId, reactionTypeName);
        return new RedirectView("/recipe/page/{id}");
    }

    @PostMapping("recipe/page/{id}/delete")
    public RedirectView deleteRecipe(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId) throws NotAuthorizedException {
        recipeService.deleteRecipe(user, recipeId);
        return new RedirectView("/recipes");
    }

    @PostMapping("user/profile/recipe/{id}/delete")
    public RedirectView deleteRecipeFromProfile(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId) throws NotAuthorizedException {
        recipeService.deleteRecipe(user, recipeId);
        return new RedirectView("user/profile");
    }

    @GetMapping("recipe/page/{id}/update")
    public ModelAndView getRecipeUpdateForm(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId,
            Map<String, Object> model) {
        model.put("recipe", recipeService.getRecipe(recipeId));
        model.put("ingredientTypes", ingredientTypeService.getIngredientTypes());
        final boolean isUserAuthorized = user != null;
        model.put("isUserAuthorized", isUserAuthorized);
        model.put("units", Ingredient.Unit.getUnits().entrySet());
        return new ModelAndView("/recipe/update");
    }

    @PostMapping("recipe/page/{id}/update")
    public RedirectView updateRecipe(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId,
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "ingredients[]") String[] ingredientTypeNames,
            @RequestParam(value = "amounts[]") int[] ingredientAmounts,
            @RequestParam(value = "units[]") String[] ingredientUnits) throws NotAuthorizedException {
        recipeService.updateRecipe(user, recipeId, title, text, file, ingredientTypeNames, ingredientAmounts, ingredientUnits);
        return new RedirectView("/recipe/page/{id}");
    }

    @PostMapping("recipe/page/{id}/comment/add")
    public RedirectView addComment(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId,
            @RequestParam String text) throws NotAuthorizedException {
        commentService.addComment(user, recipeId, text);
        return new RedirectView("/recipe/page/{id}");
    }

    @GetMapping("recipe/page/{r_id}/comment/{c_id}/update")
    public RedirectView getCommentUpdateForm(
            @PathVariable("c_id") long commentId,
            Map<String, Object> model) {
        final Comment comment = commentService.getComment(commentId);
        model.put("comment", comment);
        return new RedirectView("/recipe/page/{r_id}");
    }

    @PostMapping("recipe/page/{r_id}/comment/{c_id}/update")
    public RedirectView updateComment(
            @AuthenticationPrincipal User user,
            @PathVariable("c_id") long commentId,
            @RequestParam String text) throws NotAuthorizedException, NoAccessException {
        commentService.updateComment(user, commentId, text);
        return new RedirectView("/recipe/page/{r_id}");
    }

    @PostMapping("recipe/page/{r_id}/comment/{c_id}/delete")
    public RedirectView deleteComment(
            @AuthenticationPrincipal User user,
            @PathVariable("c_id") long commentId) throws NotAuthorizedException, NoAccessException {
        commentService.deleteComment(user, commentId);
        return new RedirectView("/recipe/page/{r_id}");
    }
}
