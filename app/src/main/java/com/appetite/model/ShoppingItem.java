package com.appetite.model;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingItem implements Serializable {

    //private Recipe recipe;
    //private List<RecipeIngredient> ingredientsList;
    private String recipe;
    private String recipeImage;
    private int servings;
    private Map<RecipeIngredient, Boolean> ingredientsMapping;
    private List<RecipeIngredient> ingredientsList;

    public ShoppingItem(Recipe recipe, int servings) {
        this.recipe = recipe.getName();
        this.recipeImage = recipe.getImage();
        this.servings = servings;
        ingredientsMapping = new HashMap<>();
        ingredientsList = new ArrayList<>();
        for (int i = 0; i < recipe.getIngredient_name().size(); i++) {
            RecipeIngredient ri = new RecipeIngredient(recipe.getIngredient_name().get(i), recipe.getIngredient_quantity().get(i), recipe.getIngredient_unit().get(i));
            this.ingredientsMapping.put(ri, false);
            this.ingredientsList.add(ri);
        }
    }
/*
    public Recipe getRecipe() {
        return recipe;
    }

    public List<RecipeIngredient> getIngredientsList() {
        return ingredientsList;
    } */

    public String getRecipe() {
        return recipe;
    }

    public String getImage() {
        return recipeImage;
    }

    /**
     * list of ingredients of the recipe.
     *
     * @return name of the ingredient and true (if already bought) or false (if we still have o buy it)
     */
    public Map<RecipeIngredient, Boolean> getIngredientsMapping() {
        return ingredientsMapping;
    }

    public List<RecipeIngredient> getIngredientsList() {
        return ingredientsList;
    }

    public int getServings(){
        return servings;
    }
}
