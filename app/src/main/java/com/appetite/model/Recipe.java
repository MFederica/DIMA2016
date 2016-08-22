package com.appetite.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DynamoDBTable(tableName = "dima-mobilehub-516910810-Recipe")

public class Recipe implements Parcelable {

    private String name;
    private String image;

    private String advice;
    private String introduction;
    private String category;
    private String country;
    private String amount;
    private String cookingTime;
    private String preparationTime;
    private String vegetarian;
    private int difficulty;
    private List<String> ingredient_name;
    private List<String> ingredient_quantity;
    private List<String> ingredient_unit;
    private List<String> step;

        public Recipe() {
        }

        public Recipe(String name, String image, String advice, String introduction, String category, String country, String amount, String cookingTime, String preparationTime,
                String vegetarian, int difficulty, List<String> ingredient_name, List<String> ingredient_unit, List<String> ingredient_quantity, List<String> step) {
            this.name = name;
            this.image = image;
            this.advice = advice;
            this.introduction = introduction;
            this.country = country;
            this.category = category;
            this.amount = amount;
            this.cookingTime = cookingTime;
            this.preparationTime = preparationTime;
            this.difficulty = difficulty;
            this.vegetarian = vegetarian;
            this.ingredient_name = ingredient_name;
            this.ingredient_quantity = ingredient_quantity;
            this.ingredient_unit = ingredient_unit;
            this.step = step;
        }

    @DynamoDBHashKey(attributeName = "Name")
    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBRangeKey(attributeName = "Difficulty")
    @DynamoDBAttribute(attributeName = "Difficulty")
    public int getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    @DynamoDBAttribute(attributeName = "Image")
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    @DynamoDBAttribute(attributeName = "Vegetarian")
    public String getVegetarian() {
        return vegetarian;
    }
    public void setVegetarian(String vegetarian) {
        this.vegetarian = vegetarian;
    }

    @DynamoDBAttribute(attributeName = "Advice")
    public String getAdvice() {
        return advice;
    }
    public void setAdvice(String advice) {
        this.advice = advice;
    }

    @DynamoDBAttribute(attributeName = "Introduction")
    public String getIntroduction() {
        return introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @DynamoDBIndexHashKey(attributeName = "Category", globalSecondaryIndexName = "Category")
    public String getCategory() {return category; }
    public void setCategory(String category) {this.category = category;}

    @DynamoDBAttribute(attributeName = "Country")
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    @DynamoDBAttribute(attributeName = "Amount")
    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    @DynamoDBAttribute(attributeName = "CookingTime")
    public String getCookingTime() {
        return cookingTime;
    }
    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    @DynamoDBAttribute(attributeName = "PreparationTime")
    public String getPreparationTime() {return preparationTime;}
    public void setPreparationTime(String preparationTime) {this.preparationTime = preparationTime;}

    @DynamoDBAttribute(attributeName = "Ingredient_name")
    public List<String> getIngredient_name() {return ingredient_name;}
    public void setIngredient_name(List<String> ingredient_name) {this.ingredient_name = ingredient_name;}

    @DynamoDBAttribute(attributeName = "Ingredient_quantity")
    public List<String> getIngredient_quantity() {return ingredient_quantity;}
    public void setIngredient_quantity(List<String> ingredient_quantity) {this.ingredient_quantity = ingredient_quantity;}

    @DynamoDBAttribute(attributeName = "Ingredient_unit")
    public List<String> getIngredient_unit() {return ingredient_unit;}
    public void setIngredient_unit(List<String> ingredient_unit) {this.ingredient_unit = ingredient_unit;}

    @DynamoDBAttribute(attributeName = "Step")
    public List<String> getStep() {return step;}
    public void setStep(List<String> step) {this.step = step;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(advice);
        dest.writeString(introduction);
        dest.writeString(category);
        dest.writeString(country);
        dest.writeString(amount);
        dest.writeString(cookingTime);
        dest.writeString(preparationTime);
        dest.writeInt(difficulty);
        dest.writeString(vegetarian);
        dest.writeStringList(ingredient_name);
        dest.writeStringList(ingredient_quantity);
        dest.writeStringList(ingredient_unit);
        dest.writeStringList(step);
    }

    private Recipe(Parcel in) {
        image = in.readString();
        name = in.readString();
        advice = in.readString();
        introduction = in.readString();
        category = in.readString();
        country = in.readString();
        amount = in.readString();
        cookingTime = in.readString();
        preparationTime = in.readString();
        difficulty = in.readInt();
        vegetarian = in.readString();
        ingredient_name = new ArrayList<String>();
        ingredient_unit = new ArrayList<String>();
        ingredient_quantity = new ArrayList<String>();
        step = new ArrayList<String>();
        in.readStringList(ingredient_name);
        in.readStringList(ingredient_quantity);
        in.readStringList(ingredient_unit);
        in.readStringList(step);

    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };



}