package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.DishCardAdapter;
import com.example.unieat.dao.DishDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;
import com.example.unieat.presenter.OrderPresenter;
import com.example.unieat.presenter.student.MenuPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MenuActivity extends AppCompatActivity implements MenuPresenter.View {

    private MenuPresenter presenter;
    private OrderPresenter orderPresenter;
    private RecyclerView rvMainDishes, rvSnacks, rvDrinks, rvDesserts;
    private TextView tvBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        presenter = new MenuPresenter(this, new DishDAO(this));
        orderPresenter = new OrderPresenter(this);

        setupNavigation();
        bindViews();
        setupSearch();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_menu);
    }

    private void bindViews() {
        rvMainDishes = findViewById(R.id.rvMainDishes);
        rvSnacks = findViewById(R.id.rvSnacks);
        rvDrinks = findViewById(R.id.rvDrinks);
        rvDesserts = findViewById(R.id.rvDesserts);
        tvBalance = findViewById(R.id.tvBalance);

        rvMainDishes.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSnacks.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDrinks.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDesserts.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        SessionManager session = new SessionManager(this);
        tvBalance.setText(String.format("R$ %.2f", session.getBalance()));
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    loadData();
                } else {
                    presenter.searchByName(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        DishDAO dishDAO = new DishDAO(this);

        List<Dish> mainDishes = dishDAO.getDishesByType(FoodType.REFEICAO);

        List<Dish> snacks = dishDAO.getDishesByType(FoodType.SANDUICHE_NATURAL);
        snacks.addAll(dishDAO.getDishesByType(FoodType.SALGADO_ASSADO));
        snacks.addAll(dishDAO.getDishesByType(FoodType.SALGADO_FRITO));
        snacks.addAll(dishDAO.getDishesByType(FoodType.SNACK));
        snacks.addAll(dishDAO.getDishesByType(FoodType.TAPIOCA));
        snacks.addAll(dishDAO.getDishesByType(FoodType.CUSCUZ));

        List<Dish> drinks = dishDAO.getDishesByType(FoodType.BEBIDA_QUENTE);
        drinks.addAll(dishDAO.getDishesByType(FoodType.BEBIDA_GELADA));

        List<Dish> desserts = dishDAO.getDishesByType(FoodType.SOBREMESA_GELADA);
        desserts.addAll(dishDAO.getDishesByType(FoodType.DOCE_CAKE));

        rvMainDishes.setAdapter(new DishCardAdapter(this, mainDishes, dish -> {
            orderPresenter.addItem(dish);
            startActivity(new Intent(this, OrderActivity.class));
        }));

        rvSnacks.setAdapter(new DishCardAdapter(this, snacks, dish -> {
            orderPresenter.addItem(dish);
            startActivity(new Intent(this, OrderActivity.class));
        }));

        rvDrinks.setAdapter(new DishCardAdapter(this, drinks, dish -> {
            orderPresenter.addItem(dish);
            startActivity(new Intent(this, OrderActivity.class));
        }));

        rvDesserts.setAdapter(new DishCardAdapter(this, desserts, dish -> {
            orderPresenter.addItem(dish);
            startActivity(new Intent(this, OrderActivity.class));
        }));
    }

    // MenuPresenter.View callbacks
    @Override
    public void showDishes(List<Dish> dishes) {
        rvMainDishes.setAdapter(new DishCardAdapter(this, dishes, dish -> {
            orderPresenter.addItem(dish);
            startActivity(new Intent(this, OrderActivity.class));
        }));
        rvSnacks.setAdapter(null);
        rvDrinks.setAdapter(null);
        rvDesserts.setAdapter(null);
    }

    @Override
    public void showEmptyState(String message) {
        rvMainDishes.setAdapter(null);
        rvSnacks.setAdapter(null);
        rvDrinks.setAdapter(null);
        rvDesserts.setAdapter(null);
    }

    @Override
    public void showError(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}