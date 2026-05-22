package com.example.unieat.data;

import android.content.Context;

import com.example.unieat.dao.DishDAO;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.enums.FoodType;
import com.example.unieat.enums.UserType;
import com.example.unieat.model.Dish;
import com.example.unieat.model.User;

import java.util.UUID;

public class DataSeeder {

    public static void seed(Context context) {
        seedUsers(context);
        seedDishes(context);
    }

    private static void seedUsers(Context context) {
        UserDAO userDAO = new UserDAO(context);

        if(userDAO.findByUsername("felipe") != null) return;

        userDAO.insert(new User(
                UUID.randomUUID().toString(),
                "Felipe",
                "felipe123",
                "felipe321",
                "felipe@email.com",
                50.00,
                UserType.ALUNO
        ));

        userDAO.insert(new User(
                UUID.randomUUID().toString(),
                "Cozinha",
                "cozinha",
                "123456",
                "cozinha@email.com",
                0.0,
                UserType.COZINHEIRO
        ));
    }

    private static void seedDishes(Context context) {
        DishDAO dishDAO = new DishDAO(context);

        if(!dishDAO.findAll().isEmpty()) return;

        dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                "Executivo do Dia", "Arroz, feijão, bife acebolado e batata.",
                15.90, FoodType.REFEICAO));

        dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                "Feijoada Completa", "Feijoada, arroz, couve, farofa e laranja.",
                19.90, FoodType.REFEICAO));

        dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                "UniBurguer Classic", "Blend 150g, cheddar e brioche.",
                22.50, FoodType.SANDUICHE_NATURAL));

        dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                "Chicken Crispy", "Frango empanado e maionese.",
                18.90, FoodType.SANDUICHE_NATURAL));

        dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                "Suco Natural 500ml", "Laranja ou Maracujá fresco.",
                8.50, FoodType.BEBIDA_QUENTE));

        dishDAO.insert(new Dish(UUID.randomUUID().toString(),
                "Refrigerante Lata", "Coca-Cola, Guaraná ou Sprite.",
                6.00, FoodType.BEBIDA_QUENTE));
    }
}
