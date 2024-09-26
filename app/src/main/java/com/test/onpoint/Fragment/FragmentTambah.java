package com.test.onpoint.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.test.onpoint.Class.PointDataClass;
import com.test.onpoint.Class.UserClass;
import com.test.onpoint.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FragmentTambah extends Fragment {

    private TextInputEditText etLokasi, etLatitude, etLongitude, etQR;
    private ImageButton btnLokasi, btnGenerate;
    private Button btnTambah;
    private ProgressBar progress_Bar;
    private ProgressDialog progressDialog;
    private DatabaseReference itemDatabase;
    private ActivityResultLauncher<ScanOptions> qrCodeLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tambah, container, false);

        setupUiComponents(view);

        initActivityResultLaunchers();

        setUpButtonListener();

        setBtnGenerate();

        return view;
    }

    private void setupUiComponents(View view){
        etLokasi = view.findViewById(R.id.etLokasi);
        etLatitude = view.findViewById(R.id.etLatitude);
        etLongitude = view.findViewById(R.id.etLongitude);
        etQR = view.findViewById(R.id.etQR);
        btnLokasi = view.findViewById(R.id.btnLokasi);
        btnGenerate = view.findViewById(R.id.btnGenerate);
        btnTambah = view.findViewById(R.id.btnSimpan);
        progress_Bar = view.findViewById(R.id.progressBar);
        progress_Bar.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(getContext());

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void setBtnGenerate(){
        btnGenerate.setOnClickListener(view -> checkPermissionAndShowActivity(getContext()));
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
                }
            }
        });

        qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                setResult(result.getContents());
            }
        });
    }

    private void requestPermissionLauncher () {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(getContext(), "Access Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void setResult(String contents){
        etQR.setText(contents);
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

        double latitude;
        double longitude;

        String lokasi = etLokasi.getText().toString();
        String qrCode = etQR.getText().toString();

        if (etLatitude.getText().toString().isEmpty()){
            latitude = -2.9633061649053487;
        } else {
            String latitudeStr = etLatitude.getText().toString();
            latitude = Double.parseDouble(latitudeStr);
        }

        if (etLongitude.getText().toString().isEmpty()){
            longitude = 104.7732953101861;
        } else {
            String longitudeStr = etLongitude.getText().toString();
            longitude = Double.parseDouble(longitudeStr);
        }

        String keterangan = "";
        boolean periksa = false;

        if (!etLatitude.getText().toString().matches("^-?\\d*(\\.\\d+)?$") || !etLongitude.getText().toString().matches("^-?\\d*(\\.\\d+)?$")) {
            Toast.makeText(getContext(), "Latitude dan Longitude hanya boleh berisi angka, titik, dan minus", Toast.LENGTH_SHORT).show();
            return;
        }

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

                            PointDataClass dataClass = new PointDataClass(lokasi, qrCode, latitude, longitude, finalUser[0], currentDate, currentTime, keterangan, periksa);

                            String historyFormat = qrCode + " " + currentDate;

                            FirebaseDatabase.getInstance().getReference("Check Point").child(qrCode).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseDatabase.getInstance().getReference("Riwayat").child(qrCode).child(historyFormat).setValue(dataClass);
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