package com.test.onpoint.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.test.onpoint.R;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    private TextInputEditText etEmail, etPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference userDatabase;
    private ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        initializeUiComponents();
        progressDialog = new ProgressDialog(this);

        isLogin();

        setOnClick();
    }

    private void initializeUiComponents(){
        btn_login = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.loginEmail);
        etPassword = findViewById(R.id.et_Password);
        progressBar = findViewById(R.id.loginProgessBar);
        progressBar.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
    }

    private void isLogin(){
        FirebaseUser user = auth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void setOnClick(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });
    }

    private void validate(String userEmail, String userPassword){

        if(userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else{
                    Toast.makeText(LoginActivity.this,"Email or Password is incorrect", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }
}