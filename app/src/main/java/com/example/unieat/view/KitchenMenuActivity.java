package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unieat.R;
import com.example.unieat.dao.DishDAO;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;
import com.example.unieat.presenter.kitchen.KitchenMenuPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Locale;

public class KitchenMenuActivity extends AppCompatActivity
        implements KitchenMenuPresenter.KitchenView {

    private LinearLayout dishContainer;
    private KitchenMenuPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_menu);

        presenter = new KitchenMenuPresenter(this, new DishDAO(this));
        dishContainer = findViewById(R.id.dishContainer);

        setupSearch();
        setupFilters();
        setupNavigation();

        findViewById(R.id.btnAddDish).setOnClickListener(v -> {
            // futuramente abre EditDishActivity com prato vazio
            Intent intent = new Intent(this, EditDishActivity.class);
            startActivity(intent);
        });

        presenter.loadAvailableDishes();
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.searchByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilters() {
        findViewById(R.id.chipAll).setOnClickListener(v ->
                presenter.loadAvailableDishes()
        );

        findViewById(R.id.chipMainDishes).setOnClickListener(v ->
                presenter.filterByType(FoodType.SALGADO_ASSADO)
        );

        findViewById(R.id.chipSnacks).setOnClickListener(v ->
                presenter.filterByType(FoodType.SALGADO_FRITO)
        );

        findViewById(R.id.chipBeverages).setOnClickListener(v ->
                presenter.filterByType(FoodType.SANDUICHE_NATURAL)
        );

        findViewById(R.id.chipBeverages).setOnClickListener(v ->
                presenter.filterByType(FoodType.REFEICAO)
        );

        findViewById(R.id.chipBeverages).setOnClickListener(v ->
                presenter.filterByType(FoodType.CUSCUZ)
        );

        findViewById(R.id.chipBeverages).setOnClickListener(v ->
                presenter.filterByType(FoodType.TAPIOCA)
        );

        findViewById(R.id.chipBeverages).setOnClickListener(v ->
                presenter.filterByType(FoodType.DOCE_CAKE)
        );

        findViewById(R.id.chipBeverages).setOnClickListener(v ->
                presenter.filterByType(FoodType.SNACK)
        );

        findViewById(R.id.chipBeverages).setOnClickListener(v ->
                presenter.filterByType(FoodType.BEBIDA_QUENTE)
        );

        findViewById(R.id.chipBeverages).setOnClickListener(v ->
                presenter.filterByType(FoodType.BEBIDA_GELADA)
        );

        findViewById(R.id.chipBeverages).setOnClickListener(v ->
                presenter.filterByType(FoodType.SOBREMESA_GELADA)
        );

    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupKitchenNavigation(this, bottomNav, R.id.nav_kitchen_menu);
    }

    private void renderDishes(List<Dish> dishes) {
        dishContainer.removeAllViews();
        for (Dish dish : dishes) {
            addDishCard(dish);
        }
    }

    private void addDishCard(Dish dish) {
        View cardView = LayoutInflater.from(this)
                .inflate(R.layout.item_kitchen_dish, dishContainer, false);

        TextView tvName = cardView.findViewById(R.id.tvDishName);
        TextView tvPrice = cardView.findViewById(R.id.tvDishPrice);
        TextView tvStockStatus = cardView.findViewById(R.id.tvStockStatus);
        SwitchMaterial switchAvailable = cardView.findViewById(R.id.switchAvailable);
        View cardDish = cardView.findViewById(R.id.cardDish);

        tvName.setText(dish.getName());
        tvPrice.setText(String.format(Locale.getDefault(), "R$ %.2f", dish.getPrice()));
        updateStockStatus(tvStockStatus, dish.isAvailable());
        switchAvailable.setChecked(dish.isAvailable());

        switchAvailable.setOnCheckedChangeListener((buttonView, isChecked) ->
                presenter.toggleAvailability(dish)
        );

        cardDish.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditDishActivity.class);
            intent.putExtra("dish_id", dish.getId());
            startActivity(intent);
        });

        dishContainer.addView(cardView);
    }

    private void updateStockStatus(TextView tvStatus, boolean isAvailable) {
        if (isAvailable) {
            tvStatus.setText("IN STOCK");
            tvStatus.setTextColor(0xFFAAAAAA);
        } else {
            tvStatus.setText("OUT OF STOCK");
            tvStatus.setTextColor(0xFFFF4444);
        }
    }

    // KitchenMenuPresenter.KitchenView callbacks
    @Override
    public void showDishes(List<Dish> dishes) {
        renderDishes(dishes);
    }

    @Override
    public void showEmptyState(String message) {
        dishContainer.removeAllViews();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSaveSuccess() {
        Toast.makeText(this, "Prato salvo com sucesso!", Toast.LENGTH_SHORT).show();
        presenter.loadAvailableDishes();
    }

    @Override
    public void showDeleteSuccess() {
        Toast.makeText(this, "Prato removido!", Toast.LENGTH_SHORT).show();
        presenter.loadAvailableDishes();
    }

    @Override
    public void showValidationError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}