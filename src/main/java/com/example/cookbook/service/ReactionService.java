package com.example.cookbook.service;

import com.example.cookbook.domain.Reaction;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.User;
import com.example.cookbook.repo.ReactionRepo;
import com.example.cookbook.repo.RecipeRepo;
import com.example.cookbook.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ReactionService {

    @Autowired
    private RecipeRepo recipeRepo;

    @Autowired
    private ReactionRepo reactionRepo;

    @Autowired
    private UserRepo userRepo;

    @Transactional
    public void addReaction(User user, long recipeId, String reactionTypeName) {
        if (user == null) return;

        Recipe recipe = recipeRepo.getOne(recipeId);
        Reaction.ReactionType reactionType = Reaction.ReactionType.getByName(reactionTypeName);

        Reaction reaction = findReactionByTypeName(reactionTypeName, recipe.getReactions());

        if (reaction == null) {
            reaction = new Reaction(recipe, reactionType);
        }
        if (reaction.hasUser(user)) {
            reaction.decrementCount();
            reaction.getUsers().removeIf(u -> u.getId().equals(user.getId()));
        } else {
            reaction.incrementCount();
            reaction.getUsers().add(user);
        }
        if (reaction.getCount() == 0) {
            reactionRepo.delete(reaction);
            return;
        }

        reactionRepo.save(reaction);
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

    public List<Reaction> getRecipeReactions(Recipe recipe) {
        return reactionRepo.findByRecipeOrderByCountDesc(recipe);
    }
}
