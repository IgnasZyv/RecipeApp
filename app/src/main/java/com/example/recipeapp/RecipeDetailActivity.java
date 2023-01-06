package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RecipeDetailActivity extends AppCompatActivity {

        public RecipeDetailActivity() {
            super(R.layout.activity_fragment);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent intent = getIntent(); // get the intent that started the activity
            Bundle extras = intent.getExtras(); // get the extras from the intent



            if (extras != null) {
                Recipe model = (Recipe) getIntent().getSerializableExtra("recipe"); // get the recipe from the intent

                if (model != null) {
                    RecipeController recipeController = new RecipeController(model); // create a new recipe controller

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // get the current user
                    addMenuProvider(new MenuProvider() {
                        @Override
                        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                            menuInflater.inflate(R.menu.detail_menu, menu);
                        }

                        @Override
                        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.delete_recipe) {
                                // delete the recipe
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://recipeapp-7d055-default-rtdb.europe-west1.firebasedatabase.app/")
                                        .getReference("Recipe")
                                        .child(user.getUid());

                                Query query = databaseReference.child("id").equalTo(recipeController.getId());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                return true;
                            }
                            return false;
                        }
                    });


                    setTitle(recipeController.getTitle()); // set the title of the activity
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("controller", recipeController); // put the recipe controller in the bundle

                    RecipeDetailFragment recipeDetail = new RecipeDetailFragment();
                    recipeDetail.setArguments(bundle); // set the bundle as the arguments of the fragment

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, recipeDetail)
                            .commit();
                }
            }
        }


}
