package com.appetite.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

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
    private int difficulty;
    private String vegetarian;


        public Recipe() {
        }

        public Recipe(String name, String image, String advice, String introduction, String category, String amount, String cookingTime, String preparationTime,
                      int difficulty, String vegetarian) {
            this.name = name;
            this.image = image;
            this.advice = advice;
            this.introduction = introduction;
            this.category = category;
            this.amount = amount;
            this.cookingTime = cookingTime;
            this.preparationTime = preparationTime;
            this.difficulty = difficulty;
            this.vegetarian = vegetarian;
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
    public String getPreparationTime() {
        return preparationTime;
    }
    public void setPreparationTime(String preparationTime) {this.preparationTime = preparationTime;}

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
        dest.writeString(amount);
        dest.writeString(cookingTime);
        dest.writeString(preparationTime);
        dest.writeInt(difficulty);
        dest.writeString(vegetarian);
    }

    private Recipe(Parcel in) {
        image = in.readString();
        name = in.readString();
        advice = in.readString();
        introduction = in.readString();
        category = in.readString();
        amount = in.readString();
        cookingTime = in.readString();
        preparationTime = in.readString();
        difficulty = in.readInt();
        vegetarian = in.readString();

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