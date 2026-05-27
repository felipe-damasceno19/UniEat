package com.example.unieat.presenter.kitchen;

import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.dao.PaymentDAO;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.model.Payment;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;

public class KitchenHomePresenter {

    public interface View {
        void showPendingCount(int count);
        void showPreparingCount(int count);
        void showReadyCount(int count);
        void showRecentOrders(List<Order> orders);
        void showError(String message);
        void showDailyRevenue(double revenue);
        void showDeliveredCount(int count);
    }

    private final View view;
    private final OrderDAO orderDAO;
    private final PaymentDAO paymentDAO;
    private ValueEventListener countsListener;

    public KitchenHomePresenter(View view) {
        this.view = view;
        this.orderDAO = new OrderDAO();
        this.paymentDAO = new PaymentDAO();
    }

    public void startListening() {
        countsListener = orderDAO.listenToStatusCounts(new OrderDAO.OnOrderStatusCount() {
            @Override public void onCountUpdated(int pending, int preparing, int ready) {
                view.showPendingCount(pending);
                view.showPreparingCount(preparing);
                view.showReadyCount(ready);
            }
            @Override public void onError(String error) { view.showError(error); }
        });
    }

    public void stopListening() {
        if (countsListener != null) {
            orderDAO.removeOrdersListener(countsListener);
            countsListener = null;
        }
    }

    public void loadDashboard() {
        final int[] counts = {-1, -1, -1};

        orderDAO.countByStatus(OrderStatus.PENDENTE, new FirebaseCallback<Integer>() {
            @Override public void onSuccess(Integer count) {
                counts[0] = count;
                view.showPendingCount(count);
                tryLoadRecent(counts);
            }
            @Override public void onFailure(String error) { view.showError(error); }
        });

        orderDAO.countByStatus(OrderStatus.PREPARANDO, new FirebaseCallback<Integer>() {
            @Override public void onSuccess(Integer count) {
                counts[1] = count;
                view.showPreparingCount(count);
                tryLoadRecent(counts);
            }
            @Override public void onFailure(String error) { view.showError(error); }
        });

        orderDAO.countByStatus(OrderStatus.PRONTO, new FirebaseCallback<Integer>() {
            @Override public void onSuccess(Integer count) {
                counts[2] = count;
                view.showReadyCount(count);
                tryLoadRecent(counts);
            }
            @Override public void onFailure(String error) { view.showError(error); }
        });

        loadDailyStats();
    }

    public void loadDailyStats() {
        orderDAO.countByStatus(OrderStatus.ENTREGUE, new FirebaseCallback<Integer>() {
            @Override public void onSuccess(Integer count) {
                view.showDeliveredCount(count);
            }
            @Override public void onFailure(String error) {}
        });

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfToday = cal.getTimeInMillis();

        paymentDAO.findAll(new FirebaseCallback<List<Payment>>() {
            @Override public void onSuccess(List<Payment> payments) {
                double total = 0;
                for (Payment p : payments) {
                    if (p != null && p.getTimeMillis() >= startOfToday) {
                        total += p.getAmount();
                    }
                }
                view.showDailyRevenue(total);
            }
            @Override public void onFailure(String error) {}
        });
    }

    private void tryLoadRecent(int[] counts) {
        if (counts[0] < 0 || counts[1] < 0 || counts[2] < 0) return;

        orderDAO.findRecentOrders(10, new FirebaseCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> orders) {
                view.showRecentOrders(orders);
            }
            @Override public void onFailure(String error) { view.showError(error); }
        });
    }

    public void setOrderStatus(String orderId, OrderStatus newStatus) {
        orderDAO.updateStatus(orderId, newStatus, new FirebaseCallback<Void>() {
            @Override public void onSuccess(Void v) { loadDashboard(); }
            @Override public void onFailure(String error) { view.showError(error); }
        });
    }

    public void loadOrdersByStatus(OrderStatus status) {
        if (status == null) {
            orderDAO.findRecentOrders(10, new FirebaseCallback<List<Order>>() {
                @Override public void onSuccess(List<Order> orders) { view.showRecentOrders(orders); }
                @Override public void onFailure(String error) { view.showError(error); }
            });
            return;
        }
        orderDAO.findByStatus(status, new FirebaseCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> orders) { view.showRecentOrders(orders); }
            @Override public void onFailure(String error) { view.showError(error); }
        });
    }
}
