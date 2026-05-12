package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.OrderDAO;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryPresenter {

    private OrderDAO orderDAO;

    public HistoryPresenter(Context context) {
        orderDAO = new OrderDAO(context);
    }

    public List<Order> getHistory() {
        return orderDAO.findAll();
    }
    
    public List<Order> getHistoryByStatus(OrderStatus status) {
        return orderDAO.findByStatus(status);
    }
    
    public String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    public String formatStatus(OrderStatus status) {
        switch (status){
            case PENDENTE: return "Pendente";
            case PREPARANDO: return "Preparando";
            case PRONTO: return "Pronto";
            case ENTREGUE: return "Entregue";
            default: return status.name();
        }
    }
}
