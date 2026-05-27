package com.example.unieat.view;

import android.content.Intent;
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

    private TextView tvDailyRevenue;
    private TextView tvDeliveredCount;
    private TextView tvActiveBadge;
    private TextView tvChipNew;
    private TextView tvChipInPrep;
    private TextView tvChipReady;

    private int pendingCount = 0;
    private int preparingCount = 0;
    private int readyCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_home);

        presenter = new KitchenHomePresenter(this);

        bindViews();
        setupFilters();
        setupNavigation();

        findViewById(R.id.tvViewAll).setOnClickListener(v -> {
            startActivity(new Intent(this, KitchenAllOrdersActivity.class));
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
        tvDailyRevenue = findViewById(R.id.tvDailyRevenue);
        tvDeliveredCount = findViewById(R.id.tvDeliveredCount);
        tvActiveBadge = findViewById(R.id.tvActiveBadge);
        tvChipNew = findViewById(R.id.tvChipNew);
        tvChipInPrep = findViewById(R.id.tvChipInPrep);
        tvChipReady = findViewById(R.id.tvChipReady);
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

    private void updateChips() {
        tvChipNew.setText(String.format(Locale.getDefault(), "● %02d Novos", pendingCount));
        tvChipInPrep.setText(String.format(Locale.getDefault(), "● %02d Em Preparo", preparingCount));
        tvChipReady.setText(String.format(Locale.getDefault(), "● %02d Prontos", readyCount));
        int total = pendingCount + preparingCount + readyCount;
        tvActiveBadge.setText(String.format(Locale.getDefault(), "%02d", total));
    }

    @Override
    public void showPendingCount(int count) {
        pendingCount = count;
        updateChips();
    }

    @Override
    public void showPreparingCount(int count) {
        preparingCount = count;
        updateChips();
    }

    @Override
    public void showReadyCount(int count) {
        readyCount = count;
        updateChips();
    }

    @Override
    public void showDailyRevenue(double revenue) {
        tvDailyRevenue.setText(String.format(Locale.getDefault(), "R$ %.2f", revenue));
    }

    @Override
    public void showDeliveredCount(int count) {
        tvDeliveredCount.setText(String.valueOf(count));
    }

    @Override
    public void showRecentOrders(List<Order> orders) {
        if (adapter == null) {
            adapter = new KitchenOrderAdapter(this, orders,
                    new KitchenOrderAdapter.OnOrderActionListener() {
                        @Override public void onChangeStatus(Order order, OrderStatus newStatus) {
                            presenter.setOrderStatus(order.getId(), newStatus);
                        }
                        @Override public void onReject(Order order) {
                            presenter.setOrderStatus(order.getId(), OrderStatus.REJEITADO);
                        }
                    });
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
