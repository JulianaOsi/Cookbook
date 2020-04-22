package com.example.cookbook.service;

import com.example.cookbook.domain.Comment;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.Role;
import com.example.cookbook.domain.User;
import com.example.cookbook.repo.CommentRepo;
import com.example.cookbook.repo.RecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private RecipeRepo recipeRepo;

    public void addComment(User user, long recipeId, String text) {
        if (user == null) return;
        Recipe recipe = recipeRepo.getOne(recipeId);
        Comment comment = new Comment(user, recipe, text);
        commentRepo.save(comment);
    }

    public void updateComment(User user, long commentId, String text) {
        if (isAuthorOrAdmin(user, commentId)) {
            Comment comment = commentRepo.getOne(commentId);
            comment.setText(text);
            commentRepo.save(comment);
        }
    }

    public void deleteComment(User user, long commentId) {
        if (isAuthorOrAdmin(user, commentId)) {
            Comment comment = commentRepo.getOne(commentId);
            commentRepo.delete(comment);
        }
    }

    public Comment getComment(long commentId) {
        return commentRepo.getOne(commentId);
    }

    public boolean isAuthorOrAdmin(User user, long commentId) {
        return commentRepo
                .getOne(commentId)
                .getAuthor()
                .getId().equals(user.getId())
                || user.getRoles().contains(Role.ADMIN);
    }

    public List<Comment> getRecipeComments(Recipe recipe){
        return commentRepo.findByRecipeOrderByTime(recipe);
    }
}
