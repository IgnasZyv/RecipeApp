package com.example.recipeapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeActivity extends AppCompatActivity {

    public RecipeActivity() {
        super(R.layout.activity_fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction() // add the fragment to the activity
                .setReorderingAllowed(true)
                .add(R.id.fragment_container, RecipeFragment.class, null)
                .commit();
    }
}