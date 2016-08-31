package com.appetite.model;

import java.io.Serializable;

public class FavoriteItem implements Serializable {
    private String recipe;
    private String recipeImage;

    public FavoriteItem(Recipe recipe) {
        this.recipe = recipe.getName();
        recipeImage = recipe.getImage();
    }

    public String getRecipe() {
        return recipe;
    }

    public String getImage() {
        return recipeImage;
    }
}
