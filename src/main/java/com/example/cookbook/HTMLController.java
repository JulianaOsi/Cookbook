package com.example.cookbook;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.repos.RecipesRepo;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class HTMLController {
    @Autowired
    private RecipesRepo recipesRepo;

    @GetMapping("/")
    public RedirectView toIndex (){
        return new RedirectView("/index");
    }

    @GetMapping("index")
    public ModelAndView index(Map<String, Object> model){
        Iterable<Recipe> recipes = recipesRepo.findAll();
        Collections.reverse((List<?>) recipes);
        model.put("recipes", recipes);
        return new ModelAndView("index", model);
    }

    @GetMapping("recipe/add")
    public ModelAndView addRecipeWindow(){
        return new ModelAndView("recipe/add");
    }

    @PostMapping("recipe/add")
        public RedirectView addRecipe(@RequestParam String title, @RequestParam String text){
            Recipe recipe = new Recipe(title, text);
            recipesRepo.save(recipe);
            return new RedirectView("/index");
    }
}
