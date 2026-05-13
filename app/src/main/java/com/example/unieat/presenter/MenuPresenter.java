// presenter/MenuPresenter.java
package com.example.unieat.presenter;

import com.example.unieat.dao.DishDAO;
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
        try {
            List<Dish> dishes = dishDAO.getAvailableDishes();
            if (dishes == null || dishes.isEmpty()) {
                view.showEmptyState("Nenhum prato disponível no momento.");
            } else {
                view.showDishes(dishes);
            }
        } catch (Exception e) {
            view.showError("Erro ao carregar cardápio.");
        }
    }

    public void filterByType(FoodType foodType) {
        try {
            List<Dish> dishes;
            if (foodType == null) {
                // null = "Todos" — mostra tudo disponível
                dishes = dishDAO.getAvailableDishes();
            } else {
                dishes = dishDAO.getDishesByType(foodType);
            }

            if (dishes == null || dishes.isEmpty()) {
                view.showEmptyState("Nenhum prato encontrado nessa categoria.");
            } else {
                view.showDishes(dishes);
            }
        } catch (Exception e) {
            view.showError("Erro ao filtrar cardápio.");
        }
    }

    public void searchByName(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadAvailableDishes();
            return;
        }
        try {
            List<Dish> dishes = dishDAO.searchByName(query.trim());
            if (dishes == null || dishes.isEmpty()) {
                view.showEmptyState("Nenhum prato encontrado para \"" + query + "\".");
            } else {
                view.showDishes(dishes);
            }
        } catch (Exception e) {
            view.showError("Erro ao buscar prato.");
        }
    }
}