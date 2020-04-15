package com.example.cookbook.repo;

import com.example.cookbook.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepo extends JpaRepository<Recipe, Long> {
}
