package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

        public RecipeDetailActivity() {
            super(R.layout.activity_fragment);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent intent = getIntent();
            Bundle extras = intent.getExtras();

            if (extras != null) {
                Recipe model = (Recipe) getIntent().getSerializableExtra("recipe");

                if (model != null) {
                    RecipeController recipeController = new RecipeController(model);
                    setTitle(recipeController.getTitle());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("controller", recipeController);

                    RecipeDetailFragment recipeDetail = new RecipeDetailFragment();
                    recipeDetail.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, recipeDetail)
                            .commit();
                }
            }
        }


}
