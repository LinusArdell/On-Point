package com.test.onpoint.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.test.onpoint.Class.UserClass;
import com.test.onpoint.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    Button btnKembali, btnSimpan;
    FloatingActionButton fabCamera;
    ImageView ivProfile;
    TextInputEditText etUserName;
    Uri uri;
    String currentPhotoPath, userId, userProfilePicture, userName, userNewProfileUrl, userRole, userEmail;
    ActivityResultLauncher<Intent> activityResultLauncher, takePictureLauncher;
    ActivityResultLauncher<String> requestPermissionLauncher;
    FirebaseAuth userAuth;
    DatabaseReference userDatabase;
    StorageReference userStorage;


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
        getCurrentUser();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK) {
                            Intent data = o.getData();
                            uri= data.getData();
                            ivProfile.setImageURI(uri);
                        } else {
                            Toast.makeText(EditProfileActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null && result.getData().getExtras() != null) {
                            Bundle extras = result.getData().getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            ivProfile.setImageBitmap(imageBitmap);
                            uri = getImageUri(this, imageBitmap);
                        } else {
                            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                            ivProfile.setImageBitmap(imageBitmap);
                            uri = Uri.fromFile(new File(currentPhotoPath));
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(EditProfileActivity.this, "Access Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Camera permission required", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        setFabCamera();
    }

    public void initializeUi(){
        btnKembali = findViewById(R.id.btn_back);
        btnSimpan = findViewById(R.id.btnProfileSimpan);
        fabCamera = findViewById(R.id.fab_camera);
        ivProfile = findViewById(R.id.ivProfilePicture);
        etUserName = findViewById(R.id.etNama);

        userAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userStorage = FirebaseStorage.getInstance().getReference("User Profile");
    }

    public void setButton() {
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }

    public void uploadImage(){
        if (uri != null){

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yy", Locale.US);
            Calendar calendar = Calendar.getInstance();
            String currentDates = dateFormat.format(calendar.getTime());

            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_dialog);
            AlertDialog dialog = builder.create();
            dialog.show();

            userStorage.child(userName).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri urlImage = uriTask.getResult();
                    userNewProfileUrl = urlImage.toString();
                    updateProfile();
                    dialog.dismiss();
                }
            });

        } else {
            updateIfNoImage();
        }
    }

    public void updateProfile(){
        String newUsername = etUserName.getText().toString();
        String role = userRole;
        String email = userEmail;

        UserClass dataClass = new UserClass(newUsername, email, userNewProfileUrl, role);

        userDatabase.child(userId).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EditProfileActivity.this, "Data berhasil di perbarui", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Data gagal diperbarui", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void updateIfNoImage(){
        String newUsername = etUserName.getText().toString();
        String role = userRole;
        String email = userEmail;

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_dialog);
        AlertDialog dialog = builder.create();
        dialog.show();

        UserClass dataClass = new UserClass(newUsername, email, userProfilePicture, role);

        userDatabase.child(userId).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EditProfileActivity.this, "Data berhasil di perbarui", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Data gagal diperbarui", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
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

    public void setFabCamera(){
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
    }

    public void showBottomDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout galeriLayout = dialog.findViewById(R.id.layoutGaleri);
        LinearLayout kameraLayout = dialog.findViewById(R.id.layoutKamera);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        galeriLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        kameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(takePictureLauncher);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(ActivityResultLauncher<Intent> takePictureLauncher) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.test.onpoint.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                takePictureLauncher.launch(takePictureIntent);
            }
        }

    }

    public void getCurrentUser(){
        FirebaseUser currentUser = userAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userEmail = currentUser.getEmail();

            userDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserClass userData = snapshot.getValue(UserClass.class);
                        
                        userProfilePicture = userData.getUserPicture();
                        userName = userData.getUsername();
                        userRole = userData.getRole();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditProfileActivity.this, "Database error! error code : EPA", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Failed to retrieve current user", Toast.LENGTH_SHORT).show();
        }
    }

}