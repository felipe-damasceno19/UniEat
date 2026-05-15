package com.example.unieat.presenter.kitchen;

import com.example.unieat.dao.DishDAO;
import com.example.unieat.model.Dish;
import com.example.unieat.presenter.student.MenuPresenter;

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
        if(!isValid(dish)) return;

        try {
            if (dish.getId() == null || dish.getId().isEmpty()) {
                dish.setId(UUID.randomUUID().toString());
                dishDAO.insert(dish);
            } else {
                dishDAO.update(dish);
            }
            kitchenView.showSaveSuccess();
            loadAvailableDishes();
        } catch (Exception e){
            kitchenView.showError("Erro ao salvar prato");
        }
    }

    public void deleteDish(String dishId){
        try {
            dishDAO.delete(dishId);
            kitchenView.showDeleteSuccess();
            loadAvailableDishes();
        } catch(Exception e) {
            kitchenView.showError("Erro ao deletar prato");
        }
    }

    public void toggleAvailability(Dish dish) {
        try {
            dishDAO.updateAvailability(dish.getId(), !dish.isAvailable());
            loadAvailableDishes();
        } catch (Exception e) {
            kitchenView.showError("Erro ao atualizar disponibilidade");
        }
    }

    private boolean isValid(Dish dish) {
        if(dish.getName() == null || dish.getName().trim().isEmpty()) {
            kitchenView.showValidationError("Nome do prato é obrigatório");
            return false;
        }
        if(dish.getPrice() <= 0) {
            kitchenView.showValidationError("Preço deve ser maior que zero");
            return false;
        }
        if(dish.getType() == null) {
            kitchenView.showValidationError("Categoria é obrigatória");
            return false;
        }
        return true;
    }

}
