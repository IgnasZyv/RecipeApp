package com.example.recipeapp;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
//                    progressBar.setVisibility(View.GONE);
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





        return view;
    }
}
