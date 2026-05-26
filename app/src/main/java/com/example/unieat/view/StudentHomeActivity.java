package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.BannerAdapter;
import com.example.unieat.adapter.DishCardAdapter;
import com.example.unieat.model.Banner;
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
    private RecyclerView rvFeatured, rvRecentOrders, rvBanners;
    private TextView tvBalance;
    private TextView tvCartBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        presenter      = new StudentHomePresenter(this, this);
        orderPresenter = new OrderPresenter(this);

        setupNavigation();
        setupViews();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_home);

        FloatingActionButton fabCart = findViewById(R.id.fabCart);
        fabCart.setOnClickListener(v -> {
            if (!orderPresenter.isCartEmpty()) {
                startActivity(new Intent(this, OrderActivity.class));
            } else {
                String activeOrderId = new com.example.unieat.data.SessionManager(this).getActiveOrderId();
                if (activeOrderId != null) {
                    Intent intent = new Intent(this, OrderSuccessActivity.class);
                    intent.putExtra("order_id", activeOrderId);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, OrderActivity.class));
                }
            }
        });

        tvCartBadge = findViewById(R.id.tvCartBadge);
    }

    private void updateCartBadge() {
        int count = orderPresenter.getCartItemCount();
        if (count > 0) {
            tvCartBadge.setText(String.valueOf(count));
            tvCartBadge.setVisibility(View.VISIBLE);
        } else if (new com.example.unieat.data.SessionManager(this).getActiveOrderId() != null) {
            tvCartBadge.setText("!");
            tvCartBadge.setVisibility(View.VISIBLE);
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    private void setupViews() {
        TextView tvWelcome = findViewById(R.id.tvWelcomeName);
        tvWelcome.setText(presenter.getWelcomeMessage());

        tvBalance = findViewById(R.id.tvBalance);

        rvFeatured = findViewById(R.id.rvFeaturedDishes);
        rvFeatured.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        rvRecentOrders = findViewById(R.id.rvRecentOrders);
        rvRecentOrders.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        rvBanners = findViewById(R.id.rvBanners);
        rvBanners.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvBalance.setText(String.format("R$ %.2f", presenter.getBalance()));
        presenter.getRecentDishes();
        presenter.getBanners();
        updateCartBadge();
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
    public void onFeaturedDishesLoaded(List<Dish> dishes) {
        rvFeatured.setAdapter(new DishCardAdapter(this, dishes, dish -> {
            orderPresenter.addItem(dish);
            updateCartBadge();
            Toast.makeText(this, dish.getName() + " adicionado ao carrinho", Toast.LENGTH_SHORT).show();
        }));
    }

    @Override
    public void onRecentDishesLoaded(List<Dish> dishes) {
        rvRecentOrders.setAdapter(new DishCardAdapter(this, dishes, dish -> {
            orderPresenter.addItem(dish);
            updateCartBadge();
            Toast.makeText(this, dish.getName() + " adicionado ao carrinho", Toast.LENGTH_SHORT).show();
        }));
    }

    @Override
    public void onBannersLoaded(List<Banner> banners) {
        rvBanners.setAdapter(new BannerAdapter(this, banners, banner -> {
            Intent intent = new Intent(this, BannerProductsActivity.class);
            intent.putExtra("banner_id", banner.getId());
            startActivity(intent);
        }));
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
