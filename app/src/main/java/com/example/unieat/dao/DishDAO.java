package com.example.unieat.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.unieat.data.FirebaseHelper;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DishDAO {

    public void insert(Dish dish, FirebaseCallback<String> cb) {
        DatabaseReference ref = FirebaseHelper.dishes().push();
        dish.setId(ref.getKey());
        ref.setValue(dish)
                .addOnSuccessListener(a -> cb.onSuccess(dish.getId()))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }


    public void findAll(FirebaseCallback<List<Dish>> cb) {
        FirebaseHelper.dishes().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                List<Dish> list = new ArrayList<>();
                for (DataSnapshot child : snap.getChildren())
                    list.add(snapToDish(child));
                cb.onSuccess(list);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
        });
    }

    public void getAvailableDishes(FirebaseCallback<List<Dish>> cb) {
        FirebaseHelper.dishes()
                .orderByChild("available").equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        List<Dish> list = new ArrayList<>();
                        for (DataSnapshot child : snap.getChildren())
                            list.add(snapToDish(child));
                        cb.onSuccess(list);
                    }
                    @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
                });
    }

    public void getDishesByType(FoodType type, FirebaseCallback<List<Dish>> cb) {
        getAvailableDishes(new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) {
                List<Dish> filtered = new ArrayList<>();
                for (Dish d : dishes)
                    if (d.getType() == type) filtered.add(d);
                cb.onSuccess(filtered);
            }
            @Override public void onFailure(String error) { cb.onFailure(error); }
        });
    }

    public void findById(String id, FirebaseCallback<Dish> cb) {
        FirebaseHelper.dishes().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                cb.onSuccess(snap.exists() ? snapToDish(snap) : null);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
        });
    }

    public void update(Dish dish, FirebaseCallback<Void> cb) {
        FirebaseHelper.dishes().child(dish.getId()).setValue(dish)
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void updateAvailability(String id, boolean available, FirebaseCallback<Void> cb) {
        FirebaseHelper.dishes().child(id).child("available").setValue(available)
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void delete(String id, FirebaseCallback<Void> cb) {
        FirebaseHelper.dishes().child(id).removeValue()
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public ValueEventListener listenToAvailableDishes(FirebaseCallback<List<Dish>> cb) {
        ValueEventListener listener = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                List<Dish> list = new ArrayList<>();
                for (DataSnapshot child : snap.getChildren()) {
                    Dish dish = snapToDish(child);
                    if (dish != null && dish.isAvailable()) list.add(dish);
                }
                cb.onSuccess(list);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
        };
        FirebaseHelper.dishes().addValueEventListener(listener);
        return listener;
    }

    public void removeListener(ValueEventListener listener) {
        FirebaseHelper.dishes().removeEventListener(listener);
    }

    public void searchByName(String name, FirebaseCallback<List<Dish>> cb) {
        findAll(new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) {
                List<Dish> filtered = new ArrayList<>();
                String query = name.toLowerCase();
                for (Dish d : dishes)
                    if (d.getName().toLowerCase().contains(query)) filtered.add(d);
                cb.onSuccess(filtered);
            }
            @Override public void onFailure(String error) { cb.onFailure(error); }
        });
    }

    private Dish snapToDish(DataSnapshot snap) {
        Dish dish = snap.getValue(Dish.class);
        if (dish != null) dish.setId(snap.getKey());
        return dish;
    }
}
