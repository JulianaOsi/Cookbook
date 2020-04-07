package com.example.cookbook.domain;

import javax.persistence.*;

@Entity
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private ReactionType type;

    private int count;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public enum ReactionType {
        LIKE("like.png"),
        DISLIKE("dislike.png"),
        DIAMOND("diamond.png"),
        HEART("heart.png");

        public final String filename;

        ReactionType(String filename) {
            this.filename = filename;
        }
    }

    public Reaction() {
    }

    public Reaction(Recipe recipe, ReactionType type, int count) {
        this.recipe = recipe;
        this.type = type;
        this.count = count;
    }


    public void incrementCount() {
        count++;
    }

    public Reaction.ReactionType[] getAllReactions() {
        return Reaction.ReactionType.values();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReactionType getType() {
        return type;
    }

    public void setType(ReactionType type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
