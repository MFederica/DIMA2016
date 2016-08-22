package com.appetite.model;

import java.io.Serializable;
import java.util.List;

public class RecipeIngredient implements Serializable {

    private String name;
    private String quantity;
    private String unit;

    public RecipeIngredient(String name, String quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }
}
