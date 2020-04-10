package com.example.cookbook.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany(mappedBy = "reactions")
    private List<User> users = new ArrayList<>();

    public enum ReactionType {
        LIKE("like.png"),
        DISLIKE("dislike.png"),
        DIAMOND("diamond.png"),
        HEART("heart.png");

        public final String filename;

        ReactionType(String filename) {
            this.filename = filename;
        }

        public static ReactionType getByName (String typeName) {
           return Reaction.ReactionType.valueOf(typeName.substring(0, typeName.length() - 4).toUpperCase());
        }
    }

    public Reaction() {
    }

    public Reaction(Recipe recipe, ReactionType type, int count) {
        this.recipe = recipe;
        this.type = type;
        this.count = count;
    }


    public boolean hasUser(User user){
        for (User u: users) {
            if (u.getId().equals(user.getId())) return true;
        }
        return false;
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
