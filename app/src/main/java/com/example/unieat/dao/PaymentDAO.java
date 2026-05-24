package com.example.unieat.dao;

import com.example.unieat.data.FirebaseHelper;
import com.example.unieat.enums.PaymentMethod;
import com.example.unieat.model.Payment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentDAO {

    public void insert(Payment payment, FirebaseCallback<String> cb) {
        DatabaseReference ref = FirebaseHelper.payments().push();
        payment.setId(ref.getKey());
        ref.setValue(payment)
                .addOnSuccessListener(a -> cb.onSuccess(payment.getId()))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void findByOrderId(String orderId, FirebaseCallback<Payment> cb) {
        FirebaseHelper.payments()
                .orderByChild("orderId").equalTo(orderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        for (DataSnapshot child : snap.getChildren()) {
                            cb.onSuccess(snapToPayment(child));
                            return;
                        }
                        cb.onSuccess(null);
                    }
                    @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
                });
    }

    public void findById(String id, FirebaseCallback<Payment> cb) {
        FirebaseHelper.payments().child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        cb.onSuccess(snap.exists() ? snapToPayment(snap) : null);
                    }
                    @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
                });
    }

    public void findAll(FirebaseCallback<List<Payment>> cb) {
        FirebaseHelper.payments()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        List<Payment> list = new ArrayList<>();
                        for (DataSnapshot child : snap.getChildren())
                            list.add(snapToPayment(child));
                        cb.onSuccess(list);
                    }
                    @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
                });
    }

    private Payment snapToPayment(DataSnapshot snap) {
        String id        = snap.getKey();
        String orderId   = snap.child("orderId").getValue(String.class);
        String methodStr = snap.child("method").getValue(String.class);
        Double amount    = snap.child("amount").getValue(Double.class);
        Long time        = snap.child("timeMillis").getValue(Long.class);

        return new Payment(
                id,
                orderId,
                methodStr != null ? PaymentMethod.valueOf(methodStr) : null,
                amount != null ? amount : 0.0,
                time != null ? new Date(time) : new Date()
        );
    }
}