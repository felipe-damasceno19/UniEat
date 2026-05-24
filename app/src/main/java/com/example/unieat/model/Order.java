package com.example.unieat.model;

import com.example.unieat.enums.OrderStatus;
import com.google.firebase.database.Exclude;

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

    @Exclude
    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Exclude
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

    @Exclude
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

    public long getTimeMillis() {
        return time != null ? time.getTime() : 0;
    }

    public void setTimeMillis(long millis) {
        this.time = new Date(millis);
    }
}
