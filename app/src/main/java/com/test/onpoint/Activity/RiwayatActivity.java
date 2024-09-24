package com.test.onpoint.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.onpoint.Adapter.RiwayatAdapter;
import com.test.onpoint.Class.RiwayatDataClass;
import com.test.onpoint.R;

import java.util.ArrayList;
import java.util.List;

public class RiwayatActivity extends AppCompatActivity {

    private TextView tvTitle, tvKodeQR, tvLokasi, tvUser, tvTanggal, tvStatus, tvLatitude, tvLongitude;
    Button btnKembali;
    RecyclerView recyclerView;
    RiwayatAdapter adapter;
    List<RiwayatDataClass> dataList;
    DatabaseReference databaseReferences;
    ValueEventListener eventListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initializeUiComponents();

        setBtnKembali();

        fillDataFromIntent();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        String childKey = tvTitle.getText().toString();
        databaseReferences = FirebaseDatabase.getInstance().getReference("Riwayat").child(childKey);

        eventListeners = databaseReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();

                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    RiwayatDataClass dataClass = itemSnapshot.getValue(RiwayatDataClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RiwayatActivity.this, "Failed to read data from database", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void initializeUiComponents(){
        tvTitle = findViewById(R.id.tv_title);
        btnKembali = findViewById(R.id.btn_back);
        tvKodeQR = findViewById(R.id.tvQR);
        tvLokasi = findViewById(R.id.tvLokasi);
        tvUser = findViewById(R.id.tvUser);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvStatus = findViewById(R.id.tvStatus);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvLatitude = findViewById(R.id.tvLatitude);
        recyclerView = findViewById(R.id.historyRecycler);
        dataList = new ArrayList<>();
        adapter = new RiwayatAdapter(RiwayatActivity.this, dataList);
    }

    private void setBtnKembali(){
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void fillDataFromIntent(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            double latitude = bundle.getDouble("Latitude");
            double longitude = bundle.getDouble("Longitude");
            boolean status = bundle.getBoolean("Status");

            if (status == true){
                tvStatus.setText("Checked");
            } else {
                tvStatus.setText("Not Checked");
                tvStatus.setTextColor(ContextCompat.getColor(RiwayatActivity.this, R.color.red));
            }

            String hari = bundle.getString("Hari");
            String waktu = bundle.getString("Waktu");

            tvTitle.setText("Riwayat " + bundle.getString("KodeQR"));
            tvKodeQR.setText(bundle.getString("KodeQR"));
            tvLokasi.setText(bundle.getString("Lokasi"));
            tvUser.setText(bundle.getString("User"));
            tvTanggal.setText(hari + "," + waktu);


            tvLatitude.setText(String.valueOf(latitude));
            tvLongitude.setText(String.valueOf(longitude));
        }
    }
}