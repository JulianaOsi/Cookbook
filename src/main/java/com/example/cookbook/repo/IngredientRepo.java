package com.example.cookbook.repo;

import com.example.cookbook.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepo extends JpaRepository<Ingredient, Long> {

}
