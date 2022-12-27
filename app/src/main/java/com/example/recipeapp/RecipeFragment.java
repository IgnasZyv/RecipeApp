package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RecipeFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecipeAdapter mRecipeAdapter;
    ImageButton mAddRecipeButton;
    ImageButton mLogoutButton;

    public  RecipeFragment() {super(R.layout.fragment_recipe);} // call the constructor of the Fragment class

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false); // inflate the layout for this fragment

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // get the current user

        assert user != null; // if the user is null, throw an exception
        DatabaseReference recipeRef = FirebaseDatabase.getInstance("https://recipeapp-7d055-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Recipe").child(user.getUid()); // get reference to the database for the user
        recipeRef.keepSynced(true); // keep the data synced

        mRecyclerView = view.findViewById(R.id.rv_recipes);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // set the layout manager for the recycler view

        FirebaseRecyclerOptions<Recipe> options =
                new FirebaseRecyclerOptions.Builder<Recipe>()
                        .setQuery(recipeRef, Recipe.class)
                        .build(); // get all the recipes from the database

        mRecipeAdapter = new RecipeAdapter(options); // create a new adapter

        mRecyclerView.setAdapter(mRecipeAdapter); // set the adapter to the recycler view

        mAddRecipeButton = view.findViewById(R.id.ib_add_recipe);
        mAddRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateRecipeActivity.class);
                startActivity(intent); // start the create recipe activity
            }
        });

        mLogoutButton = view.findViewById(R.id.ib_sign_out);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(v.getContext()) // sign out the user
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear the activity stack
                                startActivity(intent); // start the login activity
                            }
                        });
            }
        });

        return view;
    }

    @Override public void onStart() { // start the adapter
        super.onStart();
        mRecipeAdapter.startListening(); // start listening for changes in the database
    }

    @Override public void onStop() { // stop the adapter
        super.onStop();
        mRecipeAdapter.stopListening(); // stop listening for changes in the database
    }

}
