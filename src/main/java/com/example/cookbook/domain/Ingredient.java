package com.example.cookbook.domain;

import javax.persistence.*;

@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private IngredientType type;

    private int amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public Ingredient() {
    }

    public Ingredient(Recipe recipe, IngredientType type, int amount) {
        this.recipe = recipe;
        this.type = type;
        this.amount = amount;
    }

    public enum IngredientType {

        EGG("egg"),
        CARROT("carrot"),
        POTATO("potato"),
        TOMATO("tomato");

        private final String name;

        IngredientType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IngredientType getType() {
        return type;
    }

    public void setType(IngredientType type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
