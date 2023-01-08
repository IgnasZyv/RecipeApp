package com.example.recipeapp;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RecipeDetailFragment extends Fragment {

    LinearLayout mImageLayout;
    ImageView mImageView;
    RecipeController mRecipeController;
    LinearLayout mIngredientsLayout;

    public RecipeDetailFragment() {
        super(R.layout.fragment_recipe_detail);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        mRecipeController = (RecipeController) getArguments().getSerializable("controller");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);


        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        mImageLayout = view.findViewById(R.id.ll_recipe_image);
        mImageView = view.findViewById(R.id.iv_recipe);
        mIngredientsLayout = view.findViewById(R.id.ingredient_layout);

        if (mRecipeController.getPictureName() != null) {
            progressBar.setVisibility(View.VISIBLE);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;

            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("Images/").child(user.getUid()).child(mRecipeController.getPictureName());

            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(requireContext())
                            .load(uri)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(mImageView);
                    mImageLayout.setVisibility(View.VISIBLE);
                }
            });

//            while (!urlTask.isSuccessful()) {
//                // Wait for the task to complete
//            };
//
//            Uri downloadUrl = urlTask.getResult();
//            Glide.with(requireContext())
//                    .load(downloadUrl)
//                    .into(mImageView);
//
//            mImageLayout.setVisibility(View.VISIBLE);
        }


        getIngredients();


        return view;
    }

    private void getIngredients() {
        if (mRecipeController.getIngredients() != null) {
            IngredientRow ingredientRow = mRecipeController.getIngredients();
            for (int i = 0; i < ingredientRow.getIngredient().size(); i++) {
                String ingredient = ingredientRow.getIngredient().get(i);
                String quantity = ingredientRow.getQuantity().get(i);

                LinearLayout horizontalLayout = new LinearLayout(getContext()); // create a new horizontal layout
                // set the layout params
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                horizontalLayout.setGravity(Gravity.CENTER);
                horizontalLayout.setWeightSum(1);
                horizontalLayout.setPadding(0, 0, 0, 10);

                float factor = getResources().getDisplayMetrics().density; // get the density factor
                int pxWidth = (int)(198 * factor); // convert the width to pixels
                int pxHeight = (int)(48 * factor); // convert the height to pixels


                TextView tvIngredient = new TextView(getContext()); // create a new edit text
                tvIngredient.setId(View.generateViewId());
                tvIngredient.setLayoutParams(new LinearLayout.LayoutParams(pxWidth, pxHeight));
                tvIngredient.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tvIngredient.setText(ingredient);
                tvIngredient.setTextSize(18);
                horizontalLayout.addView(tvIngredient); // add the edit text to the horizontal layout

                int pxWidth2 = (int)(150 * factor);
                int pxHeight2 = (int)(48 * factor);

                TextView tvQuantity = new TextView(getContext()); // create a new edit text
                tvQuantity.setId(View.generateViewId());
                tvQuantity.setLayoutParams(new LinearLayout.LayoutParams(pxWidth2, pxHeight2));
                tvQuantity.setText(quantity);
                tvQuantity.setTextSize(18);
                horizontalLayout.addView(tvQuantity); // add the edit text to the horizontal layout
                mIngredientsLayout.addView(horizontalLayout); // add the horizontal layout to the vertical layout

            }
        }
    }

    private void DeleteIngredient(LinearLayout layout) {
        layout.removeAllViews();
    }









}
