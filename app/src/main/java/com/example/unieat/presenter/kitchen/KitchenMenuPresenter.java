package com.example.unieat.presenter.kitchen;

import com.example.unieat.dao.DishDAO;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.model.Dish;
import com.example.unieat.presenter.student.MenuPresenter;

import java.util.List;
import java.util.UUID;

public class KitchenMenuPresenter extends MenuPresenter {

    public interface KitchenView extends View {
        void showSaveSuccess();
        void showDeleteSuccess();
        void showValidationError(String message);
    }

    private final KitchenView kitchenView;
    private final DishDAO dishDAO;

    public KitchenMenuPresenter(KitchenView view, DishDAO dishDAO) {
        super(view, dishDAO);
        this.kitchenView = view;
        this.dishDAO = dishDAO;
    }

    public void saveDish(Dish dish) {
        if (!isValid(dish)) return;

        if (dish.getId() == null || dish.getId().isEmpty()) {
            dish.setId(UUID.randomUUID().toString());
            dishDAO.insert(dish, new FirebaseCallback<String>() {
                @Override public void onSuccess(String id) {
                    kitchenView.showSaveSuccess();
                    loadAvailableDishes();
                }
                @Override public void onFailure(String error) { kitchenView.showError("Erro ao salvar prato."); }
            });
        } else {
            dishDAO.update(dish, new FirebaseCallback<Void>() {
                @Override public void onSuccess(Void v) {
                    kitchenView.showSaveSuccess();
                    loadAvailableDishes();
                }
                @Override public void onFailure(String error) { kitchenView.showError("Erro ao atualizar prato."); }
            });
        }
    }

    public void getAllDishes() {
        dishDAO.findAll(new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) {
                if (dishes == null || dishes.isEmpty())
                    kitchenView.showEmptyState("Nenhum prato cadastrado.");
                else
                    kitchenView.showDishes(dishes);
            }
            @Override public void onFailure(String error) { kitchenView.showError("Erro ao carregar pratos."); }
        });
    }

    public void deleteDish(String dishId) {
        dishDAO.delete(dishId, new FirebaseCallback<Void>() {
            @Override public void onSuccess(Void v) {
                kitchenView.showDeleteSuccess();
                loadAvailableDishes();
            }
            @Override public void onFailure(String error) { kitchenView.showError("Erro ao deletar prato."); }
        });
    }

    public void toggleAvailability(Dish dish) {
        dishDAO.updateAvailability(dish.getId(), !dish.isAvailable(), new FirebaseCallback<Void>() {
            @Override public void onSuccess(Void v) { getAllDishes(); }
            @Override public void onFailure(String error) { kitchenView.showError("Erro ao atualizar disponibilidade."); }
        });
    }

    private boolean isValid(Dish dish) {
        if (dish.getName() == null || dish.getName().trim().isEmpty()) {
            kitchenView.showValidationError("Nome do prato é obrigatório");
            return false;
        }
        if (dish.getPrice() <= 0) {
            kitchenView.showValidationError("Preço deve ser maior que zero");
            return false;
        }
        if (dish.getType() == null) {
            kitchenView.showValidationError("Categoria é obrigatória");
            return false;
        }
        return true;
    }
}