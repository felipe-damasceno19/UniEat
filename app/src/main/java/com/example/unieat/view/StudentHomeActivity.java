package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.DishCardAdapter;
import com.example.unieat.dao.DishDAO;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;
import com.example.unieat.presenter.OrderPresenter;
import com.example.unieat.presenter.StudentHomePresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.UUID;

public class StudentHomeActivity extends AppCompatActivity {

    private StudentHomePresenter presenter;
    private OrderPresenter orderPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

//        DishDAO dishDAO = new DishDAO(this);
//        if (dishDAO.findAll().isEmpty()) {
//            dishDAO.insert(new Dish(UUID.randomUUID().toString(), "Grelhado Imperial",
//                    "Frango com arroz integral", 18.90, FoodType.REFEICAO));
//            dishDAO.insert(new Dish(UUID.randomUUID().toString(), "Lasanha de Berinjela",
//                    "Com queijo coalho", 16.50, FoodType.REFEICAO));
//            dishDAO.insert(new Dish(UUID.randomUUID().toString(), "Suco Natural",
//                    "Laranja ou acerola", 7.50, FoodType.BEBIDA_GELADA));
//        }

        presenter = new StudentHomePresenter(this);
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
    }

    private void loadData() {
        List<Dish> dishes = presenter.getFeaturedDishes();

        RecyclerView rvFeatured = findViewById(R.id.rvFeaturedDishes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false
        );
        rvFeatured.setLayoutManager(layoutManager);
        rvFeatured.setAdapter(new DishCardAdapter(this, dishes, dish -> {
            // adiciona ao carrinho e vai para OrderActivity
            orderPresenter.addItem(dish);
            startActivity(new Intent(this, OrderActivity.class));
        }));
    }
}
