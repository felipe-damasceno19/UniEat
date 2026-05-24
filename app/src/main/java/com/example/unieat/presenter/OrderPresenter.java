package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Dish;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderPresenter {

    public interface OrderView {
        void onOrderPlaced(Order order);
        void onOrderError(String message);
    }

    private final OrderDAO orderDAO;
    private final SessionManager sessionManager;
    private final List<OrderItem> cart = new ArrayList<>();

    public OrderPresenter(Context context) {
        this.orderDAO = new OrderDAO();
        this.sessionManager = new SessionManager(context);
    }

    public void addItem(Dish dish) {
        for (OrderItem item : cart) {
            if (item.getDish().getId().equals(dish.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cart.add(new OrderItem(UUID.randomUUID().toString(), 1, dish));
    }

    public void removeItem(OrderItem item)                  { cart.remove(item); }
    public void changeQuantity(OrderItem item, int quantity) { item.setQuantity(quantity); }
    public List<OrderItem> getCart()                        { return cart; }
    public boolean isCartEmpty()                            { return cart.isEmpty(); }

    public double calculateSubTotal(OrderItem item) {
        return item.getQuantity() * item.getDish().getPrice();
    }

    public double calculateTotal() {
        double total = 0;
        for (OrderItem item : cart) total += calculateSubTotal(item);
        return total;
    }

    public void placeOrder(String annotation, OrderView view) {
        Order order = new Order(
                UUID.randomUUID().toString(),
                sessionManager.getId(),
                new ArrayList<>(cart),
                OrderStatus.PENDENTE,
                new Date(),
                annotation
        );

        orderDAO.insert(order, new FirebaseCallback<Order>() {
            @Override public void onSuccess(Order savedOrder) {
                cart.clear();
                view.onOrderPlaced(savedOrder);
            }
            @Override public void onFailure(String error) {
                view.onOrderError("Erro ao realizar pedido: " + error);
            }
        });
    }
}