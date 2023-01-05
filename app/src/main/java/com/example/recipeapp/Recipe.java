package com.example.recipeapp;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String mTitle;
    private String mPictureName;

    public Recipe() {} // Needed for Firebase

    public Recipe(String title, String pictureName) {
        this.mTitle = title;
        this.mPictureName = pictureName;
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

}
