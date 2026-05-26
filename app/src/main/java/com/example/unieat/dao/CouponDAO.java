package com.example.unieat.dao;

import com.example.unieat.data.FirebaseHelper;
import com.example.unieat.model.Coupon;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CouponDAO {

    public void insert(Coupon coupon, FirebaseCallback<Void> cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("value", coupon.getValue());
        data.put("maxUses", coupon.getMaxUses());

        FirebaseHelper.coupons().child(coupon.getCode()).setValue(data)
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void findByCode(String code, FirebaseCallback<Coupon> cb) {
        FirebaseHelper.coupons().child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                if (!snap.exists()) { cb.onSuccess(null); return; }

                Double value   = snap.child("value").getValue(Double.class);
                Integer maxUses = snap.child("maxUses").getValue(Integer.class);

                Coupon coupon = new Coupon(
                        snap.getKey(),
                        value != null ? value : 0,
                        maxUses != null ? maxUses : 0
                );

                Map<String, Boolean> usedBy = new HashMap<>();
                for (DataSnapshot child : snap.child("usedBy").getChildren()) {
                    usedBy.put(child.getKey(), Boolean.TRUE);
                }
                coupon.setUsedBy(usedBy);

                cb.onSuccess(coupon);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
        });
    }

    public void findAll(FirebaseCallback<List<Coupon>> cb) {
        FirebaseHelper.coupons().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                List<Coupon> list = new java.util.ArrayList<>();
                for (DataSnapshot child : snap.getChildren()) {
                    Double value    = child.child("value").getValue(Double.class);
                    Integer maxUses = child.child("maxUses").getValue(Integer.class);
                    Coupon coupon = new Coupon(
                            child.getKey(),
                            value != null ? value : 0,
                            maxUses != null ? maxUses : 0
                    );
                    Map<String, Boolean> usedBy = new HashMap<>();
                    for (DataSnapshot u : child.child("usedBy").getChildren())
                        usedBy.put(u.getKey(), Boolean.TRUE);
                    coupon.setUsedBy(usedBy);
                    list.add(coupon);
                }
                cb.onSuccess(list);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
        });
    }

    public void delete(String code, FirebaseCallback<Void> cb) {
        FirebaseHelper.coupons().child(code).removeValue()
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void redeem(String code, String userId, FirebaseCallback<Void> cb) {
        FirebaseHelper.coupons().child(code).child("usedBy").child(userId).setValue(true)
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }
}
