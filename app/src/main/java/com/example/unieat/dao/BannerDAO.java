package com.example.unieat.dao;

import com.example.unieat.data.FirebaseHelper;
import com.example.unieat.model.Banner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannerDAO {

    public void insert(Banner banner, FirebaseCallback<String> cb) {
        String id = FirebaseHelper.banners().push().getKey();
        banner.setId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("tag", banner.getTag());
        data.put("title", banner.getTitle());
        data.put("subtitle", banner.getSubtitle());
        data.put("imageUrl", banner.getImageUrl());

        if (banner.getDishIds() != null && !banner.getDishIds().isEmpty()) {
            Map<String, Boolean> dishMap = new HashMap<>();
            for (String dishId : banner.getDishIds()) dishMap.put(dishId, true);
            data.put("dishes", dishMap);
        }

        FirebaseHelper.banners().child(id).setValue(data)
                .addOnSuccessListener(a -> cb.onSuccess(id))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void findAll(FirebaseCallback<List<Banner>> cb) {
        FirebaseHelper.banners().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                List<Banner> list = new ArrayList<>();
                for (DataSnapshot child : snap.getChildren()) {
                    Banner b = parse(child);
                    if (b != null) list.add(b);
                }
                cb.onSuccess(list);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
        });
    }

    public void delete(String id, FirebaseCallback<Void> cb) {
        FirebaseHelper.banners().child(id).removeValue()
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    private Banner parse(DataSnapshot snap) {
        String id       = snap.getKey();
        String tag      = snap.child("tag").getValue(String.class);
        String title    = snap.child("title").getValue(String.class);
        String subtitle = snap.child("subtitle").getValue(String.class);
        String imageUrl = snap.child("imageUrl").getValue(String.class);
        List<String> dishIds = new ArrayList<>();
        for (DataSnapshot d : snap.child("dishes").getChildren()) dishIds.add(d.getKey());
        return new Banner(id,
                tag      != null ? tag      : "",
                title    != null ? title    : "",
                subtitle != null ? subtitle : "",
                imageUrl != null ? imageUrl : "",
                dishIds);
    }
}
