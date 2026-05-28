package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.data.FirebaseHelper;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.util.DateUtils;
import com.example.unieat.util.OrderUtils;

import java.util.Date;

import com.google.firebase.database.ValueEventListener;

public class OrderStatusPresenter {

    public interface OrderStatusView {
        void onOrderLoaded(Order order);
        void onError(String message);
    }

    private final OrderDAO orderDAO;
    private ValueEventListener activeListener;

    public OrderStatusPresenter(Context context) {
        this.orderDAO = new OrderDAO();
    }

    public void getOrderById(String orderId, OrderStatusView view) {
        orderDAO.findById(orderId, new FirebaseCallback<Order>() {
            @Override public void onSuccess(Order order) { view.onOrderLoaded(order); }
            @Override public void onFailure(String error) { view.onError(error); }
        });
    }

    public void listenToOrder(String orderId, OrderStatusView view) {
        activeListener = orderDAO.listenToOrder(orderId, new FirebaseCallback<Order>() {
            @Override public void onSuccess(Order order) { view.onOrderLoaded(order); }
            @Override public void onFailure(String error) { view.onError(error); }
        });
    }

    public void stopListening(String orderId) {
        if (activeListener != null) {
            orderDAO.findById(orderId, FirebaseCallback.ignore());
            FirebaseHelper.orders()
                    .child(orderId)
                    .removeEventListener(activeListener);
            activeListener = null;
        }
    }

    public String formatDate(Date date)          { return DateUtils.formatDate(date); }
    public String formatStatus(OrderStatus status) { return OrderUtils.formatStatus(status); }
}