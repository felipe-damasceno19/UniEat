package com.example.unieat.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.unieat.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class KitchenHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupKitchenNavigation(this, bottomNavigationView, R.id.nav_kitchen_orders);
    }
}
