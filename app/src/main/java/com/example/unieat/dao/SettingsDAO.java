package com.example.unieat.dao;

import androidx.annotation.NonNull;

import com.example.unieat.data.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SettingsDAO {

    public void getPixInfo(FirebaseCallback<String[]> cb) {
        FirebaseHelper.settings().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                String pixKey   = snap.child("pixKey").getValue(String.class);
                String pixQrUrl = snap.child("pixQrUrl").getValue(String.class);
                cb.onSuccess(new String[]{
                        pixKey   != null ? pixKey   : "",
                        pixQrUrl != null ? pixQrUrl : ""
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {
                cb.onFailure(e.getMessage());
            }
        });
    }

    public void getServiceFee(FirebaseCallback<double[]> cb) {
        FirebaseHelper.settings().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                Boolean isPercent = snap.child("serviceFeeIsPercent").getValue(Boolean.class);
                Double value      = snap.child("serviceFeeValue").getValue(Double.class);
                cb.onSuccess(new double[]{
                        (isPercent != null ? isPercent : true) ? 1.0 : 0.0,
                        value != null ? value : 10.0
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { cb.onFailure(e.getMessage()); }
        });
    }

    public void saveServiceFee(boolean isPercent, double value, FirebaseCallback<Void> cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("serviceFeeIsPercent", isPercent);
        data.put("serviceFeeValue", value);
        FirebaseHelper.settings().updateChildren(data)
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void savePixInfo(String pixKey, String pixQrUrl, FirebaseCallback<Void> cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("pixKey",   pixKey   != null ? pixKey   : "");
        data.put("pixQrUrl", pixQrUrl != null ? pixQrUrl : "");
        FirebaseHelper.settings().updateChildren(data)
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }
}
