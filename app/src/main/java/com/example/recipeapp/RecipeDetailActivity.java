package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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
                            menuInflater.inflate(R.menu.detail_menu, menu); // inflate the menu
                        }

                        @Override
                        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.delete_recipe) { // if the delete recipe button is clicked
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://recipeapp-7d055-default-rtdb.europe-west1.firebasedatabase.app/")
                                        .getReference();

                                assert user != null;
                                // get the reference to the recipe in the database
                                Query query = databaseReference.child("Recipe").child(user.getUid()).child(recipeController.getId());
                                query.addListenerForSingleValueEvent(new ValueEventListener() { // add a listener for the value event
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            snapshot.getRef().removeValue(); // remove the recipe from the database
                                            changeActivity(); // change the activity
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(RecipeDetailActivity.this, "Error deleting recipe", Toast.LENGTH_SHORT).show();
                                        Log.d("menuDelete", "onCancelled: " + error.getMessage());
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

    private void changeActivity() {
            Intent intent = new Intent(this, RecipeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear the activity stack
            startActivity(intent);
    }


}
