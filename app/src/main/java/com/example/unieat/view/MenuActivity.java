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
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;
import com.example.unieat.presenter.OrderPresenter;
import com.example.unieat.presenter.student.MenuPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements MenuPresenter.View {

    private MenuPresenter presenter;
    private OrderPresenter orderPresenter;
    private DishDAO dishDAO;
    private RecyclerView rvMainDishes, rvSnacks, rvDrinks, rvDesserts;
    private TextView tvBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        dishDAO = new DishDAO();
        presenter = new MenuPresenter(this, dishDAO);
        orderPresenter = new OrderPresenter(this);

        setupNavigation();
        bindViews();
        setupSearch();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startListeningDishes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopListeningDishes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvBalance.setText(String.format("R$ %.2f", new SessionManager(this).getBalance()));
        loadData();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_menu);
    }

    private void bindViews() {
        rvMainDishes = findViewById(R.id.rvMainDishes);
        rvSnacks     = findViewById(R.id.rvSnacks);
        rvDrinks     = findViewById(R.id.rvDrinks);
        rvDesserts   = findViewById(R.id.rvDesserts);
        tvBalance    = findViewById(R.id.tvBalance);

        rvMainDishes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSnacks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDrinks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDesserts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        tvBalance.setText(String.format("R$ %.2f", new SessionManager(this).getBalance()));
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) loadData();
                else presenter.searchByName(s.toString());
            }
        });
    }

    private void loadData() {
        dishDAO.getDishesByType(FoodType.REFEICAO, new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) {
                setAdapter(rvMainDishes, dishes);
            }
            @Override public void onFailure(String e) {}
        });

        FoodType[] snackTypes = {
                FoodType.SANDUICHE_NATURAL, FoodType.SALGADO_ASSADO,
                FoodType.SALGADO_FRITO, FoodType.SNACK,
                FoodType.TAPIOCA, FoodType.CUSCUZ
        };
        loadMerged(snackTypes, rvSnacks);

        FoodType[] drinkTypes = {FoodType.BEBIDA_QUENTE, FoodType.BEBIDA_GELADA};
        loadMerged(drinkTypes, rvDrinks);

        FoodType[] dessertTypes = {FoodType.SOBREMESA_GELADA, FoodType.DOCE_CAKE};
        loadMerged(dessertTypes, rvDesserts);
    }

    // Busca múltiplos tipos e junta na mesma RecyclerView
    private void loadMerged(FoodType[] types, RecyclerView recyclerView) {
        List<Dish> merged = new ArrayList<>();
        final int[] remaining = {types.length};

        for (FoodType type : types) {
            dishDAO.getDishesByType(type, new FirebaseCallback<List<Dish>>() {
                @Override public void onSuccess(List<Dish> dishes) {
                    merged.addAll(dishes);
                    remaining[0]--;
                    if (remaining[0] == 0) setAdapter(recyclerView, merged);
                }
                @Override public void onFailure(String e) {
                    remaining[0]--;
                    if (remaining[0] == 0) setAdapter(recyclerView, merged);
                }
            });
        }
    }

    private void setAdapter(RecyclerView rv, List<Dish> dishes) {
        rv.setAdapter(new DishCardAdapter(this, dishes, dish -> {
            orderPresenter.addItem(dish);
            startActivity(new Intent(this, OrderActivity.class));
        }));
    }

    // MenuPresenter.View callbacks
    @Override
    public void showDishes(List<Dish> dishes) {
        setAdapter(rvMainDishes, dishes);
        rvSnacks.setAdapter(null);
        rvDrinks.setAdapter(null);
        rvDesserts.setAdapter(null);
    }

    @Override public void showEmptyState(String message) {
        rvMainDishes.setAdapter(null);
        rvSnacks.setAdapter(null);
        rvDrinks.setAdapter(null);
        rvDesserts.setAdapter(null);
    }

    @Override public void showError(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}