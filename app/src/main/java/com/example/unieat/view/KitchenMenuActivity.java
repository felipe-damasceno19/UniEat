package com.example.unieat.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unieat.R;
import com.example.unieat.dao.DishDAO;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class KitchenMenuActivity extends AppCompatActivity {

    private LinearLayout dishContainer;
    private DishDAO dishDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_menu);

        dishDAO = new DishDAO(this);
        dishContainer = findViewById(R.id.dishContainer);

        findViewById(R.id.btnAddDish).setOnClickListener(v -> {
            Toast.makeText(this, "Add new menu item", Toast.LENGTH_SHORT).show();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupKitchenNavigation(this, bottomNavigationView, R.id.nav_kitchen_menu);

        loadDishes();
    }

    private void loadDishes() {
        List<Dish> dishes = dishDAO.findAll();
        
        if (dishes.isEmpty()) {
            addPlaceholders();
            dishes = dishDAO.findAll();
        }

        dishContainer.removeAllViews();
        for (Dish dish : dishes) {
            addDishCard(dish);
        }
    }

    private void addPlaceholders() {
        dishDAO.insert(new Dish(UUID.randomUUID().toString(), "Classic Margherita", "Pizza tradicional com manjericão fresco.", 32.90, FoodType.REFEICAO));
        dishDAO.insert(new Dish(UUID.randomUUID().toString(), "Double Smash Burger", "Dois blends suculentos com queijo cheddar.", 28.50, FoodType.SANDUICHE_NATURAL));
        dishDAO.insert(new Dish(UUID.randomUUID().toString(), "Fresh Garden Salad", "Mix de folhas verdes e molho da casa.", 22.00, FoodType.REFEICAO));
        dishDAO.insert(new Dish(UUID.randomUUID().toString(), "Gourmet Cappuccino", "Café premium com espuma cremosa.", 12.50, FoodType.BEBIDA_QUENTE));
        dishDAO.insert(new Dish(UUID.randomUUID().toString(), "Pasta Carbonara", "Massa italiana com bacon e ovos.", 35.00, FoodType.REFEICAO));
        dishDAO.insert(new Dish(UUID.randomUUID().toString(), "Suco de Laranja 500ml", "Suco natural e refrescante.", 8.00, FoodType.BEBIDA_GELADA));
        dishDAO.insert(new Dish(UUID.randomUUID().toString(), "Torta de Chocolate", "Sobremesa rica em cacau.", 15.00, FoodType.DOCE_CAKE));
    }

    private void addDishCard(Dish dish) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_kitchen_dish, dishContainer, false);

        TextView tvName = cardView.findViewById(R.id.tvDishName);
        TextView tvPrice = cardView.findViewById(R.id.tvDishPrice);
        TextView tvStockStatus = cardView.findViewById(R.id.tvStockStatus);
        SwitchMaterial switchAvailable = cardView.findViewById(R.id.switchAvailable);
        View cardDish = cardView.findViewById(R.id.cardDish);

        tvName.setText(dish.getName());
        tvPrice.setText(String.format(Locale.getDefault(), "R$ %.2f", dish.getPrice()));
        
        updateStockStatus(tvStockStatus, dish.isAvailable());
        switchAvailable.setChecked(dish.isAvailable());

        switchAvailable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dishDAO.updateAvailability(dish.getId(), isChecked);
            updateStockStatus(tvStockStatus, isChecked);
        });

        cardDish.setOnClickListener(v -> {
            Toast.makeText(this, "Editing: " + dish.getName(), Toast.LENGTH_SHORT).show();
            // Here you would navigate to an EditDishActivity
        });

        dishContainer.addView(cardView);
    }

    private void updateStockStatus(TextView tvStatus, boolean isAvailable) {
        if (isAvailable) {
            tvStatus.setText("IN STOCK");
            tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            tvStatus.setText("OUT OF STOCK");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
}
