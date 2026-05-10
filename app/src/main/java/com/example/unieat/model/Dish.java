package com.example.unieat.model;

import com.example.unieat.enums.FoodType;

public class Dish {

    private String id;

    private String name;

    private String description;

    private Double price;

    private FoodType type;

    private boolean available;

    public Dish(){
    }

    public Dish(String id,String name, String description, Double price, FoodType type){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.available = true;
    }

    public String getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public String getDescription() {
        return description;
    }

    public FoodType getType() {
        return type;
    }

    public Double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setType(FoodType type) {
        this.type = type;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", type=" + type +
                '}';
    }
}
