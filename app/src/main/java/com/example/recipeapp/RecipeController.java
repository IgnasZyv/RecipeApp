package com.example.recipeapp;

import java.io.Serializable;

public class RecipeController implements Serializable {

    private final Recipe mRecipe;

    public RecipeController(Recipe recipe) {
        mRecipe = recipe;
    }

    public Recipe getRecipe() {
        return mRecipe;
    }

    public String getPictureName() {
        return mRecipe.getPictureName();
    }

    public void setPictureName(String pictureName) {
        mRecipe.setPictureName(pictureName);
    }

    public String getTitle() {
        return mRecipe.getTitle();
    }

    public void setTitle(String title) {
        mRecipe.setTitle(title);
    }
}
