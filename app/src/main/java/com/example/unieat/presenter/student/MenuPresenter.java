package com.example.unieat.presenter.student;

import com.example.unieat.dao.DishDAO;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;

import java.util.List;

public class MenuPresenter {

    public interface View {
        void showDishes(List<Dish> dishes);
        void showEmptyState(String message);
        void showError(String message);
    }

    private final View view;
    private final DishDAO dishDAO;

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

    public void filterByType(FoodType foodType) {
        if (foodType == null) {
            loadAvailableDishes();
            return;
        }
        dishDAO.getDishesByType(foodType, new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) {
                if (dishes == null || dishes.isEmpty())
                    view.showEmptyState("Nenhum prato encontrado nessa categoria.");
                else
                    view.showDishes(dishes);
            }
            @Override public void onFailure(String error) { view.showError("Erro ao filtrar cardápio."); }
        });
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