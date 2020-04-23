package com.example.cookbook.repo;

import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeRepo extends JpaRepository<Recipe, Long> {
    @Query(
            "select r from Recipe r where r.id not in " +
            "(select r.id from Recipe r join Ingredient i on r.id=i.recipe.id where " +
            "(i.type.id not in (?1)))")
    List<Recipe> findByIngredientTypesId(List<Long> ingredientTypesId);

    List<Recipe> findByAuthor(User author);
}
