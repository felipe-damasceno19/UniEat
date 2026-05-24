package com.example.unieat.util;

import android.app.Activity;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class AppNotification {

    public static void success(Activity activity, String message) {
        show(activity, message, 0xFF2E7D32);
    }

    public static void error(Activity activity, String message) {
        show(activity, message, 0xFFB71C1C);
    }

    public static void info(Activity activity, String message) {
        show(activity, message, 0xFF1A237E);
    }

    private static void show(Activity activity, String message, int bgColor) {
        View root = activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(root, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(bgColor);
        snackbar.setTextColor(0xFFFFFFFF);
        snackbar.show();
    }
}
