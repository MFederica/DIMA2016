package com.appetite.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShoppingItem implements Serializable{

    //private Recipe recipe;
    //private List<RecipeIngredient> ingredientsList;
    private String recipe;
    private List<String> ingredientsList;

    public ShoppingItem(Recipe recipe, List<RecipeIngredient> ingredientsList ) {
        this.recipe = recipe.getName();
        List<String> list = new ArrayList<>();
        for(RecipeIngredient ri : ingredientsList) {
            list.add(ri.getName());
        }
        this.ingredientsList = list;
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

    public List<String> getIngredientsList() {
        return ingredientsList;
    }
}
