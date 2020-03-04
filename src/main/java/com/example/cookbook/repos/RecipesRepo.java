package com.example.cookbook.repos;

import com.example.cookbook.domain.Recipe;
import org.springframework.data.repository.CrudRepository;

public interface RecipesRepo extends CrudRepository<Recipe, Long> {
}
