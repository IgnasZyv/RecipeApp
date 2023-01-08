package com.example.recipeapp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Recipe implements Serializable {
    private String mId;
    private String mTitle;
    private String mPictureName;
    private IngredientRow mIngredients;
    private Date mDate;

    public Recipe() {} // Needed for Firebase

    public Recipe(String title, String pictureName, IngredientRow ingredients) {
        this.mTitle = title;
        this.mPictureName = pictureName;
        this.mIngredients = ingredients;
        this.mId = UUID.randomUUID().toString();

        Calendar calendar = Calendar.getInstance();
        this.mDate = calendar.getTime();
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

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }
}
