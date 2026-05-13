package com.example.unieat.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.unieat.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AvaliationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliation);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNavigationView, -1);
    }
}
