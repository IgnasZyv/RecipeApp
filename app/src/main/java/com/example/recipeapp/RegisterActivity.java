package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;

    private EditText mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_password);
        Button submitButton = findViewById(R.id.btn_submit);

        submitButton.setOnClickListener(v -> { // register the user
            String email = mEmail.getText().toString(); // get the email
            String password = mPassword.getText().toString(); // get the password
            mAuth.createUserWithEmailAndPassword(email, password) // create the user with email and password in firebase
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "createUserWithEmail:success");
                            Intent intent = new Intent(this, RecipeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear the back stack
                            startActivity(intent); // go to the recipe activity
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
