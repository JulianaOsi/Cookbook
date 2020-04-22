package com.example.cookbook.repo;

import com.example.cookbook.domain.Reaction;
import com.example.cookbook.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReactionRepo extends JpaRepository<Reaction, Long> {
    List<Reaction> findByRecipeOrderByCountDesc(Recipe recipe);
}
