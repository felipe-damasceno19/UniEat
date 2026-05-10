package com.example.unieat.model;

import com.example.unieat.enums.FoodType;

public class Dish {

    private String name;

    private Double price;

    private FoodType type;

    public Dish(){
    }

    public Dish(String name, Double price, FoodType type){
        this.name = name;
        this.price = price;
        this.type = type;
    }

    public String getName(){
        return name;
    }

    public FoodType getType() {
        return type;
    }

    public Double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setType(FoodType type) {
        this.type = type;
    }
}
