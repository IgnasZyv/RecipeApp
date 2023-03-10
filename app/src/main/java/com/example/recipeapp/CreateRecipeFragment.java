package com.example.recipeapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class CreateRecipeFragment extends Fragment {

    private RecipeController mController;
    private DatabaseReference mRecipeRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private Uri mImageUri;

    private ImageView mRecipeImage;
    private EditText mEtTitle;
    private String mFilename;
    private ProgressBar mProgressBar;
    private LinearLayout mImageLayout;
    private LinearLayout mIngredientLayout;

    private ArrayList<EditText> mIngredientList;
    private ArrayList<EditText> mQuantityList;

    public CreateRecipeFragment() {
        super(R.layout.fragment_create_recipe);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIngredientList = new ArrayList<>();
        mQuantityList = new ArrayList<>();
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_recipe, container, false);

        mProgressBar = view.findViewById(R.id.progress_bar);
        mImageLayout = view.findViewById(R.id.linear_layout);
        mRecipeImage = view.findViewById(R.id.iv_recipe);
        mIngredientLayout = view.findViewById(R.id.ingredient_layout);
        ImageButton addIngredient = view.findViewById(R.id.add_ingredients);

        mEtTitle = view.findViewById(R.id.et_recipe_title);
        Button btnSubmit = view.findViewById(R.id.btn_submit);
        ImageView btnPic = view.findViewById(R.id.btn_pic);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        mRecipeRef = FirebaseDatabase.getInstance("https://recipeapp-7d055-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Recipe").child(user.getUid()); // get reference to the database for the user

        mStorage = FirebaseStorage.getInstance("gs://recipeapp-7d055.appspot.com/");
        mStorageRef = mStorage.getReference().child("Images").child(user.getUid()); // get reference to the storage for the user

        btnPic.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // if the user has already granted the permissions, then open the camera
                OpenCamera();
            } else {
                // ask for permission
//                permissions();
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        addIngredient.setOnClickListener(v -> {
            AddIngredientRow();
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmit.setEnabled(false);
                String title = mEtTitle.getText().toString();
                boolean hasPicture = mRecipeImage != null;
                Recipe recipe = new Recipe(title, mFilename, getIngredients()); // create a new recipe object
                // create a new RecipeController instance
                mController = new RecipeController(recipe);
                mRecipeRef.child(mController.getId()).setValue(mController.getRecipe()).addOnSuccessListener(new OnSuccessListener<Void>() { // push the recipe to the database
                    @Override
                    public void onSuccess(Void unused) {

                        if (mImageUri != null) {
                            // if the image is not null, then upload the image to the storage and gallery
                            saveImageToGallery(mImageUri); // save the image to the gallery
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), mImageUri);
                                uploadImageToStorage(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            uploadImageToStorage();
                        } else {
                            Toast.makeText(getContext(), "Recipe created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), RecipeActivity.class);
                            startActivity(intent); // start the recipe activity
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create recipe", Toast.LENGTH_SHORT).show());
            }
        });

        return view;
    }

    private void AddIngredientRow() {
        LinearLayout horizontalLayout = new LinearLayout(getContext()); // create a new horizontal layout
        // set the layout params
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        horizontalLayout.setGravity(Gravity.CENTER);
        horizontalLayout.setWeightSum(1);
        horizontalLayout.setPadding(0, 0, 0, 10);

        float factor = getResources().getDisplayMetrics().density; // get the density factor
        int pxWidthIngredient = (int)(198 * factor); // convert the width to pixels
        int pxHeightIngredient = (int)(48 * factor); // convert the height to pixels

        EditText etIngredient = new EditText(getContext()); // create a new edit text
        etIngredient.setId(View.generateViewId());
        etIngredient.setHint(R.string.ingredient);
        etIngredient.setLayoutParams(new LinearLayout.LayoutParams(pxWidthIngredient, pxHeightIngredient));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            etIngredient.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }
        etIngredient.setInputType(InputType.TYPE_CLASS_TEXT);
        horizontalLayout.addView(etIngredient); // add the edit text to the horizontal layout

        int pxWidthQuantity = (int)(100 * factor);
        int pxHeightQuantity = (int)(48 * factor);

        EditText etQuantity = new EditText(getContext()); // create a new edit text
        etQuantity.setId(View.generateViewId());
        etQuantity.setHint(R.string.quantity);
        etQuantity.setLayoutParams(new LinearLayout.LayoutParams(pxWidthQuantity, pxHeightQuantity));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            etIngredient.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }
        etIngredient.setInputType(InputType.TYPE_CLASS_TEXT);

        horizontalLayout.addView(etQuantity); // add the edit text to the horizontal layout
        mIngredientLayout.addView(horizontalLayout); // add the horizontal layout to the vertical layout
        mIngredientList.add(etIngredient);
        mQuantityList.add(etQuantity); // add the edit text to the list

        int pxWidthButton = (int)(48 * factor);
        int pxHeightButton = (int)(48 * factor);


        ImageButton btnDelete = new ImageButton(getContext());
        btnDelete.setId(View.generateViewId());
        btnDelete.setLayoutParams(new LinearLayout.LayoutParams(pxWidthButton, pxHeightButton));
        btnDelete.setImageResource(R.drawable.ic_button_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIngredientLayout.removeView(horizontalLayout);
                mIngredientList.remove(etIngredient);
                mQuantityList.remove(etQuantity);
            }
        });

        horizontalLayout.addView(btnDelete);

    }

    private IngredientRow getIngredients() {
        ArrayList<String> ingredients = new ArrayList<>();
        ArrayList<String> quantities = new ArrayList<>();
        for (EditText et : mIngredientList) {
            ingredients.add(et.getText().toString());
        }
        for (EditText et : mQuantityList) {
            quantities.add(et.getText().toString());
        }
        return new IngredientRow(ingredients, quantities);
    }

    private void uploadImageToStorage(Bitmap bitmap) {
        mProgressBar.setVisibility(View.VISIBLE);

        Matrix matrix = new Matrix();
        matrix.postRotate(90); // rotate the image by 90 degrees

        // create a new bitmap from the original using the matrix to get the rotated bitmap
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos); // compress the image to a JPEG with 70% quality
        byte[] data = baos.toByteArray();


        UploadTask uploadTask = mStorageRef.child(mFilename).putBytes(data); // upload the image to the storage
        uploadTask.addOnFailureListener(exception -> {
            Toast.makeText(getContext(), "Upload Failed!", Toast.LENGTH_SHORT).show();
            Log.d("ImageUpload", "uploadImageToStorage: " + exception.getMessage());
        });

        uploadTask.addOnProgressListener(taskSnapshot -> { // show the progress of the upload
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            mProgressBar.setProgress((int) progress);
        });

        uploadTask.addOnCompleteListener(task -> { // when the upload is complete, show a toast and start the recipe activity
            if (task.isSuccessful()) {
                // Upload was successful, move to the next activity
                Toast.makeText(getContext(), "Recipe created and image uploaded", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), RecipeActivity.class);
                startActivity(intent);
            } else {
                // Upload failed, show an error message
                Toast.makeText(getContext(), "Upload Failed!", Toast.LENGTH_SHORT).show();
                Log.d("ImageUpload", "uploadImageToStorage: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }


//    private void uploadImageToStorage() {
//        mRecipeImage.setDrawingCacheEnabled(true);
//        mRecipeImage.buildDrawingCache();
//        Bitmap bitmap = mRecipeImage.getDrawingCache();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        UploadTask uploadTask = mStorageRef.child(mFilename).putBytes(data);
//        uploadTask.addOnFailureListener(exception -> {
//            Toast.makeText(getContext(), "Upload Failed!", Toast.LENGTH_SHORT).show();
//            Log.d("ImageUpload", "uploadImageToStorage: " + exception.getMessage());
//        }).addOnSuccessListener(taskSnapshot -> {
//            Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
//        });
//    }

    //TODO: only save the image to the Gallery and db if the user clicks the submit button
    //TODO: add the image to the recipe object
    //TODO: change name of the image to the title of the recipe

    private void OpenCamera() {
        launchCameraResult.launch(getUri());
    }

    private Uri getUri() {
        File file = new File(requireContext().getFilesDir(), "picFromCamera");
        return FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", file);
    }

    private void createFileName() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        mFilename = mEtTitle.getText().toString() + " " + dateFormat.format(new Date());
    }

    ActivityResultLauncher<Uri> launchCameraResult = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        mImageUri = getUri();
                        createFileName();
                        Bitmap scaledImage = scaleImage(mImageUri);

                        // Set the image to the ImageView
                        mRecipeImage.setImageBitmap(scaledImage);
                        mImageLayout.setVisibility(View.VISIBLE);
                    }
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveImageToGallery(Uri imageUri) {
        // Get the file path of the image
        String filePath = imageUri.getPath();

        // Get the content resolver
        ContentResolver resolver = requireContext().getContentResolver();

        // Create a new ContentValues object
        ContentValues contentValues = new ContentValues();

        // Set the values for the ContentValues object
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, mFilename);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/recipeapp");

        // Insert the image into the gallery
        Uri newUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//        mImageUri = newUri;
        // Copy the file to the new location
        try (InputStream inputStream = resolver.openInputStream(imageUri);
             OutputStream outputStream = resolver.openOutputStream(newUri)) {
            byte[] buffer = new byte[1024];
            int len;
            // Copy the bits from InputStream to OutputStream
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            Log.d("saveToGallery", "saveImageToGallery: Failed" + e);
            Toast.makeText(requireContext(), "Could not save to gallery", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap scaleImage(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Calculate the new size for the image
        assert bitmap != null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratio = (float) width / height;
        int newWidth = 500; // Set the desired width for the image
        int newHeight = Math.round(newWidth / ratio);

        // Create a scaled version of the image
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        // Create a Matrix object
        Matrix matrix = new Matrix();

        // Rotate the image 90 degrees to the right wont work for emulator
        matrix.postRotate(90);

        // Create a rotated version of the scaled image

        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    };

    // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    OpenCamera();
                } else {
                    Toast.makeText(getContext(), "Permission Required", Toast.LENGTH_SHORT).show();
                }
            });




//
//    // Permissions for camera and storage using Dexter library
//    private void permissions() {
//        Dexter.withContext(requireContext())
//                .withPermissions( // ask for permissions
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                        ).withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        Log.d("Permissions", "onPermissionsChecked: " + "success");
//                        OpenCamera();
//
//
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest(); // ask again
//                    }
//                }).check();
//    }

//
//    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
//            new ActivityResultContracts.RequestMultiplePermissions(),
//            result -> {
//                //here we will check if permissions were now (from permission request dialog) or already granted or not
//
//                boolean allAreGranted = true;
//                for (Boolean isGranted : result.values()) {
//                    Log.d("permissions", "onActivityResult: isGranted: " + isGranted);
//                    allAreGranted = allAreGranted && isGranted;
//                }
//
//                if (allAreGranted) {
//                    //All Permissions granted now do the required task here or call the function for that
//                    OpenCamera();
//                } else {
//                    //All or some Permissions were denied so can't do the task that requires that permission
//                    Log.d("permissions", "onActivityResult: All or some permissions denied...");
//                    Toast.makeText(requireContext(), "All or some permissions denied...", Toast.LENGTH_SHORT).show();
//                }
//            }
//    );

}