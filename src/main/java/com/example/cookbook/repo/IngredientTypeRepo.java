package com.example.cookbook.repo;

import com.example.cookbook.domain.IngredientType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientTypeRepo extends JpaRepository<IngredientType, Long> {
    IngredientType findByName (String name);
}
