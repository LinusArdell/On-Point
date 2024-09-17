package com.test.onpoint.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.test.onpoint.Fragment.FragmentBeranda;
import com.test.onpoint.Fragment.FragmentTambah;
import com.test.onpoint.R;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabScan;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        replaceFragment(new FragmentBeranda());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_beranda:
                    replaceFragment(new FragmentBeranda());
                    break;
                case R.id.nav_add:
                    replaceFragment(new FragmentTambah());
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
    }
}