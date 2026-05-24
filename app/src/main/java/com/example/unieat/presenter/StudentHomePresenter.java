package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.DishDAO;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.model.Dish;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentHomePresenter {

    public interface StudentHomeView {
        void onFeaturedDishesLoaded(List<Dish> dishes);
        void onRecentDishesLoaded(List<Dish> dishes);
        void onError(String message);
    }

    private final DishDAO dishDAO;
    private final OrderDAO orderDAO;
    private final SessionManager sessionManager;
    private final StudentHomeView view;
    private ValueEventListener dishListener;

    public StudentHomePresenter(Context context, StudentHomeView view) {
        this.dishDAO        = new DishDAO();
        this.orderDAO       = new OrderDAO();
        this.sessionManager = new SessionManager(context);
        this.view           = view;
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

    public void getRecentDishes() {
        String userId = sessionManager.getId();
        orderDAO.findAll(new FirebaseCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> orders) {
                List<Dish> recent = new ArrayList<>();
                Set<String> seen  = new HashSet<>();
                if (orders != null) {
                    for (Order o : orders) {
                        if (!userId.equals(o.getUserId())) continue;
                        for (OrderItem item : o.getItems()) {
                            Dish d = item.getDish();
                            if (d != null && d.getId() != null && seen.add(d.getId())) {
                                recent.add(d);
                                if (recent.size() >= 10) break;
                            }
                        }
                        if (recent.size() >= 10) break;
                    }
                }
                view.onRecentDishesLoaded(recent);
            }
            @Override public void onFailure(String error) {
                view.onRecentDishesLoaded(new ArrayList<>());
            }
        });
    }

    public void startListeningDishes() {
        dishListener = dishDAO.listenToAvailableDishes(new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) {
                view.onFeaturedDishesLoaded(dishes);
            }
            @Override public void onFailure(String error) {
                view.onError("Erro ao carregar pratos: " + error);
            }
        });
    }

    public void stopListeningDishes() {
        if (dishListener != null) {
            dishDAO.removeListener(dishListener);
            dishListener = null;
        }
    }
}
