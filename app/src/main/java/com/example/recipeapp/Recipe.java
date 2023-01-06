package com.example.recipeapp;

import java.io.Serializable;
import java.util.UUID;

public class Recipe implements Serializable {
    private String mId;
    private String mTitle;
    private String mPictureName;
    private IngredientRow mIngredients;

    public Recipe() {} // Needed for Firebase

    public Recipe(String title, String pictureName, IngredientRow ingredients) {
        this.mTitle = title;
        this.mPictureName = pictureName;
        this.mIngredients = ingredients;
        this.mId = UUID.randomUUID().toString();
    }

    public String getPictureName() {
        return mPictureName;
    }

    public void setPictureName(String pictureName) {
        mPictureName = pictureName;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public IngredientRow getIngredients() {
        return mIngredients;
    }

    public void setIngredients(IngredientRow ingredients) {
        mIngredients = ingredients;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
