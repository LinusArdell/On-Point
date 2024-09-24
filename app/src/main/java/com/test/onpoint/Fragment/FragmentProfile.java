package com.test.onpoint.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.onpoint.Activity.LoginActivity;
import com.test.onpoint.Activity.RegisterActivity;
import com.test.onpoint.Class.UserClass;
import com.test.onpoint.R;

public class FragmentProfile extends Fragment {

    ShapeableImageView profilePicture;
    TextView textViewUsername, textViewEmail, textViewRole;
    private DatabaseReference databaseReference, userDatabase;
    private FirebaseAuth userAuth;
    LinearLayout llResetPassword, llLogout, llRegister, llDownloadExcel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeUiComponents(view);

        isNetworkAvailable();

        getCurrentUser();

        setActionListener();

        return view;
    }

    public void initializeUiComponents(View view){
        profilePicture = view.findViewById(R.id.ivProfilePicture);
        textViewUsername = view.findViewById(R.id.tvUsername);
        textViewEmail = view.findViewById(R.id.tvUserEmail);
        textViewRole = view.findViewById(R.id.tvUserRole);

        userAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference();

        llResetPassword = view.findViewById(R.id.containerResetPassword);
        llRegister = view.findViewById(R.id.containerRegister);
        llLogout = view.findViewById(R.id.containerLogout);
        llDownloadExcel = view.findViewById(R.id.containerDownloadExcel);
    }

    private void setActionListener(){
        llRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmationDialog();
            }
        });

        llResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetpasword();
            }
        });

        llDownloadExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDownloadExcel();
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Konfirmasi Logout");
        builder.setMessage("Apakah Anda yakin ingin logout?");
        builder.setCancelable(true);

        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void logout() {
        userAuth.signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void getCurrentUser() {
        if (isNetworkAvailable()) {
            FirebaseUser currentUser = userAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                String email = currentUser.getEmail();

                textViewEmail.setText(email);
                saveToSharedPreferences("email", email);

                userDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserClass userData = snapshot.getValue(UserClass.class);

                            String username = userData.getUsername();
                            String userProfile = userData.getUserPicture();
                            String userRole = userData.getRole();

                            textViewUsername.setText(username);
                            textViewRole.setText(userRole);

                            saveToSharedPreferences("username", username);
                            saveToSharedPreferences("userProfile", userProfile);
                            saveToSharedPreferences("role", userRole);

                            Glide.with(getActivity())
                                    .load(userProfile)
                                    .placeholder(R.drawable.defaultpicture)
                                    .error(R.drawable.error)
                                    .into(profilePicture);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(getContext(), "Autentikasi gagal, silahkan login kembali", Toast.LENGTH_SHORT).show();
            }
        } else {
            loadUserFromSharedPreferences();
        }
    }

    private void saveToSharedPreferences(String key, String value) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void loadUserFromSharedPreferences() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

        String email = sharedPref.getString("email", "Email not available");
        String username = sharedPref.getString("username", "Username not available");
        String userProfile = sharedPref.getString("userProfile", null);
        String role = sharedPref.getString("role", "Role not available");

        textViewEmail.setText(email);
        textViewUsername.setText(username);
        textViewRole.setText(role);

        if (userProfile != null) {
            Glide.with(getActivity())
                    .load(userProfile)
                    .placeholder(R.drawable.defaultpicture)
                    .error(R.drawable.error)
                    .into(profilePicture);
        } else {
            profilePicture.setImageResource(R.drawable.defaultpicture); // Set gambar default jika tidak ada data
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void resetpasword(){
        final String resetemail = textViewEmail.getText().toString();

        if (resetemail.isEmpty()) {
            textViewEmail.setError("It's empty");
            textViewEmail.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(resetemail).matches()) {
            textViewEmail.setError("Email is not registered");
            textViewEmail.requestFocus();
        } else {
            userAuth.sendPasswordResetEmail(resetemail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void setDownloadExcel(){

    }
}