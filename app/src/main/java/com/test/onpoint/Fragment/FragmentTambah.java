package com.test.onpoint.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.onpoint.Class.PointDataClass;
import com.test.onpoint.Class.UserClass;
import com.test.onpoint.R;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class FragmentTambah extends Fragment {

    private TextInputEditText etLokasi, etLatitude, etLongitude, etQR;
    private ImageButton btnLokasi, btnGenerate;
    private Button btnTambah;
    private ProgressBar progress_Bar;
    private ProgressDialog progressDialog;
    private DatabaseReference itemDatabase;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tambah, container, false);

        setupUiComponents(view);

        setUpButtonListener();

        return view;
    }

    private void setupUiComponents(View view){
        etLokasi = view.findViewById(R.id.etLokasi);
        etLatitude = view.findViewById(R.id.etLatitude);
        etLongitude = view.findViewById(R.id.etLongitude);
        etQR = view.findViewById(R.id.etQR);
        btnLokasi = view.findViewById(R.id.btnLokasi);
        btnGenerate = view.findViewById(R.id.btnGenerate);
        btnTambah = view.findViewById(R.id.btnTambah);
        progress_Bar = view.findViewById(R.id.progressBar);
        progress_Bar.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(getContext());

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void setUpButtonListener (){
        btnLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode();
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCheckPoint();
            }
        });
    }

    private void getLocation(){}

    private void generateQRCode(){}

    private void addCheckPoint(){
        progress_Bar.setVisibility(View.VISIBLE);

        String lokasi = etLokasi.getText().toString();
        String qrCode = etQR.getText().toString();
        String latitudeStr = etLatitude.getText().toString();
        String longitudeStr = etLongitude.getText().toString();

        if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            Toast.makeText(getContext(), "Latitude dan Longitude tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!latitudeStr.matches("^-?\\d*(\\.\\d+)?$") || !longitudeStr.matches("^-?\\d*(\\.\\d+)?$")) {
            Toast.makeText(getContext(), "Latitude dan Longitude hanya boleh berisi angka, titik, dan minus", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude = Double.parseDouble(latitudeStr);
        double longitude = Double.parseDouble(longitudeStr);

        final FirebaseUser users = firebaseAuth.getCurrentUser();
        final String[] finalUser = {""};

        if (users != null){
            String userId = users.getUid();
            DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserClass userData = snapshot.getValue(UserClass.class);
                        if (userData != null) {
                            finalUser[0] = userData.getUsername();

                            SimpleDateFormat simpleDate = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                            String currentDate = simpleDate.format(Calendar.getInstance().getTime());

                            SimpleDateFormat simpleTime = new SimpleDateFormat("HH.mm.ss", Locale.US);
                            String currentTime = simpleTime.format(Calendar.getInstance().getTime());

                            PointDataClass dataClass = new PointDataClass(lokasi, qrCode, latitude, longitude, finalUser[0], currentDate, currentTime);

                            FirebaseDatabase.getInstance().getReference("Check Point").child(qrCode).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progress_Bar.setVisibility(View.GONE);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setMessage("Data berhasil ditambahkan").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                clearInputFields();
                                                dialogInterface.dismiss();
                                            }
                                        });

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    } else {
                                        Toast.makeText(getContext(), "Gagal menambahkan data", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                private void clearInputFields(){
                                    etLokasi.setText("");
                                    etQR.setText("");
                                    etLatitude.setText("");
                                    etLongitude.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Cannot connect to database", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}