package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.PaymentDAO;
import com.example.unieat.dao.SettingsDAO;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.PaymentMethod;
import com.example.unieat.model.Payment;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PaymentPresenter {

    public interface PaymentView {
        void onPaymentSuccess(Payment payment);
        void onPaymentError(String message);
    }

    private final PaymentDAO paymentDAO;
    private final SessionManager sessionManager;
    private final UserDAO userDAO;

    public PaymentPresenter(Context context) {
        this.paymentDAO = new PaymentDAO();
        this.sessionManager = new SessionManager(context);
        this.userDAO = new UserDAO();
    }

    public void processPayment(String orderId, PaymentMethod method, double amount, PaymentView view) {
        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                orderId,
                method,
                amount,
                new Date()
        );

        paymentDAO.insert(payment, new FirebaseCallback<String>() {
            @Override public void onSuccess(String id) { view.onPaymentSuccess(payment); }
            @Override public void onFailure(String error) { view.onPaymentError("Erro ao registrar pagamento: " + error); }
        });
    }

    public double getBalance() { return sessionManager.getBalance(); }

    public boolean hasSufficientBalance(double amount) {
        return sessionManager.getBalance() >= amount;
    }

    public void deductBalance(double amount) {
        double newBalance = sessionManager.getBalance() - amount;
        sessionManager.updateBalance(newBalance);
        userDAO.updateBalance(sessionManager.getId(), newBalance, FirebaseCallback.ignore());
    }

    public void getPaymentByOrderId(String orderId, FirebaseCallback<Payment> cb) {
        paymentDAO.findByOrderId(orderId, cb);
    }

    public void getPaymentById(String id, FirebaseCallback<Payment> cb) {
        paymentDAO.findById(id, cb);
    }

    public void findAll(FirebaseCallback<List<Payment>> cb) {
        paymentDAO.findAll(cb);
    }

    public double calculateServiceFee(double amount) {
        double fee = sessionManager.getServiceFee();
        return sessionManager.isServiceFeePercent() ? amount * (fee / 100.0) : fee;
    }
    public double calculateTotal(double amount) { return amount + calculateServiceFee(amount); }

    public void loadServiceFeeSettings(FirebaseCallback<Void> cb) {
        new SettingsDAO().getServiceFee(new FirebaseCallback<double[]>() {
            @Override public void onSuccess(double[] data) {
                sessionManager.setServiceFeeIsPercent(data[0] == 1.0);
                sessionManager.setServiceFee(data[1]);
                cb.onSuccess(null);
            }
            @Override public void onFailure(String error) { cb.onSuccess(null); }
        });
    }
}