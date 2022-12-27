package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateRecipeFragment extends Fragment {
    private DatabaseReference mRecipeRef;
    Button mBtnSubmit;
    EditText mEtTitle;


    public CreateRecipeFragment() {
        super(R.layout.fragment_create_recipe);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_recipe, container, false);

        mEtTitle = view.findViewById(R.id.et_recipe_title);
        mBtnSubmit = view.findViewById(R.id.btn_submit);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        mRecipeRef = FirebaseDatabase.getInstance("https://recipeapp-7d055-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Recipe").child(user.getUid()); // get reference to the database for the user

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mEtTitle.getText().toString();
                Recipe recipe = new Recipe(title); // create a new recipe object
                mRecipeRef.push().setValue(recipe).addOnSuccessListener(new OnSuccessListener<Void>() { // push the recipe to the database
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Recipe created", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), RecipeActivity.class);
                        startActivity(intent); // start the recipe activity
                    }
                });
            }
        });
        return view;
    }
}
