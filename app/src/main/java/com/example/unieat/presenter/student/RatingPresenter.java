package com.example.unieat.presenter.student;

import android.content.Context;

import com.example.unieat.dao.RatingDAO;
import com.example.unieat.model.Rating;

import java.util.List;
import java.util.UUID;

public class RatingPresenter {
    private RatingDAO avaliationDAO;

    public RatingPresenter(Context context) {
        avaliationDAO = new RatingDAO(context);
    }

    public boolean submitRating(String dishId, int rating, String comment) {
        if(rating < 1 || rating > 5) return false;
        comment = (comment == null) ? "" : comment.trim();

        Rating avaliation = new Rating(
                UUID.randomUUID().toString(),
                dishId,
                rating,
                comment
        );

        avaliationDAO.insert(avaliation);
        return true;
    }

    public List<Rating> getRatingsByDishId(String dishId) {
        return avaliationDAO.findByDishId(dishId);
    }

    public double getAverageRating(String dishId){
        List<Rating> avaliations = avaliationDAO.findByDishId(dishId);
        if (avaliations.isEmpty()) return 0;
        
        double sum = 0;
        for(Rating rating : avaliations){
            sum += rating.getRating();
        }
        return sum / avaliations.size();
    }
}
