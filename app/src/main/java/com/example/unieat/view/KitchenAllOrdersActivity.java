package com.example.unieat.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.KitchenOrderAdapter;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class KitchenAllOrdersActivity extends BaseActivity {

    private RecyclerView recyclerAllOrders;
    private KitchenOrderAdapter adapter;
    private OrderDAO orderDAO;
    private OrderStatus currentFilter = null;

    private TextView filterTodos;
    private TextView filterNovos;
    private TextView filterEmPreparo;
    private TextView filterProntos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_all_orders);

        orderDAO = new OrderDAO();
        bindViews();
        setupFilters();
        setupNavigation();
        loadOrders();

        findViewById(R.id.imgHistory).setOnClickListener(v ->
                startActivity(new Intent(this, KitchenHistoryActivity.class)));
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupKitchenNavigation(this, bottomNav, R.id.nav_kitchen_history);
    }

    private void bindViews() {
        recyclerAllOrders = findViewById(R.id.recyclerAllOrders);
        recyclerAllOrders.setLayoutManager(new LinearLayoutManager(this));
        filterTodos = findViewById(R.id.filterTodos);
        filterNovos = findViewById(R.id.filterNovos);
        filterEmPreparo = findViewById(R.id.filterEmPreparo);
        filterProntos = findViewById(R.id.filterProntos);
    }

    private void setupFilters() {
        filterTodos.setOnClickListener(v -> {
            currentFilter = null;
            updateFilterSelection(filterTodos);
            loadOrders();
        });
        filterNovos.setOnClickListener(v -> {
            currentFilter = OrderStatus.PENDENTE;
            updateFilterSelection(filterNovos);
            loadOrders();
        });
        filterEmPreparo.setOnClickListener(v -> {
            currentFilter = OrderStatus.PREPARANDO;
            updateFilterSelection(filterEmPreparo);
            loadOrders();
        });
        filterProntos.setOnClickListener(v -> {
            currentFilter = OrderStatus.PRONTO;
            updateFilterSelection(filterProntos);
            loadOrders();
        });
    }

    private void updateFilterSelection(TextView selected) {
        TextView[] all = {filterTodos, filterNovos, filterEmPreparo, filterProntos};
        for (TextView tv : all) {
            if (tv == selected) {
                tv.setBackgroundResource(R.drawable.shape_filter_chip_selected);
                tv.setTextColor(0xFFFFFFFF);
            } else {
                tv.setBackgroundResource(R.drawable.shape_filter_chip_default);
                tv.setTextColor(0xFF444444);
            }
        }
    }

    private void loadOrders() {
        FirebaseCallback<List<Order>> listCallback = new FirebaseCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> orders) {
                if (adapter == null) {
                    adapter = new KitchenOrderAdapter(KitchenAllOrdersActivity.this, orders,
                            new KitchenOrderAdapter.OnOrderActionListener() {
                                @Override public void onChangeStatus(Order order, OrderStatus newStatus) {
                                    changeStatus(order.getId(), newStatus);
                                }
                                @Override public void onReject(Order order) {
                                    changeStatus(order.getId(), OrderStatus.REJEITADO);
                                }
                            });
                    recyclerAllOrders.setAdapter(adapter);
                } else {
                    adapter.updateData(orders);
                }
            }
            @Override public void onFailure(String error) {}
        };

        if (currentFilter == null) {
            orderDAO.findAll(listCallback);
        } else {
            orderDAO.findByStatus(currentFilter, listCallback);
        }
    }

    private void changeStatus(String orderId, OrderStatus newStatus) {
        orderDAO.updateStatus(orderId, newStatus, new FirebaseCallback<Void>() {
            @Override public void onSuccess(Void v) { loadOrders(); }
            @Override public void onFailure(String error) {}
        });
    }
}
