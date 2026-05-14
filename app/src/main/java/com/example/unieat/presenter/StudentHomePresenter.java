package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.DishDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.model.Dish;

import java.util.ArrayList;
import java.util.List;

public class StudentHomePresenter {

    private DishDAO dishDAO;
    private SessionManager sessionManager;

    public StudentHomePresenter(Context context) {
        dishDAO = new DishDAO(context);
        sessionManager = new SessionManager(context);
    }

    public String getWelcomeMessage() {
        return "Olá, " +sessionManager.getName() +" !";
    }

    public double getBalance() {
        return sessionManager.getBalance();
    }

    public List<Dish> getFeaturedDishes() {
        List<Dish> list = dishDAO.findAll();
        List<Dish> availableDishes = new ArrayList<>();

        for (Dish dish : list) {
            if(dish.isAvailable()){
                availableDishes.add(dish);
            }
        }

        return availableDishes;
    }

}
