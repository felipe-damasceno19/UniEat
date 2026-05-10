package com.example.unieat.model;

import com.example.unieat.enums.OrderStatus;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Order {

    private String id;

    private List<OrderItem> items;

    private OrderStatus status;

    private String annotation;

    private Date time;

    public Order() {
    }

    public Order(String id, List<OrderItem> items, OrderStatus status, Date time, String annotation) {
        this.id = id;
        this.items = items;
        this.status = status;
        this.time = time;
        this.annotation = annotation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", items=" + items +
                ", status=" + status +
                ", annotation='" + annotation + '\'' +
                ", time=" + time +
                '}';
    }
}
