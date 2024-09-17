package com.test.onpoint.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.test.onpoint.R;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabScan;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        bottomNavigationView.setBackground(null);
    }

    private void initializeComponents(){
        fabScan = findViewById(R.id.fabQrScanner);
        bottomNavigationView = findViewById(R.id.bottomNavView);
    }

//    test
}