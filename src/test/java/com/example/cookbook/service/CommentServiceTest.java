package com.example.cookbook.service;

import com.example.cookbook.domain.Comment;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.User;
import com.example.cookbook.repo.CommentRepo;
import com.example.cookbook.repo.RecipeRepo;
import com.example.cookbook.service.exception.NotAuthorizedException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class CommentServiceTest {
    @Autowired
    CommentService commentService;

    @MockBean
    private CommentRepo commentRepo;

    @MockBean
    private RecipeRepo recipeRepo;

    @Mock
    private User user;

    @Test
    void addCommentShouldThrowNotAuthorizedWhenUserNull() throws NotAuthorizedException {
        Assertions.assertThrows(
                NotAuthorizedException.class,
                () -> commentService.addComment(null, 0, "")
        );
    }

    @Test
    void addCommentShouldCompleteSuccessfully() throws NotAuthorizedException {
        final String text = "comment text";
        final Recipe recipe = Mockito.mock(Recipe.class);
        Mockito.when(recipeRepo.getOne(recipe.getId())).thenReturn(recipe);

        commentService.addComment(user, recipe.getId(), text);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        Mockito.verify(commentRepo, Mockito.times(1)).save(captor.capture());
        Comment comment = captor.getValue();
        Assert.assertEquals(text, comment.getText());
        Assert.assertEquals(recipe, comment.getRecipe());
        Assert.assertEquals(user, comment.getAuthor());
    }
}
