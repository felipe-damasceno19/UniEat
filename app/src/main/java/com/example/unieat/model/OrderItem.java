package com.example.unieat.model;

import java.util.UUID;

public class OrderItem {

    private UUID id;

    private Dish dish;

    private Integer quantity;

    public OrderItem() {
    }

    public OrderItem(UUID id, Integer quantity, Dish dish) {
        this.id = id;
        this.quantity = quantity;
        this.dish = dish;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", dish=" + dish +
                ", quantity=" + quantity +
                '}';
    }
}
