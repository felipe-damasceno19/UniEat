package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.OrderDAO;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.util.DateUtils;
import com.example.unieat.util.OrderUtils;

import java.util.Date;

public class OrderStatusPresenter {

    private OrderDAO orderDAO;

    public OrderStatusPresenter(Context context) {
        orderDAO = new OrderDAO(context);
    }

    public Order getOrderById(String orderId){
        return orderDAO.findById(orderId);
    }

    public String formatDate(Date date) {
        return DateUtils.formatDate(date);
    }

    public String formatStatus(OrderStatus status) {
        return OrderUtils.formatStatus(status);
    }
}
