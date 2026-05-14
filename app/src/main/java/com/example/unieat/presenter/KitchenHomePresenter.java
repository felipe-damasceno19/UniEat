package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.OrderDAO;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;

import java.util.List;

public class KitchenHomePresenter {

    public interface View {
        void showPendingCount(int count);
        void showPreparingCount(int count);
        void showReadyCount(int count);
        void showRecentOrders(List<Order> orders);
        void showError(String message);
    }

    private final View view;
    private OrderDAO orderDAO;

    public KitchenHomePresenter(Context context, View view){
        this.view = view;
        this.orderDAO = new OrderDAO(context);
    }

    public void loadDashboard() {
        try {
            int pending = orderDAO.countByStatus(OrderStatus.PENDENTE);
            int preparing = orderDAO.countByStatus(OrderStatus.PREPARANDO);
            int ready = orderDAO.countByStatus(OrderStatus.PRONTO);
            List<Order> recent = orderDAO.findRecentOrders(10);

            view.showPendingCount(pending);
            view.showPreparingCount(preparing);
            view.showReadyCount(ready);
            view.showRecentOrders(recent);
        } catch (Exception e) {
            view.showError("Erro ao carregar dashboard: " + e.getMessage());
        }
    }

    public void advanceOrderStatus(String orderId) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            view.showError("Pedido não encontrado");
            return;
        }

        OrderStatus nextStatus;
        switch (order.getStatus()) {
            case PENDENTE:
                nextStatus = OrderStatus.PREPARANDO;
                break;
            case PREPARANDO:
                nextStatus = OrderStatus.PRONTO;
                break;
            case PRONTO:
                nextStatus = OrderStatus.ENTREGUE;
                break;
            default:
                return;
        }

        orderDAO.updateStatus(orderId, nextStatus);
        loadDashboard();
    }

    public void loadOrdersByStatus(OrderStatus status) {
        try {
            List<Order> orders = orderDAO.findByStatus(status);
            view.showRecentOrders(orders);
        } catch (Exception e) {
            view.showError("Erro ao carregar pedidos: " + e.getMessage());
        }
    }
}
