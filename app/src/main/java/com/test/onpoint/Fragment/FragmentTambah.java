package com.test.onpoint.Fragment;

import android.app.ProgressDialog;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.test.onpoint.R;

public class FragmentTambah extends Fragment {

    private TextInputEditText etLokasi, etLatitude, etLongitude, etQR;
    private ImageButton btnLokasi, btnGenerate;
    private Button btnTambah;
    private ProgressBar progressBar;
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
        progressBar = view.findViewById(R.id.progressBar);
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
        String lokasi = etLokasi.getText().toString();
        String qrCode = etQR.getText().toString();
        String latitudeStr = etLatitude.getText().toString();
        String longitudeStr = etLongitude.getText().toString();

        if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            Toast.makeText(getContext(), "Latitude dan Longitude tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Latitude dan Longitude harus berupa angka", Toast.LENGTH_SHORT).show();
        }

        final FirebaseUser users = firebaseAuth.getCurrentUser();
        final String[] finalUser = {""};

        if (users != null){

        }
    }


}