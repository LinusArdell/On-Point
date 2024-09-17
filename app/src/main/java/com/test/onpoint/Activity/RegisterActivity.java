package com.test.onpoint.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.test.onpoint.Class.UserClass;
import com.test.onpoint.R;

public class RegisterActivity extends AppCompatActivity {
    Button btn_Register;
    TextInputEditText etNama, etEmail, etPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference userDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initializeComponents();

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        progressBar.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
    }

    private void registerUser() {
        final String username = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String userPicture = "https://firebasestorage.googleapis.com/v0/b/check-point-a7308.appspot.com/o/User%20Profile%20Picture%2FDefaultPicture.jpg?alt=media&token=7a82525e-aa9a-484d-ac51-0443b404ac20";

        if (username.isEmpty()) {
            etNama.setError("Username is required");
            etNama.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etPassword.setError("Password must be at least 8 characters long");
            etPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            saveUserData(user.getUid(), username, user.getEmail(), userPicture);

                            showSuccessDialog();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Registration Successful");
        builder.setMessage("Your account has been successfully registered.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void initializeComponents(){
        btn_Register = findViewById(R.id.btnRegister);
        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void saveUserData(String userId, String username, String email, String userPicture) {
        UserClass userData = new UserClass(username, email, userPicture);
        userDatabase.child("Users").child(userId).setValue(userData);
    }
}