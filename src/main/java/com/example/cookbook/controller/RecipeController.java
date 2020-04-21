package com.example.cookbook.controller;

import com.example.cookbook.domain.*;
import com.example.cookbook.service.CommentService;
import com.example.cookbook.service.IngredientTypeService;
import com.example.cookbook.service.ReactionService;
import com.example.cookbook.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class RecipeController {
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
            @RequestParam(required = false) List<Long> ingredientsId,
            Map<String, Object> model) {
        boolean isUserAuthorized = user != null;
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
        boolean isUserAuthorized = user != null;
        model.put("isUserAuthorized", isUserAuthorized);
        return new ModelAndView("recipe/add", model);
    }

    @PostMapping("recipe/add")
    public RedirectView addRecipe(
            @AuthenticationPrincipal User user,
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam(value = "select[]") String[] ingredientTypeNames,
            @RequestParam(value = "counter[]") int[] ingredientAmounts,
            @RequestParam("file") MultipartFile file) throws IOException {
        recipeService.addRecipe(user, title, text, file, ingredientTypeNames, ingredientAmounts);
        return new RedirectView("/recipes");
    }

    @GetMapping("recipe/page/{id}")
    public ModelAndView getRecipePage(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long id,
            Map<String, Object> model) {

        Recipe recipe = recipeService.getRecipe(id);
        model.put("recipe", recipe);

        boolean isUserAuthorized = user != null;
        model.put("isUserAuthorized", isUserAuthorized);

        boolean hasAccessToRecipe = isUserAuthorized && recipeService.isAuthor(user.getId(), id);
        model.put("hasAccessToRecipe", hasAccessToRecipe);

        model.put("ingredients", recipe.getIngredients());
        Map<Comment, Boolean> comments = new LinkedHashMap<>();
        List<Comment> commentList = recipe.getComments();
        commentList.sort(Comparator.comparing(Comment::getTime, LocalDateTime::compareTo).reversed());
        commentList
                .forEach(comment -> {
                    boolean hasAccess = isUserAuthorized && commentService.isAuthor(user.getId(), comment.getId());
                    comments.put(comment, hasAccess);
                });
        model.put("comments", comments.entrySet());

        model.put("reactionsTypes", Reaction.ReactionType.values());
        List<Reaction> reactionList = recipe.getReactions();
        Comparator<Reaction> compareByCount = Comparator.comparingInt(Reaction::getCount);
        reactionList.sort(compareByCount.reversed());
        model.put("reactions", reactionList);

        return new ModelAndView("recipe/page");
    }

    @PostMapping("recipe/page/{id}/reaction/add")
    public RedirectView addReaction(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId,
            @RequestParam String reactionTypeName) {
        reactionService.addReaction(user, recipeId, reactionTypeName);
        return new RedirectView("/recipe/page/{id}");
    }

    @PostMapping("recipe/page/{id}/delete")
    public RedirectView deleteRecipe(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId) {
        recipeService.deleteRecipe(user.getId(), recipeId);
        return new RedirectView("/recipes");
    }

    @GetMapping("recipe/page/{id}/update")
    public ModelAndView getRecipeUpdateForm(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId,
            Map<String, Object> model) {
        model.put("recipe", recipeService.getRecipe(recipeId));
        model.put("ingredientTypes", ingredientTypeService.getIngredientTypes());
        boolean isUserAuthorized = user != null;
        model.put("isUserAuthorized", isUserAuthorized);
        return new ModelAndView("/recipe/update");
    }

    @PostMapping("recipe/page/{id}/update")
    public RedirectView updateRecipe(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId,
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "select[]") String[] ingredientTypeNames,
            @RequestParam(value = "counter[]") int[] ingredientAmounts) throws IOException {
        recipeService.updateRecipe(user.getId(), recipeId, title, text, file, ingredientTypeNames, ingredientAmounts);
        return new RedirectView("/recipe/page/{id}");
    }

    @PostMapping("recipe/page/{id}/comment/add")
    public RedirectView addComment(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long recipeId,
            @RequestParam String text) {
        commentService.addComment(user, recipeId, text);
        return new RedirectView("/recipe/page/{id}");
    }

    @GetMapping("recipe/page/{r_id}/comment/{c_id}/update")
    public RedirectView getCommentUpdateForm(
            @PathVariable("c_id") long commentId,
            Map<String, Object> model) {
        Comment comment = commentService.getComment(commentId);
        model.put("comment", comment);
        return new RedirectView("/recipe/page/{r_id}");
    }

    @PostMapping("recipe/page/{r_id}/comment/{c_id}/update")
    public RedirectView updateComment(
            @AuthenticationPrincipal User user,
            @PathVariable("c_id") long commentId,
            @RequestParam String text) {
        commentService.updateComment(user.getId(), commentId, text);
        return new RedirectView("/recipe/page/{r_id}");
    }

    @PostMapping("recipe/page/{r_id}/comment/{c_id}/delete")
    public RedirectView deleteComment(
            @AuthenticationPrincipal User user,
            @PathVariable("c_id") long commentId) {
        commentService.deleteComment(user.getId(), commentId);
        return new RedirectView("/recipe/page/{r_id}");
    }
}
