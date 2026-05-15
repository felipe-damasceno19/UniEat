package com.example.unieat.view;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.KitchenOrderAdapter;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;

public class KitchenHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders;
    private KitchenOrderAdapter adapter;
    private OrderDAO orderDAO;
    private OrderStatus currentFilter = null;

    private TextView tvNewCount, tvInPrepCount, tvReadyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_home);

        orderDAO = new OrderDAO(this);
        bindViews();
        setupFilters();
        setupNavigation();

        findViewById(R.id.tvViewAll).setOnClickListener(v -> {
            updateFilter(null);
            Toast.makeText(this, "Mostrando todos os pedidos", Toast.LENGTH_SHORT).show();
        });

        loadDashboard();
    }

    private void bindViews() {
        recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        
        tvNewCount = findViewById(R.id.tvNewCount);
        tvInPrepCount = findViewById(R.id.tvInPrepCount);
        tvReadyCount = findViewById(R.id.tvReadyCount);
    }

    private void setupFilters() {
        findViewById(R.id.cardFilterNew).setOnClickListener(v -> updateFilter(OrderStatus.PENDENTE));
        findViewById(R.id.cardFilterInPrep).setOnClickListener(v -> updateFilter(OrderStatus.PREPARANDO));
        findViewById(R.id.cardFilterReady).setOnClickListener(v -> updateFilter(OrderStatus.PRONTO));
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupKitchenNavigation(this, bottomNavigationView, R.id.nav_kitchen_home);
    }

    private void updateFilter(OrderStatus status) {
        if (currentFilter == status) {
            currentFilter = null;
        } else {
            currentFilter = status;
        }
        loadDashboard();
    }

    private void loadDashboard() {
        // Update counts
        tvNewCount.setText(String.format(Locale.getDefault(), "%02d", orderDAO.countByStatus(OrderStatus.PENDENTE)));
        tvInPrepCount.setText(String.format(Locale.getDefault(), "%02d", orderDAO.countByStatus(OrderStatus.PREPARANDO)));
        tvReadyCount.setText(String.format(Locale.getDefault(), "%02d", orderDAO.countByStatus(OrderStatus.PRONTO)));

        List<Order> orders;
        if (currentFilter == null) {
            orders = orderDAO.findAll();
        } else {
            orders = orderDAO.findByStatus(currentFilter);
        }

        if (adapter == null) {
            adapter = new KitchenOrderAdapter(this, orders, this::advanceStatus);
            recyclerOrders.setAdapter(adapter);
        } else {
            adapter.updateData(orders);
        }
    }

    private void advanceStatus(Order order) {
        OrderStatus next = order.getStatus();
        if (order.getStatus() == OrderStatus.PENDENTE) next = OrderStatus.PREPARANDO;
        else if (order.getStatus() == OrderStatus.PREPARANDO) next = OrderStatus.PRONTO;
        else if (order.getStatus() == OrderStatus.PRONTO) next = OrderStatus.ENTREGUE;

        orderDAO.updateStatus(order.getId(), next);
        loadDashboard();
        Toast.makeText(this, "Status do pedido atualizado", Toast.LENGTH_SHORT).show();
    }
}
