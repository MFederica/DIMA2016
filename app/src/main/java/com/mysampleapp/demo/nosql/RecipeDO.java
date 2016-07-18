package com.mysampleapp.demo.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "dima-mobilehub-516910810-Recipe")

public class RecipeDO {
    private String _name;
    private Double _difficulty;
    private String _advice;
    private Double _amount;
    private Double _cookingTime;
    private String _country;
    private String _introduction;
    private Double _preparationTime;
    private String _type;
    private Boolean _vegetarian;

    @DynamoDBHashKey(attributeName = "Name")
    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return _name;
    }

    public void setName(final String _name) {
        this._name = _name;
    }
    @DynamoDBRangeKey(attributeName = "Difficulty")
    @DynamoDBAttribute(attributeName = "Difficulty")
    public Double getDifficulty() {
        return _difficulty;
    }

    public void setDifficulty(final Double _difficulty) {
        this._difficulty = _difficulty;
    }
    @DynamoDBAttribute(attributeName = "Advice")
    public String getAdvice() {
        return _advice;
    }

    public void setAdvice(final String _advice) {
        this._advice = _advice;
    }
    @DynamoDBAttribute(attributeName = "Amount")
    public Double getAmount() {
        return _amount;
    }

    public void setAmount(final Double _amount) {
        this._amount = _amount;
    }
    @DynamoDBAttribute(attributeName = "CookingTime")
    public Double getCookingTime() {
        return _cookingTime;
    }

    public void setCookingTime(final Double _cookingTime) {
        this._cookingTime = _cookingTime;
    }
    @DynamoDBAttribute(attributeName = "Country")
    public String getCountry() {
        return _country;
    }

    public void setCountry(final String _country) {
        this._country = _country;
    }
    @DynamoDBAttribute(attributeName = "Introduction")
    public String getIntroduction() {
        return _introduction;
    }

    public void setIntroduction(final String _introduction) {
        this._introduction = _introduction;
    }
    @DynamoDBAttribute(attributeName = "PreparationTime")
    public Double getPreparationTime() {
        return _preparationTime;
    }

    public void setPreparationTime(final Double _preparationTime) {
        this._preparationTime = _preparationTime;
    }
    @DynamoDBAttribute(attributeName = "Type")
    public String getType() {
        return _type;
    }

    public void setType(final String _type) {
        this._type = _type;
    }
    @DynamoDBAttribute(attributeName = "Vegetarian")
    public Boolean getVegetarian() {
        return _vegetarian;
    }

    public void setVegetarian(final Boolean _vegetarian) {
        this._vegetarian = _vegetarian;
    }

}
