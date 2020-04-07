package com.example.cookbook.repo;

import com.example.cookbook.domain.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepo extends JpaRepository<Reaction, Long> {
}
