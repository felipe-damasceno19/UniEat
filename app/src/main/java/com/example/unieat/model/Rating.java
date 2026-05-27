package com.example.unieat.model;

public class Rating {

    private String id;
    private String dishId;
    private String userId;
    private Integer rating;
    private String comment;

    public Rating() {
    }

    public Rating(String id, String dishId, Integer rating, String comment) {
        this.id = id;
        this.dishId = dishId;
        this.rating = rating;
        this.comment = comment;
    }

    public Rating(String id, String dishId, String userId, Integer rating, String comment) {
        this.id = id;
        this.dishId = dishId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
