package com.appetite.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable {

    private String name;
    private String image;

    private String advice;
    private String introduction;
    private String category;
    private String country;
    private int amount;
    private int cookingTime;
    private int preparationTime;
    private int difficoulty;
    private int vegetarian;


        public Recipe() {
        }

        public Recipe(String name, String image, String advice, String introduction, String category, int amount, int cookingTime, int preparationTime,
                      int difficoulty, int vegetarian) {
            this.name = name;
            this.image = image;
            this.advice = advice;
            this.introduction = introduction;
            this.category = category;
            this.amount = amount;
            this.cookingTime = cookingTime;
            this.preparationTime = preparationTime;
            this.difficoulty = difficoulty;
            this.vegetarian = vegetarian;
        }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(int vegetarian) {
        this.vegetarian = vegetarian;
    }

    public int getDifficoulty() {
        return difficoulty;
    }

    public void setDifficoulty(int difficoulty) {
        this.difficoulty = difficoulty;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

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
        dest.writeInt(amount);
        dest.writeInt(cookingTime);
        dest.writeInt(preparationTime);
        dest.writeInt(difficoulty);
        dest.writeInt(vegetarian);
    }

    private Recipe(Parcel in) {
        image = in.readString();
        name = in.readString();
        advice = in.readString();
        introduction = in.readString();
        category = in.readString();
        amount = in.readInt();
        cookingTime = in.readInt();
        preparationTime = in.readInt();
        difficoulty = in.readInt();
        vegetarian = in.readInt();

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