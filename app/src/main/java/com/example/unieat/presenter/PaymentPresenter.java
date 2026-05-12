package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.PaymentDAO;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.PaymentMethod;
import com.example.unieat.model.Payment;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PaymentPresenter {

    private PaymentDAO paymentDAO;
    private SessionManager sessionManager;
    private Context context;
    private UserDAO userDAO;

    public PaymentPresenter(Context context){
        paymentDAO = new PaymentDAO(context);
        sessionManager = new SessionManager(context);
        userDAO = new UserDAO(context);
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

    public boolean hasSufficientBalance(double amount) {
        return sessionManager.getBalance() >= amount;
    }

    public void deductBalance(double amount) {
        double newBalance = sessionManager.getBalance() - amount;
        sessionManager.updateBalance(newBalance);
        userDAO.updateBalance(sessionManager.getId(), newBalance);
    }
    public Payment getPaymentByOrderId(String orderId) {
        return paymentDAO.findByOrderId(orderId);
    }

    public Payment getPaymentById(String id) {
        return paymentDAO.findById(id);
    }

    public List<Payment> findAll() {
        return paymentDAO.findAll();
    }

    public Double calculateServiceFee(double amount) {
        double fee = 0.10;
        return amount * fee;
    }

    public Double calculateTotal(double amount) {
        return amount + calculateServiceFee(amount);
    }
}
