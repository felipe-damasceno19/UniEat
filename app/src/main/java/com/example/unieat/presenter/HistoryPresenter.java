package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.util.DateUtils;
import com.example.unieat.util.OrderUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryPresenter {

    public interface HistoryView {
        void onHistoryLoaded(List<Order> orders);
        void onError(String message);
    }

    private final OrderDAO orderDAO;
    private final SessionManager sessionManager;
    private final HistoryView view;

    public HistoryPresenter(Context context, HistoryView view) {
        this.orderDAO = new OrderDAO();
        this.sessionManager = new SessionManager(context);
        this.view = view;
    }

    public void getHistory() {
        String userId = sessionManager.getId();
        orderDAO.findAll(new FirebaseCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> orders) {
                List<Order> mine = new ArrayList<>();
                if (orders != null) {
                    for (Order o : orders) {
                        if (userId.equals(o.getUserId())) mine.add(o);
                    }
                }
                view.onHistoryLoaded(mine);
            }
            @Override public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    public void getHistoryByStatus(OrderStatus status) {
        orderDAO.findByStatus(status, new FirebaseCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> orders) {
                view.onHistoryLoaded(orders);
            }
            @Override public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    public String formatDate(Date date) { return DateUtils.formatDate(date); }
    public String formatStatus(OrderStatus status) { return OrderUtils.formatStatus(status); }
}