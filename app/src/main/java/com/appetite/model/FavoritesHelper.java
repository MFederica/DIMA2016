package com.appetite.model;

import android.content.Context;

import com.appetite.activity.ActivityMain;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FavoritesHelper implements Serializable {

    public List<FavoriteItem> favoritesList;

    private volatile static FavoritesHelper instance;

    public FavoritesHelper(List<FavoriteItem> recipesList, Context context) {
        this.favoritesList = recipesList;
    }

    /**
     * Returns singleton class instance
     * @param context
     * @return singleton class instance
     */
    public static FavoritesHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (FavoritesHelper.class) {
                if (instance == null) {
                    FileInputStream fis;
                    ObjectInputStream is;
                    try {
                        fis = context.openFileInput(ActivityMain.fileFavoritesName);
                        is = new ObjectInputStream(fis);
                        instance = (FavoritesHelper) is.readObject();
                        is.close();
                        fis.close();
                    } catch (IOException e) {
                        instance = new FavoritesHelper(new ArrayList<FavoriteItem>(), context);
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
    public static void saveFavorites(Context context) {
        FileOutputStream fos;
        ObjectOutputStream os;
        try {
            fos = context.openFileOutput(ActivityMain.fileFavoritesName, Context.MODE_PRIVATE);
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
    public boolean isInFavorites(Recipe recipe) {
        for(FavoriteItem fi : favoritesList) {
            if(fi.getRecipe().equals(recipe.getName()))
                return true;
        }
        return false;
    }

    /**
     * It's required to check if this method returns null (i.e. the recipe is not in the favorites list)
     * @param recipe
     * @return name of the recipe, if found; null, otherwise
     */
    public String getFavorite(Recipe recipe) {
        for(FavoriteItem fi : favoritesList) {
            if(fi.getRecipe().equals(recipe.getName()))
                return fi.getRecipe();
        }
        return null;
    }

    public boolean addRecipe(Recipe recipe) {
        return favoritesList.add(new FavoriteItem(recipe));
    }

    public boolean removeRecipe(String recipeName) {
        for(FavoriteItem fi : favoritesList) {
            if(fi.getRecipe().equals(recipeName)) {
                favoritesList.remove(fi);
                return true;
            }
        }
        //if i'm here it's because I haven't found the recipe in the list
        return false;
    }

    /**
     * Add or remove a recipe from the list of favorite recipes
     * @param recipe
     * @return true, if the recipe is now in favoritesList; false, if the recipe is no longer in favoritesList
     */
    public boolean favoriteChecked(Recipe recipe) {
        for(FavoriteItem fi : favoritesList) {
            if(fi.getRecipe().equals(recipe.getName())) {
                favoritesList.remove(fi);
                return false;
            }
        }
        //if i'm here it's because I haven't found the recipe in the list
        favoritesList.add(new FavoriteItem(recipe));
        return true;
    }
}
