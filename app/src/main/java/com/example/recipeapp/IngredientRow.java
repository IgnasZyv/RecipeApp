package com.example.recipeapp;

import java.io.Serializable;
import java.util.ArrayList;

public class IngredientRow implements Serializable {
    private ArrayList<String> mIngredient;
    private ArrayList<String> mQuantity;

    public IngredientRow() {}

    public IngredientRow(ArrayList<String> ingredients, ArrayList<String> quantities) {
        this.mIngredient = ingredients;
        this.mQuantity = quantities;
    }

    public ArrayList<String> getIngredient() {
        return mIngredient;
    }

    public void setIngredient(ArrayList<String> ingredient) {
        mIngredient = ingredient;
    }

    public ArrayList<String> getQuantity() {
        return mQuantity;
    }

    public void setQuantity(ArrayList<String> quantity) {
        mQuantity = quantity;
    }
}
