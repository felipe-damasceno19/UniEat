package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.PaymentDAO;
import com.example.unieat.enums.PaymentMethod;
import com.example.unieat.model.Payment;

import java.util.Date;
import java.util.UUID;

public class PaymentPresenter {

    private PaymentDAO paymentDAO;

    public PaymentPresenter(Context context){
        paymentDAO = new PaymentDAO(context);
    }

    public Payment processPayment(String order_id, PaymentMethod method, double amount) {
        Payment payment =  new Payment(
                UUID.randomUUID().toString(),
                order_id,
                method,
                amount,
                new Date()
        );

        paymentDAO.insert(payment);
        return payment;
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentDAO.findByOrderId(orderId);
    }

    public Double calculateServiceFee(double amount) {
        double fee = 0.10;
        return amount * fee;
    }

    public Double calculateTotal(double amount) {
        return amount + calculateServiceFee(amount);
    }
}
