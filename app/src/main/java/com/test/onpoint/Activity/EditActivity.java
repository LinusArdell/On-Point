package com.test.onpoint.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {
    Button btnKembali, btn_Simpan;
    TextInputEditText etLokasi, etLatitude, etLongitude, editTextQR;
    FirebaseAuth firebaseAuth;
    ProgressBar progress_Bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initializeUiComponents();

        getDataFromBundle();

        setButtonOnClick();
    }

    public void initializeUiComponents(){
        btnKembali = findViewById(R.id.btn_back);
        btn_Simpan = findViewById(R.id.btnSimpan);
        etLokasi = findViewById(R.id.etLokasi);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        editTextQR = findViewById(R.id.etQR);
        progress_Bar = findViewById(R.id.progressBar);
        progress_Bar.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void setButtonOnClick(){
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_Simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_Bar.setVisibility(View.VISIBLE);
                update();
            }
        });
    }

    public void update(){
        String lokasi = etLokasi.getText().toString();
        String latitudeStr = etLatitude.getText().toString();
        String longitudeStr = etLongitude.getText().toString();
        String qrCode = editTextQR.getText().toString();
        boolean status = true;
        String keterangan = "";

        if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            Toast.makeText(EditActivity.this, "Latitude dan Longitude tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!latitudeStr.matches("^-?\\d*(\\.\\d+)?$") || !longitudeStr.matches("^-?\\d*(\\.\\d+)?$")) {
            Toast.makeText(EditActivity.this, "Latitude dan Longitude hanya boleh berisi angka, titik, dan minus", Toast.LENGTH_SHORT).show();
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

                            SimpleDateFormat simpleHour = new SimpleDateFormat("HH", Locale.US);
                            String currentHour = simpleHour.format(Calendar.getInstance().getTime());

                            PointDataClass dataClass = new PointDataClass(lokasi, qrCode, latitude, longitude, finalUser[0], currentDate, currentTime, keterangan, status);

                            String historyFormat = qrCode + " " + currentDate + " " + currentHour;

                            FirebaseDatabase.getInstance().getReference("Check Point").child(qrCode).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseDatabase.getInstance().getReference("Riwayat").child(qrCode).child(historyFormat).setValue(dataClass);
                                        progress_Bar.setVisibility(View.GONE);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                        builder.setMessage("Data berhasil ditambahkan").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                finish();
                                            }
                                        });

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    } else {
                                        Toast.makeText(EditActivity.this, "Gagal menambahkan data", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                                    progress_Bar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditActivity.this, "Cannot connect to database", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getDataFromBundle(){
        Bundle bundle = getIntent().getExtras();

        if (bundle!= null){
            double latitude = bundle.getDouble("Latitude");
            double longitude = bundle.getDouble("Longitude");

            boolean status = bundle.getBoolean("Status");

            if (status == true){
                String strStatus = "Checked";
            } else {
                String strStatus = "Not Checked";
            }

            etLokasi.setText(bundle.getString("Lokasi"));
            etLatitude.setText(String.valueOf(latitude));
            etLongitude.setText(String.valueOf(longitude));
            editTextQR.setText(bundle.getString("KodeQR"));
        }
    }
}