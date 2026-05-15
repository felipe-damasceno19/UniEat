package com.example.unieat.view;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;

import java.util.List;
import java.util.Locale;

public class KitchenAllOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerAllOrders;
    private KitchenOrderAdapter adapter;
    private OrderDAO orderDAO;
    private OrderStatus currentFilter = null;

    private TextView tvNewCount, tvInPrepCount, tvReadyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_all_orders);

        orderDAO = new OrderDAO(this);
        bindViews();
        setupFilters();

        findViewById(R.id.imgBack).setOnClickListener(v -> finish());

        loadOrders();
    }

    private void bindViews() {
        recyclerAllOrders = findViewById(R.id.recyclerAllOrders);
        recyclerAllOrders.setLayoutManager(new LinearLayoutManager(this));
        
        tvNewCount = findViewById(R.id.tvNewCount);
        tvInPrepCount = findViewById(R.id.tvInPrepCount);
        tvReadyCount = findViewById(R.id.tvReadyCount);
    }

    private void setupFilters() {
        findViewById(R.id.cardFilterNew).setOnClickListener(v -> updateFilter(OrderStatus.PENDENTE));
        findViewById(R.id.cardFilterInPrep).setOnClickListener(v -> updateFilter(OrderStatus.PREPARANDO));
        findViewById(R.id.cardFilterReady).setOnClickListener(v -> updateFilter(OrderStatus.PRONTO));
        
        findViewById(R.id.topBar).setOnClickListener(v -> updateFilter(null));
    }

    private void updateFilter(OrderStatus status) {
        currentFilter = (currentFilter == status) ? null : status;
        loadOrders();
    }

    private void loadOrders() {
        // Update counts
        tvNewCount.setText(String.format(Locale.getDefault(), "%02d", orderDAO.countByStatus(OrderStatus.PENDENTE)));
        tvInPrepCount.setText(String.format(Locale.getDefault(), "%02d", orderDAO.countByStatus(OrderStatus.PREPARANDO)));
        tvReadyCount.setText(String.format(Locale.getDefault(), "%02d", orderDAO.countByStatus(OrderStatus.PRONTO)));

        List<Order> orders = (currentFilter == null) ? orderDAO.findAll() : orderDAO.findByStatus(currentFilter);

        if (adapter == null) {
            adapter = new KitchenOrderAdapter(this, orders, this::advanceStatus);
            recyclerAllOrders.setAdapter(adapter);
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
        loadOrders();
    }
}
