package com.example.cookbook.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public final class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int amount;

    private Unit unit;
    public enum Unit{
        GRAM("гр"),
        KILOGRAM("кг"),
        LITER("л"),
        MILLILITER("мл"),
        GLASS("ст"),
        TEASPOON("ч.л."),
        TABLESPOON("ст.л."),
        PIECE("шт"),
        ;

        String name;
        Unit(String name) {
            this.name = name;
        }
        String getName() {
            return name;
        }

        public static List<String> getNames(){
            final List<String> names = new ArrayList<>();
            for (Unit value: Unit.values()) {
                names.add(value.getName());
            }
            return names;
        }
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    private IngredientType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public Ingredient() {
    }

    public Ingredient(Recipe recipe, IngredientType type, int amount, String unit) {
        this.recipe = recipe;
        this.type = type;
        this.amount = amount;
        this.unit = Unit.valueOf(unit);
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
