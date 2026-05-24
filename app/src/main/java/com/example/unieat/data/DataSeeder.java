package com.example.unieat.data;

import com.example.unieat.dao.DishDAO;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.enums.FoodType;
import com.example.unieat.enums.UserType;
import com.example.unieat.model.Dish;
import com.example.unieat.model.User;

import java.util.List;
import java.util.UUID;

public class DataSeeder {


    public static void seed() {
        seedUsers();
        seedDishes();
    }

    private static void seedUsers() {
        UserDAO userDAO = new UserDAO();

        userDAO.findByUsername("felipe123", new FirebaseCallback<User>() {
            @Override public void onSuccess(User user) {
                if (user != null) return;

                userDAO.insert(new User(
                        UUID.randomUUID().toString(),
                        "Felipe", "felipe123", "felipe321",
                        "felipe@email.com", 50.00, UserType.ALUNO
                ), FirebaseCallback.ignore());

                userDAO.insert(new User(
                        UUID.randomUUID().toString(),
                        "Cozinha", "cozinha", "123456",
                        "cozinha@email.com", 0.0, UserType.COZINHEIRO
                ), FirebaseCallback.ignore());
            }
            @Override public void onFailure(String error) {}
        });
    }

    private static void seedDishes() {
        DishDAO dishDAO = new DishDAO();

        dishDAO.findAll(new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) {
                if (!dishes.isEmpty()) return; // já foi populado

                dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                        "Executivo do Dia", "Arroz, feijão, bife acebolado e batata.",
                        15.90, FoodType.REFEICAO, true), new FirebaseCallback<String>() {
                    @Override public void onSuccess(String result) {}
                    @Override public void onFailure(String erro) {}
                });

                dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                        "Feijoada Completa", "Feijoada, arroz, couve, farofa e laranja.",
                        19.90, FoodType.REFEICAO, true), FirebaseCallback.ignore());

                dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                        "UniBurguer Classic", "Blend 150g, cheddar e brioche.",
                        22.50, FoodType.SANDUICHE_NATURAL, true), FirebaseCallback.ignore());

                dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                        "Chicken Crispy", "Frango empanado e maionese.",
                        18.90, FoodType.SANDUICHE_NATURAL, true), FirebaseCallback.ignore());

                dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                        "Suco Natural 500ml", "Laranja ou Maracujá fresco.",
                        8.50, FoodType.BEBIDA_QUENTE, true), FirebaseCallback.ignore());

                dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                        "Refrigerante Lata", "Coca-Cola, Guaraná ou Sprite.",
                        6.00, FoodType.BEBIDA_QUENTE, true), FirebaseCallback.ignore());
            }
            @Override public void onFailure(String error) {}
        });
    }
}