package com.appetite.model;

import android.content.Context;
import android.os.AsyncTask;

import com.appetite.ActivityMain;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListHelper implements Serializable {

    public List<ShoppingItem> shoppingList;

    private volatile static ShoppingListHelper instance;

    public ShoppingListHelper(List<ShoppingItem> shoppingList, Context context) {
        this.shoppingList = shoppingList;
    }

    /**
     * Returns singleton class instance
     * @param context
     * @return singleton class instance
     */
    public static ShoppingListHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (ShoppingListHelper.class) {
                if (instance == null) {
                    FileInputStream fis;
                    ObjectInputStream is;
                    try {
                        fis = context.openFileInput(ActivityMain.fileShoppingListName);
                        is = new ObjectInputStream(fis);
                        instance = (ShoppingListHelper) is.readObject();
                        is.close();
                        fis.close();
                    } catch (IOException e) {
                        instance = new ShoppingListHelper(new ArrayList<ShoppingItem>(), context);
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return instance;
    }

    /**
     * Serializes the object and saves it in the internal storage
     * @param context
     */
    public static void saveShoppingList(Context context) {
        FileOutputStream fos;
        ObjectOutputStream os;
        try {
            fos = context.openFileOutput(ActivityMain.fileShoppingListName, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(instance);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the recipe is in the shopping list
     * @param recipe
     * @return true, if the recipe is in the shopping list; false, otherwise
     */
    public boolean isInShoppingList(Recipe recipe) {
        for(ShoppingItem si : shoppingList) {
            if(si.getRecipe().equals(recipe.getName()))
                return true;
        }
        return false;
    }

    /**
     * It's required to check if this method returns null (i.e. the recipe is not in the shopping list)
     * @param recipe
     * @return ShoppingItem, if found; null, otherwise
     */
    public ShoppingItem getShoppingItem(Recipe recipe) {
        for(ShoppingItem si : shoppingList) {
            if(si.getRecipe().equals(recipe.getName()))
                return si;
        }
        return null;
    }

    /**
     * Add an ingredient and its recipe (if they are not already in the shopping list)
     * @param recipe
     * @param recipeIngredient
     * @return true, if it has been added; false, if it's already in the shopping list
     */
    public boolean addIngredient(Recipe recipe, RecipeIngredient recipeIngredient) {
        for(ShoppingItem si : shoppingList) {
            if(si.getRecipe().equals(recipe.getName())) {
                if(!(si.getIngredientsList().contains(recipeIngredient.getName()))) {
                    //if i'm here it's because I found the recipe in the list but not the ingredient
                    si.getIngredientsList().add(recipeIngredient.getName());
                    return true;
                } else {
                    //if i'm here it's because I found the recipe in the list and the ingredient
                    return false;
                }
            }
        }
        //if i'm here it's because I haven't found the recipe in the list, so we add it
        shoppingList.add(createShoppingItem(recipe, recipeIngredient));
        return true;
    }

    /**
     * Removes an ingredient from the shopping list, if present
     * @param recipe
     * @param recipeIngredient
     * @return true, if it has been removed; false, otherwise
     */
    public boolean removeIngredient(Recipe recipe, RecipeIngredient recipeIngredient) {
        for(ShoppingItem si : shoppingList) {
            if(si.getRecipe().equals(recipe.getName())) {
                si.getIngredientsList().remove(recipeIngredient.getName());
                if(si.getIngredientsList().size() == 0)
                    shoppingList.remove(si); //TODO checkare se crea casini!!
                return true;
            }
        }
        //if i'm here it's because I haven't found the recipeIngredient in the list
        return false;
    }

    /**
     * Helper method that creates a shopping item
     * @param recipe
     * @param recipeIngredient
     * @return
     */
    private ShoppingItem createShoppingItem(Recipe recipe, RecipeIngredient recipeIngredient) {
        List ingredientsList = new ArrayList();
        ingredientsList.add(recipeIngredient);
        return new ShoppingItem(recipe, ingredientsList);
    }
}
