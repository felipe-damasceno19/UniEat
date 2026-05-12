package com.example.unieat.util;

import com.example.unieat.enums.OrderStatus;

public class OrderUtils {

    public static String formatStatus(OrderStatus status) {
        switch (status){
            case PENDENTE: return "Pendente";
            case PREPARANDO: return "Preparando";
            case PRONTO: return "Pronto";
            case ENTREGUE: return "Entregue";
            default: return status.name();
        }
    }
}
