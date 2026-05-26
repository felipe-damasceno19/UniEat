package com.example.unieat.dao;

import com.example.unieat.data.FirebaseHelper;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Dish;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDAO {

    private final DishDAO dishDAO = new DishDAO();

    public void insert(Order order, FirebaseCallback<Order> cb) {
        String orderId = FirebaseHelper.orders().push().getKey();
        order.setId(orderId);

        Map<String, Object> batch = new HashMap<>();
        batch.put("orders/" + orderId + "/annotation",   order.getAnnotation());
        batch.put("orders/" + orderId + "/orderStatus",  order.getStatus().name());
        batch.put("orders/" + orderId + "/time",         order.getTime().getTime());
        batch.put("orders/" + orderId + "/userId",       order.getUserId());

        for (OrderItem item : order.getItems()) {
            String itemId = FirebaseHelper.orders()
                    .child(orderId).child("items").push().getKey();
            item.setId(itemId);
            batch.put("orders/" + orderId + "/items/" + itemId + "/dishId",   item.getDish().getId());
            batch.put("orders/" + orderId + "/items/" + itemId + "/quantity", item.getQuantity());
        }

        FirebaseHelper.getInstance().getReference()
                .updateChildren(batch)
                .addOnSuccessListener(a -> cb.onSuccess(order))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void findById(String id, FirebaseCallback<Order> cb) {
        FirebaseHelper.orders().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                if (!snap.exists()) { cb.onSuccess(null); return; }
                buildOrderFromSnap(snap, cb);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
        });
    }

    public void findAll(FirebaseCallback<List<Order>> cb) {
        FirebaseHelper.orders().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                List<DataSnapshot> snaps = new ArrayList<>();
                for (DataSnapshot child : snap.getChildren()) snaps.add(child);
                buildOrderList(snaps, cb);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
        });
    }

    public void findByStatus(OrderStatus status, FirebaseCallback<List<Order>> cb) {
        FirebaseHelper.orders()
                .orderByChild("orderStatus").equalTo(status.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        List<DataSnapshot> snaps = new ArrayList<>();
                        for (DataSnapshot child : snap.getChildren()) snaps.add(child);
                        buildOrderList(snaps, cb);
                    }
                    @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
                });
    }

    public void findRecentOrders(int limit, FirebaseCallback<List<Order>> cb) {
        FirebaseHelper.orders()
                .orderByChild("time").limitToLast(limit)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        List<DataSnapshot> snaps = new ArrayList<>();
                        for (DataSnapshot child : snap.getChildren()) snaps.add(child);
                        buildOrderList(snaps, cb);
                    }
                    @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
                });
    }

    public void findByUserId(String userId, FirebaseCallback<List<Order>> cb) {
        FirebaseHelper.orders()
                .orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        List<DataSnapshot> snaps = new ArrayList<>();
                        for (DataSnapshot child : snap.getChildren()) snaps.add(child);
                        buildOrderList(snaps, cb);
                    }
                    @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
                });
    }

    public void countByStatus(OrderStatus status, FirebaseCallback<Integer> cb) {
        findByStatus(status, new FirebaseCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> orders) { cb.onSuccess(orders.size()); }
            @Override public void onFailure(String error) { cb.onFailure(error); }
        });
    }

    public void updateStatus(String id, OrderStatus status, FirebaseCallback<Void> cb) {
        FirebaseHelper.orders().child(id).child("orderStatus").setValue(status.name())
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public void delete(String id, FirebaseCallback<Void> cb) {
        FirebaseHelper.orders().child(id).removeValue()
                .addOnSuccessListener(a -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public ValueEventListener listenToOrder(String orderId, FirebaseCallback<Order> cb) {
        ValueEventListener listener = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                if (snap.exists()) buildOrderFromSnap(snap, cb);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
        };
        FirebaseHelper.orders().child(orderId).addValueEventListener(listener);
        return listener;
    }

    public ValueEventListener listenToAllOrders(FirebaseCallback<List<Order>> cb) {
        ValueEventListener listener = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                List<DataSnapshot> snaps = new ArrayList<>();
                for (DataSnapshot child : snap.getChildren()) snaps.add(child);
                buildOrderList(snaps, cb);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onFailure(e.getMessage()); }
        };
        FirebaseHelper.orders().addValueEventListener(listener);
        return listener;
    }

    public interface OnOrderStatusCount {
        void onCountUpdated(int pending, int preparing, int ready);
        void onError(String error);
    }

    public ValueEventListener listenToStatusCounts(OnOrderStatusCount cb) {
        ValueEventListener listener = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                int pending = 0, preparing = 0, ready = 0;
                for (DataSnapshot orderSnap : snap.getChildren()) {
                    String s = orderSnap.child("orderStatus").getValue(String.class);
                    if (s == null) continue;
                    switch (s) {
                        case "PENDENTE":   pending++;   break;
                        case "PREPARANDO": preparing++; break;
                        case "PRONTO":     ready++;     break;
                    }
                }
                cb.onCountUpdated(pending, preparing, ready);
            }
            @Override public void onCancelled(DatabaseError e) { cb.onError(e.getMessage()); }
        };
        FirebaseHelper.orders().addValueEventListener(listener);
        return listener;
    }

    public void removeOrdersListener(ValueEventListener listener) {
        FirebaseHelper.orders().removeEventListener(listener);
    }

    public void removeOrderListener(String orderId, ValueEventListener listener) {
        FirebaseHelper.orders().child(orderId).removeEventListener(listener);
    }

    private void buildOrderFromSnap(DataSnapshot snap, FirebaseCallback<Order> cb) {
        String orderId    = snap.getKey();
        String annotation = snap.child("annotation").getValue(String.class);
        String statusStr  = snap.child("orderStatus").getValue(String.class);
        Long time         = snap.child("time").getValue(Long.class);
        String userId     = snap.child("userId").getValue(String.class);

        OrderStatus status = statusStr != null ? OrderStatus.valueOf(statusStr) : OrderStatus.PENDENTE;
        Date date = time != null ? new Date(time) : new Date();

        List<DataSnapshot> itemSnaps = new ArrayList<>();
        for (DataSnapshot itemSnap : snap.child("items").getChildren())
            itemSnaps.add(itemSnap);

        if (itemSnaps.isEmpty()) {
            Order order = new Order(orderId, userId, new ArrayList<>(), status, date, annotation);
            cb.onSuccess(order);
            return;
        }

        List<OrderItem> items = new ArrayList<>();
        final int[] remaining = {itemSnaps.size()};

        for (DataSnapshot itemSnap : itemSnaps) {
            String dishId  = itemSnap.child("dishId").getValue(String.class);
            Integer qty    = itemSnap.child("quantity").getValue(Integer.class);
            String itemId  = itemSnap.getKey();
            int quantity   = qty != null ? qty : 1;

            if (dishId == null) {
                remaining[0]--;
                if (remaining[0] == 0)
                    cb.onSuccess(new Order(orderId, userId, items, status, date, annotation));
                continue;
            }

            dishDAO.findById(dishId, new FirebaseCallback<Dish>() {
                @Override public void onSuccess(Dish dish) {
                    if (dish != null) {
                        items.add(new OrderItem(itemId, quantity, dish));
                    }
                    remaining[0]--;
                    if (remaining[0] == 0)
                        cb.onSuccess(new Order(orderId, userId, items, status, date, annotation));
                }
                @Override public void onFailure(String error) {
                    remaining[0]--;
                    if (remaining[0] == 0)
                        cb.onSuccess(new Order(orderId, userId, items, status, date, annotation));
                }
            });
        }
    }

    private void buildOrderList(List<DataSnapshot> snaps, FirebaseCallback<List<Order>> cb) {
        if (snaps.isEmpty()) { cb.onSuccess(new ArrayList<>()); return; }

        List<Order> orders = new ArrayList<>();
        final int[] remaining = {snaps.size()};

        for (DataSnapshot snap : snaps) {
            buildOrderFromSnap(snap, new FirebaseCallback<Order>() {
                @Override public void onSuccess(Order order) {
                    orders.add(order);
                    if (--remaining[0] == 0) cb.onSuccess(orders);
                }
                @Override public void onFailure(String error) {
                    if (--remaining[0] == 0) cb.onSuccess(orders);
                }
            });
        }
    }
}