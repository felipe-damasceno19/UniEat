package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.DishDAO;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.data.SessionManager;
import com.example.unieat.model.Dish;

import java.util.List;

public class StudentHomePresenter {

    public interface StudentHomeView {
        void onFeaturedDishesLoaded(List<Dish> dishes);
        void onError(String message);
    }

    private final DishDAO dishDAO;
    private final SessionManager sessionManager;
    private final StudentHomeView view;

    public StudentHomePresenter(Context context, StudentHomeView view) {
        this.dishDAO = new DishDAO();
        this.sessionManager = new SessionManager(context);
        this.view = view;
    }

    public String getWelcomeMessage() {
        return "Olá, " + sessionManager.getName() + "!";
    }

    public double getBalance() {
        return sessionManager.getBalance();
    }

    public void getFeaturedDishes() {
        dishDAO.getAvailableDishes(new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) {
                view.onFeaturedDishesLoaded(dishes);
            }
            @Override public void onFailure(String error) {
                view.onError("Erro ao carregar pratos: " + error);
            }
        });
    }
}