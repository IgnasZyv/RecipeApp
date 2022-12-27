package com.example.recipeapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CreateRecipeActivity extends AppCompatActivity {

    public CreateRecipeActivity() {
        super(R.layout.activity_fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container, CreateRecipeFragment.class, null)
                .commit();

    }

}
