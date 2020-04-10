package com.example.cookbook.service;

import com.example.cookbook.domain.Reaction;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.User;
import com.example.cookbook.repo.ReactionRepo;
import com.example.cookbook.repo.RecipeRepo;
import com.example.cookbook.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReactionService {

    @Autowired
    private RecipeRepo recipeRepo;

    @Autowired
    private ReactionRepo reactionRepo;

    @Autowired
    private UserRepo userRepo;

    public void addReaction(User user, long recipeId, String reactionTypeName) {
        if (!canAddReaction(user, recipeId, reactionTypeName)) return;

        Recipe recipe = recipeRepo.getOne(recipeId);
        Reaction.ReactionType reactionType = Reaction.ReactionType.getByName(reactionTypeName);

        Reaction reaction = findReactionByTypeName(reactionTypeName, recipe.getReactions());

        if (reaction != null) {
            reaction.incrementCount();
        } else {
            reaction = new Reaction(recipe, reactionType, 1);
        }

        //TODO
        user.getReactions().clear();
        user.getReactions().add(reaction);
        reactionRepo.save(reaction);
        userRepo.save(user);
    }

    public boolean canAddReaction(User user, long recipeId, String reactionTypeName) {
        if (user == null) return false;
        Recipe recipe = recipeRepo.getOne(recipeId);
        Reaction reaction = findReactionByTypeName(reactionTypeName, recipe.getReactions());
        return reaction == null || !reaction.hasUser(user);
    }

    private Reaction findReactionByTypeName(String reactionTypeName, List<Reaction> reactions) {
        Reaction.ReactionType reactionType = Reaction.ReactionType.getByName(reactionTypeName);

        for (Reaction reaction : reactions) {
            if (reaction.getType() == reactionType) {
                return reaction;
            }
        }
        return null;
    }

    public void deleteReactions(List<Reaction> reactions) {
        //TODO
    }
}
