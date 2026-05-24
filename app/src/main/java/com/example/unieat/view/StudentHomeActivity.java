package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.DishCardAdapter;
import com.example.unieat.model.Dish;
import com.example.unieat.presenter.OrderPresenter;
import com.example.unieat.presenter.StudentHomePresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class StudentHomeActivity extends BaseActivity
        implements StudentHomePresenter.StudentHomeView {

    private StudentHomePresenter presenter;
    private OrderPresenter orderPresenter;
    private RecyclerView rvFeatured;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        presenter      = new StudentHomePresenter(this, this);
        orderPresenter = new OrderPresenter(this);

        setupNavigation();
        setupViews();
        loadData();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_home);

        FloatingActionButton fabCart = findViewById(R.id.fabCart);
        fabCart.setOnClickListener(v ->
                startActivity(new Intent(this, OrderActivity.class))
        );
    }

    private void setupViews() {
        TextView tvWelcome = findViewById(R.id.tvWelcomeName);
        TextView tvBalance = findViewById(R.id.tvBalance);

        tvWelcome.setText(presenter.getWelcomeMessage());
        tvBalance.setText(String.format("R$ %.2f", presenter.getBalance()));

        rvFeatured = findViewById(R.id.rvFeaturedDishes);
        rvFeatured.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
    }

    private void loadData() {
        presenter.getFeaturedDishes();
    }


    @Override
    public void onFeaturedDishesLoaded(List<Dish> dishes) {
        rvFeatured.setAdapter(new DishCardAdapter(this, dishes, dish -> {
            orderPresenter.addItem(dish);
            startActivity(new Intent(this, OrderActivity.class));
        }));
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}