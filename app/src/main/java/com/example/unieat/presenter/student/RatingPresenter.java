package com.example.unieat.presenter.student;

import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.RatingDAO;
import com.example.unieat.model.Rating;

import java.util.List;
import java.util.UUID;

public class RatingPresenter {

    public interface RatingView {
        void onSubmitSuccess();
        void onSubmitError(String message);
        void onRatingsLoaded(List<Rating> ratings, double average);
    }

    private final RatingDAO ratingDAO;
    private final RatingView view;

    public RatingPresenter(RatingView view) {
        this.ratingDAO = new RatingDAO();
        this.view = view;
    }

    public void submitRating(String dishId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            view.onSubmitError("Avaliação deve ser entre 1 e 5");
            return;
        }

        String finalComment = (comment == null) ? "" : comment.trim();

        Rating avaliation = new Rating(
                UUID.randomUUID().toString(),
                dishId,
                rating,
                finalComment
        );

        ratingDAO.insert(avaliation, new FirebaseCallback<String>() {
            @Override public void onSuccess(String id) { view.onSubmitSuccess(); }
            @Override public void onFailure(String error) { view.onSubmitError("Erro ao enviar avaliação: " + error); }
        });
    }

    public void getRatingsByDishId(String dishId) {
        ratingDAO.findByDishId(dishId, new FirebaseCallback<List<Rating>>() {
            @Override public void onSuccess(List<Rating> ratings) {
                double average = 0;
                if (!ratings.isEmpty()) {
                    double sum = 0;
                    for (Rating r : ratings) sum += r.getRating();
                    average = sum / ratings.size();
                }
                view.onRatingsLoaded(ratings, average);
            }
            @Override public void onFailure(String error) { view.onSubmitError(error); }
        });
    }
}