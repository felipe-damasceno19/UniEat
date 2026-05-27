package com.example.unieat.dao;

import com.example.unieat.data.FirebaseHelper;
import com.example.unieat.model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RatingDAO {

    public void insert(Rating rating, FirebaseCallback<String> cb) {
        DatabaseReference ref = FirebaseHelper.ratings().push();
        rating.setId(ref.getKey());
        ref.setValue(rating)
                .addOnSuccessListener(a -> cb.onSuccess(rating.getId()))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void findAll(FirebaseCallback<List<Rating>> cb) {
        FirebaseHelper.ratings().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                List<Rating> list = new ArrayList<>();
                for (DataSnapshot child : snap.getChildren())
                    list.add(snapToRating(child));
                cb.onSuccess(list);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
        });
    }

    public void findByDishId(String dishId, FirebaseCallback<List<Rating>> cb) {
        FirebaseHelper.ratings()
                .orderByChild("dishId").equalTo(dishId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        List<Rating> list = new ArrayList<>();
                        for (DataSnapshot child : snap.getChildren())
                            list.add(snapToRating(child));
                        cb.onSuccess(list);
                    }
                    @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
                });
    }

    public void findByDishIdAndUserId(String dishId, String userId, FirebaseCallback<Rating> cb) {
        FirebaseHelper.ratings()
                .orderByChild("dishId").equalTo(dishId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        for (DataSnapshot child : snap.getChildren()) {
                            Rating r = snapToRating(child);
                            if (r != null && userId.equals(r.getUserId())) {
                                cb.onSuccess(r);
                                return;
                            }
                        }
                        cb.onSuccess(null);
                    }
                    @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
                });
    }

    public void update(Rating rating, FirebaseCallback<Void> cb) {
        FirebaseHelper.ratings().child(rating.getId()).setValue(rating)
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void delete(String id, FirebaseCallback<Void> cb) {
        FirebaseHelper.ratings().child(id).removeValue()
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    private Rating snapToRating(DataSnapshot snap) {
        Rating rating = snap.getValue(Rating.class);
        if (rating != null) rating.setId(snap.getKey());
        return rating;
    }
}