package com.example.unieat.presenter;

import android.content.Context;

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

    private OrderDAO orderDAO;
    private SessionManager sessionManager;
    private List<OrderItem> cart = new ArrayList<>();

    public OrderPresenter(Context context) {
        orderDAO = new OrderDAO(context);
        sessionManager = new SessionManager(context);
    }

    public void addItem(Dish dish) {
        for(OrderItem item : cart){
            if(item.getDish().getId().equals(dish.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cart.add(new OrderItem((UUID.randomUUID().toString()), 1, dish));
    }

    public void removeItem(OrderItem item) {
        cart.remove(item);
    }

    public void changeQuantity(OrderItem item, int quantity) {
        item.setQuantity(quantity);
    }

    public double calculateSubTotal(OrderItem item) {
        return item.getQuantity() * item.getDish().getPrice();
    }

    public double calculateTotal() {
        double total = 0;
        for(OrderItem item: cart){
            total += calculateSubTotal(item);
        }
        return total;
    }

    public List<OrderItem> getCart() {
        return cart;
    }

    public boolean isCartEmpty() {
        return cart.isEmpty();
    }

    public Order placeOrder(String annotation){
        Order order = new Order(
                UUID.randomUUID().toString(),
                cart,
                OrderStatus.PENDENTE,
                new Date(),
                annotation
        );
        orderDAO.insert(order);
        cart.clear();
        return order;
    }
}
