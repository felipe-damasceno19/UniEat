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
        void onExistingRatingLoaded(Rating rating);
        void onDeleteSuccess();
    }

    private final RatingDAO ratingDAO;
    private final RatingView view;

    public RatingPresenter(RatingView view) {
        this.ratingDAO = new RatingDAO();
        this.view = view;
    }

    public void findExistingRating(String dishId, String userId) {
        ratingDAO.findByDishIdAndUserId(dishId, userId, new FirebaseCallback<Rating>() {
            @Override public void onSuccess(Rating rating) { view.onExistingRatingLoaded(rating); }
            @Override public void onFailure(String error) { view.onExistingRatingLoaded(null); }
        });
    }

    public void submitRating(String dishId, String userId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            view.onSubmitError("Avaliação deve ser entre 1 e 5");
            return;
        }
        String finalComment = (comment == null) ? "" : comment.trim();
        Rating avaliation = new Rating(UUID.randomUUID().toString(), dishId, userId, rating, finalComment);
        ratingDAO.insert(avaliation, new FirebaseCallback<String>() {
            @Override public void onSuccess(String id) { view.onSubmitSuccess(); }
            @Override public void onFailure(String error) { view.onSubmitError("Erro ao enviar avaliação: " + error); }
        });
    }

    public void updateRating(String ratingId, String dishId, String userId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            view.onSubmitError("Avaliação deve ser entre 1 e 5");
            return;
        }
        String finalComment = (comment == null) ? "" : comment.trim();
        Rating updated = new Rating(ratingId, dishId, userId, rating, finalComment);
        ratingDAO.update(updated, new FirebaseCallback<Void>() {
            @Override public void onSuccess(Void v) { view.onSubmitSuccess(); }
            @Override public void onFailure(String error) { view.onSubmitError("Erro ao atualizar: " + error); }
        });
    }

    public void deleteRating(String ratingId) {
        ratingDAO.delete(ratingId, new FirebaseCallback<Void>() {
            @Override public void onSuccess(Void v) { view.onDeleteSuccess(); }
            @Override public void onFailure(String error) { view.onSubmitError("Erro ao excluir: " + error); }
        });
    }

    public void getRatingsByDishId(String dishId) {
        ratingDAO.findByDishId(dishId, new FirebaseCallback<List<Rating>>() {
            @Override public void onSuccess(List<Rating> ratings) {
                double average = 0;
                if (!ratings.isEmpty()) {
                    double sum = 0;
                    for (Rating r : ratings) if (r.getRating() != null) sum += r.getRating();
                    average = sum / ratings.size();
                }
                view.onRatingsLoaded(ratings, average);
            }
            @Override public void onFailure(String error) { view.onSubmitError(error); }
        });
    }
}
