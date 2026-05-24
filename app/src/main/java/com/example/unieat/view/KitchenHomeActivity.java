package com.example.unieat.view;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.KitchenOrderAdapter;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.presenter.kitchen.KitchenHomePresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;

public class KitchenHomeActivity extends BaseActivity
        implements KitchenHomePresenter.View {

    private RecyclerView recyclerOrders;
    private KitchenOrderAdapter adapter;
    private KitchenHomePresenter presenter;
    private OrderStatus currentFilter = null;

    private TextView tvNewCount, tvInPrepCount, tvReadyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_home);

        presenter = new KitchenHomePresenter(this);

        bindViews();
        setupFilters();
        setupNavigation();

        findViewById(R.id.tvViewAll).setOnClickListener(v -> {
            currentFilter = null;
            presenter.loadDashboard();
        });

        presenter.loadDashboard();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopListening();
    }

    private void bindViews() {
        recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        tvNewCount = findViewById(R.id.tvNewCount);
        tvInPrepCount = findViewById(R.id.tvInPrepCount);
        tvReadyCount = findViewById(R.id.tvReadyCount);
    }

    private void setupFilters() {
        findViewById(R.id.cardFilterNew).setOnClickListener(v -> {
            currentFilter = currentFilter == OrderStatus.PENDENTE
                    ? null : OrderStatus.PENDENTE;
            presenter.loadOrdersByStatus(currentFilter);
        });

        findViewById(R.id.cardFilterInPrep).setOnClickListener(v -> {
            currentFilter = currentFilter == OrderStatus.PREPARANDO
                    ? null : OrderStatus.PREPARANDO;
            presenter.loadOrdersByStatus(currentFilter);
        });

        findViewById(R.id.cardFilterReady).setOnClickListener(v -> {
            currentFilter = currentFilter == OrderStatus.PRONTO
                    ? null : OrderStatus.PRONTO;
            presenter.loadOrdersByStatus(currentFilter);
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupKitchenNavigation(this, bottomNav, R.id.nav_kitchen_home);
    }

    @Override
    public void showPendingCount(int count) {
        tvNewCount.setText(String.format(Locale.getDefault(), "%02d", count));
    }

    @Override
    public void showPreparingCount(int count) {
        tvInPrepCount.setText(String.format(Locale.getDefault(), "%02d", count));
    }

    @Override
    public void showReadyCount(int count) {
        tvReadyCount.setText(String.format(Locale.getDefault(), "%02d", count));
    }

    @Override
    public void showRecentOrders(List<Order> orders) {
        if (adapter == null) {
            adapter = new KitchenOrderAdapter(this, orders, order ->
                    presenter.advanceOrderStatus(order.getId())
            );
            recyclerOrders.setAdapter(adapter);
        } else {
            adapter.updateData(orders);
        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}