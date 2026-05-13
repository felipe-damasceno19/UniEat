package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.AvaliationDAO;
import com.example.unieat.model.Avaliation;

import java.util.List;
import java.util.UUID;

public class AvaliationPresenter {
    private AvaliationDAO avaliationDAO;

    public AvaliationPresenter(Context context) {
        avaliationDAO = new AvaliationDAO(context);
    }

    public boolean submitRating(String dishId, int rating, String comment) {
        if(rating < 1 || rating > 5) return false;
        comment = (comment == null) ? "" : comment.trim();

        Avaliation avaliation = new Avaliation(
                UUID.randomUUID().toString(),
                dishId,
                rating,
                comment
        );

        avaliationDAO.insert(avaliation);
        return true;
    }

    public List<Avaliation> getRatingsByDishId(String dishId) {
        return avaliationDAO.findByDishId(dishId);
    }

    public double getAverageRating(String dishId){
        List<Avaliation> avaliations = avaliationDAO.findByDishId(dishId);
        if (avaliations.isEmpty()) return 0;
        
        double sum = 0;
        for(Avaliation rating : avaliations){
            sum += rating.getRating();
        }
        return sum / avaliations.size();
    }
}
