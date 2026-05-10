package com.example.unieat.model;

import java.util.UUID;

public class Avaliation {

    private UUID dishId;

    private Integer rating;

    private String comment;

    public Avaliation() {
    }

    public Avaliation(UUID dishId, Integer rating, String comment) {
        this.dishId = dishId;
        this.rating = rating;
        this.comment = comment;
    }

    public UUID getDishId() {
        return dishId;
    }

    public void setDishId(UUID dishId) {
        this.dishId = dishId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Avaliation{" +
                "dishId=" + dishId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}
