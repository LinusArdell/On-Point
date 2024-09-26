package com.test.onpoint.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.test.onpoint.R;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends AppCompatActivity {

    Button btnKembali, btnSimpan;
    FloatingActionButton fabCamera;
    ImageView ivProfile;
    TextInputEditText etUserName;
    String imageUrl;

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initializeUi();
        getBundle();
        setButton();
    }

    public void initializeUi(){
        btnKembali = findViewById(R.id.btn_back);
        btnSimpan = findViewById(R.id.btnProfileSimpan);
        fabCamera = findViewById(R.id.fab_camera);
        ivProfile = findViewById(R.id.ivProfilePicture);
        etUserName = findViewById(R.id.etNama);
    }

    private void setButton() {
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void getBundle(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Glide.with(EditProfileActivity.this).load(bundle.getString("Image")).into(ivProfile);
            etUserName.setText(bundle.getString("Username"));
        }
    }

    private void setFabCamera(){
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}