package com.example.unieat.util;

import com.example.unieat.enums.OrderStatus;

public class OrderUtils {

    public static String formatOrderNumber(String orderId) {
        if (orderId == null || orderId.isEmpty()) return "#0000";
        return String.format("#%04d", Math.abs(orderId.hashCode()) % 10000);
    }

    public static String formatStatus(OrderStatus status) {
        switch (status){
            case PENDENTE:   return "Pendente";
            case PREPARANDO: return "Preparando";
            case PRONTO:     return "Pronto";
            case ENTREGUE:   return "Entregue";
            case REJEITADO:  return "Rejeitado";
            default:         return status.name();
        }
    }
}
