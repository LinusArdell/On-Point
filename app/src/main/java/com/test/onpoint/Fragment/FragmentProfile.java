package com.test.onpoint.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.onpoint.Activity.RegisterActivity;
import com.test.onpoint.Class.UserClass;
import com.test.onpoint.R;

public class FragmentProfile extends Fragment {

    ShapeableImageView profilePicture;
    TextView textViewUsername, textViewEmail, textViewRole, resetPassword, downloadExcel, textViewLogout, textViewRegister;
    private DatabaseReference databaseReference, userDatabase;
    private FirebaseAuth userAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeUiComponents(view);

        getCurrentUser();

        setActionListener();

        return view;
    }

    private void initializeUiComponents(View view){
        profilePicture = view.findViewById(R.id.ivProfilePicture);
        textViewUsername = view.findViewById(R.id.tvUsername);
        textViewEmail = view.findViewById(R.id.tvUserEmail);
        textViewRole = view.findViewById(R.id.tvUserRole);
        resetPassword = view.findViewById(R.id.tvResetPassword);
        downloadExcel = view.findViewById(R.id.tvDowloadExcel);
        textViewLogout = view.findViewById(R.id.tvLogout);
        textViewRegister = view.findViewById(R.id.tvRegister);

        userAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void getCurrentUser(){
        FirebaseUser currentUser = userAuth.getCurrentUser();

        if (currentUser != null){
            String userId = currentUser.getUid();
            String email = currentUser.getEmail();

            textViewEmail.setText(email);

            userDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        UserClass userData = snapshot.getValue(UserClass.class);

                        String username = userData.getUsername();
                        String userProfile = userData.getUserPicture();
                        String userRole = userData.getRole();

                        textViewUsername.setText(username);
                        textViewRole.setText(userRole);

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
        }
    }

    private void setActionListener(){
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}