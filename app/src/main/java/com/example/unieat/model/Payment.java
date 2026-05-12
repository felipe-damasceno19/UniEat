package com.example.unieat.model;

import com.example.unieat.enums.PaymentMethod;

import java.util.Date;

public class Payment {

    private String id;
    private String orderId;
    private PaymentMethod method;
    private double amount;
    private Date time;

    public Payment() {
    }

    public Payment(String id, String orderId, PaymentMethod method, double amount, Date time) {
        this.id = id;
        this.orderId = orderId;
        this.method = method;
        this.amount = amount;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}


