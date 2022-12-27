package com.example.recipeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RecipeAdapter extends FirebaseRecyclerAdapter<Recipe , RecipeAdapter.RecipesViewHolder>{

    public RecipeAdapter (@NonNull FirebaseRecyclerOptions<Recipe> options) {
        super(options); // call the constructor of the FirebaseRecyclerAdapter
    }

    @Override
    protected void onBindViewHolder(@NonNull RecipesViewHolder holder, int position, @NonNull Recipe model) { // bind the data to the view holder
        holder.mTitle.setText(model.getTitle());

    }

    @NonNull
    @Override
    public RecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // create a new view holder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_recycler_item, parent, false); // inflate the view
        return new RecipesViewHolder(view); // return the view holder
    }

    static class RecipesViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;

        public RecipesViewHolder(@NonNull View itemView) { // create a new view holder
            super(itemView);

            mTitle = itemView.findViewById(R.id.tv_item_title);
        }
    }
}
