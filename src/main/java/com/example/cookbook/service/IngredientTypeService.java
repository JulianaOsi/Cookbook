package com.example.cookbook.service;

import com.example.cookbook.domain.IngredientType;
import com.example.cookbook.repo.IngredientTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class IngredientTypeService {
    @Autowired
    private IngredientTypeRepo ingredientTypeRepo;

    public List<IngredientType> getIngredientTypes() {
        return ingredientTypeRepo.findAll();
    }
}
