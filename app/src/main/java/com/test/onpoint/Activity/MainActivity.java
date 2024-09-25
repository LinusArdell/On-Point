package com.test.onpoint.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.test.onpoint.Class.PointDataClass;
import com.test.onpoint.Class.UserClass;
import com.test.onpoint.Fragment.FragmentBeranda;
import com.test.onpoint.Fragment.FragmentProfile;
import com.test.onpoint.Fragment.FragmentTambah;
import com.test.onpoint.R;
import com.test.onpoint.Fragment.SearchFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabScan;
    BottomNavigationView bottomNavigationView;
    private ActivityResultLauncher<ScanOptions> qrCodeLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        initializeComponents();

        replaceFragment(new FragmentBeranda());

        initActivityResultLaunchers();

        setFabScan();

        setBottomNavigationView();

        requestPermissionLauncher();
    }

    private void setFabScan (){
        fabScan.setOnClickListener(view -> checkPermissionAndShowActivity(this));
    }

    private void setBottomNavigationView (){
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_beranda:
                    replaceFragment(new FragmentBeranda());
                    break;
                case R.id.nav_add:
                    replaceFragment(new FragmentTambah());
                    break;
                case R.id.nav_profile:
                    replaceFragment(new FragmentProfile());
                    break;
                case R.id.nav_search:
                    replaceFragment(new SearchFragment());
                    break;
            }
            return true;
        });
    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private void initializeComponents(){
        fabScan = findViewById(R.id.fabQrScanner);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        databaseReference = FirebaseDatabase.getInstance().getReference("Check Point");

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt("Scan QR Code");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);

        qrCodeLauncher.launch(options);
    }

    private void initActivityResultLaunchers(){
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean isGranted) {
                if (isGranted) {
                    showCamera();
                } else {
                    Toast.makeText(MainActivity.this, "Beri akses kamera untuk aplikasi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                setResult(result.getContents());
            }
        });
    }

    private void requestPermissionLauncher () {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(MainActivity.this, "Access Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Camera permission required", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setResult(String contents) {
        Query query = databaseReference.orderByChild("qrCode").equalTo(contents);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PointDataClass data = dataSnapshot.getValue(PointDataClass.class);
                        showInfoDialog(data);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "QR Code tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showInfoDialog(PointDataClass data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String message = "Kode QR: " + data.getQrCode() + "\n" +
                "Lokasi: " + data.getLokasi();

        final FirebaseUser users = firebaseAuth.getCurrentUser();
        final String[] finalUser = {""};

        builder.setTitle("Informasi Check Point")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Check", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String kodeQR = data.getQrCode();
                        String lokasi = data.getLokasi();

                        double lintang = data.getLatitude();
                        double bujur = data.getLatitude();
                        boolean check = true;
                        String keterangan = "";

                        if (users != null) {
                            String userId = users.getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot != null){
                                        UserClass userData = snapshot.getValue(UserClass.class);
                                        if (userData != null){
                                            finalUser[0] = userData.getUsername();

                                            SimpleDateFormat simpleDate = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                                            String currentDate = simpleDate.format(Calendar.getInstance().getTime());

                                            SimpleDateFormat simpleTime = new SimpleDateFormat("HH.mm.ss", Locale.US);
                                            String currentTime = simpleTime.format(Calendar.getInstance().getTime());

                                            PointDataClass dataClass = new PointDataClass(lokasi, kodeQR, lintang, bujur, finalUser[0], currentDate, currentTime, keterangan, check);

                                            String historyFormat = kodeQR + " " + currentDate;

                                            FirebaseDatabase.getInstance().getReference("Check Point").child(kodeQR).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FirebaseDatabase.getInstance().getReference("Riwayat").child(kodeQR).child(historyFormat).setValue(dataClass);

                                                    Toast.makeText(MainActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void checkPoint(PointDataClass data) {
        Toast.makeText(this, "Pengecekan pada check point: " + data.getLokasi(), Toast.LENGTH_SHORT).show();
    }

}