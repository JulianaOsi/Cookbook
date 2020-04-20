package com.example.cookbook.repo;

import com.example.cookbook.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeRepo extends JpaRepository<Recipe, Long> {
    @Query("select r from Recipe r join Ingredient i on r.id=i.recipe.id where i.type.id in (?1) group by r.id")
    List<Recipe> findByIngredientTypesId(List<Long> ingredientTypesId);
}
