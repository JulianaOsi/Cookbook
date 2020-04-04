package com.example.cookbook.repo;

import com.example.cookbook.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipesRepo extends JpaRepository<Recipe, Long> {
}
