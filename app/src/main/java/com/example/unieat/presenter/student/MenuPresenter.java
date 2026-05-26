package com.example.unieat.presenter.student;

import com.example.unieat.dao.DishDAO;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuPresenter {

    public interface View {
        void showDishes(List<Dish> dishes);
        void showEmptyState(String message);
        void showError(String message);
    }

    private final View view;
    private final DishDAO dishDAO;
    private ValueEventListener dishListener;

    public MenuPresenter(View view, DishDAO dishDAO) {
        this.view = view;
        this.dishDAO = dishDAO;
    }

    public void loadAvailableDishes() {
        dishDAO.getAvailableDishes(new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) {
                if (dishes == null || dishes.isEmpty())
                    view.showEmptyState("Nenhum prato disponível no momento.");
                else
                    view.showDishes(dishes);
            }
            @Override public void onFailure(String error) { view.showError("Erro ao carregar cardápio."); }
        });
    }

    public void filterByType(List<FoodType> types) {
        dishDAO.getAvailableDishes(new FirebaseCallback<List<Dish>>() {
            @Override
            public void onSuccess(List<Dish> dishes) {
                List<Dish> filtered = new ArrayList<>();
                for (Dish d : dishes) {
                    if (types.contains(d.getType())) {
                        filtered.add(d);
                    }
                }
                if (filtered.isEmpty()) {
                    view.showEmptyState("Nenhum prato encontrado nessa categoria.");
                } else {
                    view.showDishes(filtered);
                }
            }

            @Override
            public void onFailure(String error) {
                view.showError("Erro ao filtrar cardápio.");
            }
        });
    }

    public void startListeningDishes() {
        dishListener = dishDAO.listenToAvailableDishes(new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) {
                if (dishes == null || dishes.isEmpty())
                    view.showEmptyState("Nenhum prato disponível no momento.");
                else
                    view.showDishes(dishes);
            }
            @Override public void onFailure(String error) { view.showError("Erro ao carregar cardápio."); }
        });
    }

    public void stopListeningDishes() {
        if (dishListener != null) {
            dishDAO.removeListener(dishListener);
            dishListener = null;
        }
    }

    public void searchByName(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadAvailableDishes();
            return;
        }
        dishDAO.searchByName(query.trim(), new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) {
                if (dishes == null || dishes.isEmpty())
                    view.showEmptyState("Nenhum prato encontrado para \"" + query + "\".");
                else
                    view.showDishes(dishes);
            }
            @Override public void onFailure(String error) { view.showError("Erro ao buscar prato."); }
        });
    }
}