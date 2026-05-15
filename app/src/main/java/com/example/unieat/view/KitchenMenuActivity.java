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
import com.example.unieat.model.Dish;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Locale;

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
            Toast.makeText(this, "Cadastrar novo prato (em breve)", Toast.LENGTH_SHORT).show();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupKitchenNavigation(this, bottomNavigationView, R.id.nav_kitchen_menu);

        loadDishes();
    }

    private void loadDishes() {
        List<Dish> dishes = dishDAO.findAll();
        dishContainer.removeAllViews();

        for (Dish dish : dishes) {
            addDishCard(dish);
        }
    }

    private void addDishCard(Dish dish) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_kitchen_dish, dishContainer, false);

        TextView tvName = card.findViewById(R.id.tvDishName);
        TextView tvPrice = card.findViewById(R.id.tvDishPrice);
        TextView tvDescription = card.findViewById(R.id.tvDishDescription);
        TextView tvCategory = card.findViewById(R.id.tvCategory);
        SwitchMaterial switchAvailable = card.findViewById(R.id.switchAvailable);
        ImageView btnDelete = card.findViewById(R.id.btnDelete);
        ImageView btnEdit = card.findViewById(R.id.btnEdit);

        tvName.setText(dish.getName());
        tvPrice.setText(String.format(Locale.getDefault(), "R$ %.2f", dish.getPrice()));
        tvDescription.setText(dish.getDescription());
        tvCategory.setText(dish.getType().name());
        switchAvailable.setChecked(dish.isAvailable());
        switchAvailable.setText(dish.isAvailable() ? "Disponível" : "Indisponível");

        switchAvailable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dishDAO.updateAvailability(dish.getId(), isChecked);
            switchAvailable.setText(isChecked ? "Disponível" : "Indisponível");
        });

        btnDelete.setOnClickListener(v -> {
            dishDAO.delete(dish.getId());
            loadDishes();
            Toast.makeText(this, "Prato removido", Toast.LENGTH_SHORT).show();
        });

        btnEdit.setOnClickListener(v -> {
            Toast.makeText(this, "Editar prato: " + dish.getName(), Toast.LENGTH_SHORT).show();
        });

        dishContainer.addView(card);
    }
}
