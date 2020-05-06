package com.example.cookbook.service;

import com.example.cookbook.domain.Comment;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.Role;
import com.example.cookbook.domain.User;
import com.example.cookbook.repo.CommentRepo;
import com.example.cookbook.repo.RecipeRepo;
import com.example.cookbook.service.exception.NoAccessException;
import com.example.cookbook.service.exception.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class CommentServiceTest {
    @Autowired
    CommentService commentService;

    @MockBean
    private CommentRepo commentRepo;

    @MockBean
    private RecipeRepo recipeRepo;

    @Mock
    private User author;

    @Mock
    private Recipe recipe;

    @Mock
    private Comment comment;

    @Captor
    private ArgumentCaptor<Comment> captor;

    @BeforeEach
    public void beforeTests() {
        Mockito.when(recipeRepo.getOne(recipe.getId())).thenReturn(recipe);
        Mockito.when(commentRepo.getOne(comment.getId())).thenReturn(comment);
        Mockito.when(comment.getId()).thenReturn(0L);
        Mockito.when(comment.getAuthor()).thenReturn(author);
    }

    @Test
    void shouldThrowNotAuthorized_WhenUserNull() throws NotAuthorizedException {
        assertThatExceptionOfType(NotAuthorizedException.class).isThrownBy(
                () -> commentService.addComment(null, 0, "")
        );

        assertThatExceptionOfType(NotAuthorizedException.class).isThrownBy(
                () -> commentService.updateComment(null, 0, "")
        );

        assertThatExceptionOfType(NotAuthorizedException.class).isThrownBy(
                () -> commentService.deleteComment(null, 0)
        );

        Mockito.verify(commentRepo, Mockito.times(0))
                .save(ArgumentMatchers.any(Comment.class));
    }

    @Test
    void addComment_ShouldCompleteSuccessfully() throws NotAuthorizedException {
        final String text = "comment text";
        commentService.addComment(author, recipe.getId(), text);

        Mockito.verify(commentRepo).save(captor.capture());
        final Comment comment = captor.getValue();
        assertThat(comment)
                .extracting("author", "recipe", "text")
                .contains(author, recipe, text);
    }

    @Test
    void updateComment_ShouldCompleteSuccessfully_WhenUserIsAuthor() throws NotAuthorizedException, NoAccessException {
        checkUpdateCommentWhenUserIs(author);
    }

    @Test
    void updateComment_ShouldCompleteSuccessfully_WhenUserIsAdmin() throws NotAuthorizedException, NoAccessException {
        final User admin = Mockito.mock(User.class);
        Mockito.when(admin.getRoles()).thenReturn(Collections.singleton(Role.ADMIN));

        checkUpdateCommentWhenUserIs(admin);
    }

    @Test
    void updateComment_ShouldThrowNoAccess_WhenUserIsNotAuthorOrAdmin() {
        final User otherUser = Mockito.mock(User.class);
        Mockito.when(otherUser.getId()).thenReturn(-1L);

        assertThatExceptionOfType(NoAccessException.class).isThrownBy(
                () -> checkUpdateCommentWhenUserIs(otherUser)
        );
        Mockito.verify(commentRepo, Mockito.times(0))
                .save(ArgumentMatchers.any(Comment.class));
    }

    void checkUpdateCommentWhenUserIs(User user) throws NotAuthorizedException, NoAccessException {
        final String text = "new text";
        commentService.updateComment(user, comment.getId(), text);

        Mockito.verify(commentRepo).save(comment);
        Mockito.verify(comment).setText(text);
    }

    @Test
    void deleteComment_ShouldThrowNoAccess_WhenUserIsNotAuthorOrAdmin() throws NotAuthorizedException, NoAccessException {
        checkDeleteCommentWhenUserIs(author);
    }

    @Test
    void deleteComment_ShouldCompleteSuccessfully_WhenUserIsAdmin() throws NotAuthorizedException, NoAccessException {
        final User admin = Mockito.mock(User.class);
        Mockito.when(admin.getRoles()).thenReturn(Collections.singleton(Role.ADMIN));
        checkDeleteCommentWhenUserIs(admin);
    }

    @Test
    void deleteComment_ShouldNotUpdate_WhenUserIsNotAuthorOrAdmin() {
        final User otherUser = Mockito.mock(User.class);
        Mockito.when(otherUser.getId()).thenReturn(-1L);

        assertThatExceptionOfType(NoAccessException.class).isThrownBy(
                () -> checkDeleteCommentWhenUserIs(otherUser)
        );
        Mockito.verify(commentRepo, Mockito.times(0))
                .delete(ArgumentMatchers.any(Comment.class));
    }

    void checkDeleteCommentWhenUserIs(User user) throws NotAuthorizedException, NoAccessException {
        commentService.deleteComment(user, comment.getId());

        Mockito.verify(commentRepo).delete(comment);
    }

    @Test
    void getComment_ShouldCompleteSuccessfully() {
        final Comment result = commentService.getComment(comment.getId());

        Mockito.verify(commentRepo).getOne(comment.getId());
        assertThat(result).isSameAs(comment);
    }
}
