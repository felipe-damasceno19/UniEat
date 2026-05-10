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
            } else if (itemId == R.id.nav_orders) {
                intent = new Intent(activity, OrderActivity.class);
            }
            // Add other navigation cases here (nav_menu, nav_history) when activities are ready

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
