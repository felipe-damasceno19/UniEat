package com.example.unieat.view;

import android.app.Activity;
import android.content.Intent;

import com.example.unieat.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationHelper {

    public static void setupBottomNavigation(Activity activity, BottomNavigationView bottomNavigationView, int selectedItemId) {
        bottomNavigationView.setSelectedItemId(selectedItemId);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == selectedItemId) return true;

            Intent intent = null;
            if (itemId == R.id.nav_home) {
                intent = new Intent(activity, StudentHomeActivity.class);
            } else if (itemId == R.id.nav_menu) {
                intent = new Intent(activity, MenuActivity.class);
            } else if (itemId == R.id.nav_orders) {
                intent = new Intent(activity, OrderActivity.class);
            }
            // Histórico activity is likely not created yet, so I'll leave it or add it if found

            if (intent != null) {
                activity.startActivity(intent);
                if (!(activity instanceof StudentHomeActivity)) {
                    activity.finish();
                }
                return true;
            }
            return false;
        });
    }
}
