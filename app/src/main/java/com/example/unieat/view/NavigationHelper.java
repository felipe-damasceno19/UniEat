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
            } else if (itemId == R.id.nav_history) {
                intent = new Intent(activity, HistoryActivity.class);
            } else if (itemId == R.id.nav_profile) {
                intent = new Intent(activity, ProfileActivity.class);
            }

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

    public static void setupKitchenNavigation(Activity activity, BottomNavigationView bottomNavigationView, int selectedItemId) {
        bottomNavigationView.setSelectedItemId(selectedItemId);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == selectedItemId) return true;

            Intent intent = null;
            if (itemId == R.id.nav_kitchen_home) {
                intent = new Intent(activity, KitchenHomeActivity.class);
            } else if (itemId == R.id.nav_kitchen_menu) {
                intent = new Intent(activity, KitchenMenuActivity.class);
            } else if (itemId == R.id.nav_kitchen_history) {
                intent = new Intent(activity, KitchenAllOrdersActivity.class);
            } else if (itemId == R.id.nav_kitchen_profile) {
                intent = new Intent(activity, ProfileActivity.class);
            }

            if (intent != null) {
                activity.startActivity(intent);
                if (!(activity instanceof KitchenHomeActivity)) {
                    activity.finish();
                }
                return true;
            }
            return false;
        });
    }
}
