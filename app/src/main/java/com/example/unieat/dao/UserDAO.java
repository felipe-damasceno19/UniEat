package com.example.unieat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.example.unieat.data.DatabaseHelper;
import com.example.unieat.data.FirebaseHelper;
import com.example.unieat.enums.UserType;
import com.example.unieat.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void insert(User user, FirebaseCallback<String> cb) {
        DatabaseReference ref = FirebaseHelper.users().push();
        user.setId(ref.getKey());
        ref.setValue(user)
                .addOnSuccessListener(a -> cb.onSuccess(user.getEmail()))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void findAll(FirebaseCallback<List<User>> cb) {
        FirebaseHelper.users().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                List<User> list = new ArrayList<>();
                for(DataSnapshot child: snap.getChildren())
                    list.add(snapToUser(child));
                cb.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cb.onFailure(error.getMessage());
            }
        });
    }

    public void findById (String id, FirebaseCallback<User> cb) {
        FirebaseHelper.users().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cb.onSuccess(snapshot.exists() ? snapToUser(snapshot) : null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cb.onFailure(error.getMessage());
            }
        });
    }

    public void findByEmail(String email, FirebaseCallback<User> cb) {
        FirebaseHelper.users()
                .orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot child: snapshot.getChildren()) {
                            cb.onSuccess(snapToUser(child));
                            return;
                        }
                        cb.onSuccess(null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cb.onFailure(error.getMessage());
                    }
                });
    }

    public void findByUsername(String username, FirebaseCallback<User> cb) {
        FirebaseHelper.users()
                .orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        for (DataSnapshot child : snap.getChildren()) {
                            cb.onSuccess(snapToUser(child));
                            return;
                        }
                        cb.onSuccess(null);
                    }
                    @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
                });
    }

    public void update(User user, FirebaseCallback<Void> cb) {
        FirebaseHelper.users().child(user.getId()).setValue(user)
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void updateBalance(String id, double balance, FirebaseCallback<Void> cb) {
        FirebaseHelper.users().child(id).child("balance").setValue(balance)
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void delete(String id, FirebaseCallback<Void> cb) {
        FirebaseHelper.users().child(id).removeValue()
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    private User snapToUser(DataSnapshot snap) {
        User user = snap.getValue(User.class);
        if (user != null) user.setId(snap.getKey());
        return user;
    }
}
