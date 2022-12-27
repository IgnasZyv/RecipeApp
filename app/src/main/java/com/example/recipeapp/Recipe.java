package com.example.recipeapp;

public class Recipe {
    private String mTitle;

    public Recipe() {} // Needed for Firebase

    public Recipe(String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
