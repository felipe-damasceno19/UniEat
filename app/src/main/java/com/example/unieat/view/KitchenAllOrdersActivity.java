package com.example.unieat.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.KitchenOrderAdapter;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;

import java.util.List;
import java.util.Locale;

public class KitchenAllOrdersActivity extends BaseActivity {

    private RecyclerView recyclerAllOrders;
    private KitchenOrderAdapter adapter;
    private OrderDAO orderDAO;
    private OrderStatus currentFilter = null;

    private TextView tvNewCount, tvInPrepCount, tvReadyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_all_orders);

        orderDAO = new OrderDAO();
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
        // contadores em paralelo
        orderDAO.countByStatus(OrderStatus.PENDENTE, new FirebaseCallback<Integer>() {
            @Override public void onSuccess(Integer count) {
                tvNewCount.setText(String.format(Locale.getDefault(), "%02d", count));
            }
            @Override public void onFailure(String error) {}
        });

        orderDAO.countByStatus(OrderStatus.PREPARANDO, new FirebaseCallback<Integer>() {
            @Override public void onSuccess(Integer count) {
                tvInPrepCount.setText(String.format(Locale.getDefault(), "%02d", count));
            }
            @Override public void onFailure(String error) {}
        });

        orderDAO.countByStatus(OrderStatus.PRONTO, new FirebaseCallback<Integer>() {
            @Override public void onSuccess(Integer count) {
                tvReadyCount.setText(String.format(Locale.getDefault(), "%02d", count));
            }
            @Override public void onFailure(String error) {}
        });

        // lista filtrada ou completa
        FirebaseCallback<List<Order>> listCallback = new FirebaseCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> orders) {
                if (adapter == null) {
                    adapter = new KitchenOrderAdapter(KitchenAllOrdersActivity.this, orders, KitchenAllOrdersActivity.this::advanceStatus);
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

    private void advanceStatus(Order order) {
        OrderStatus next;
        switch (order.getStatus()) {
            case PENDENTE:   next = OrderStatus.PREPARANDO; break;
            case PREPARANDO: next = OrderStatus.PRONTO;     break;
            case PRONTO:     next = OrderStatus.ENTREGUE;   break;
            default: return;
        }
        orderDAO.updateStatus(order.getId(), next, new FirebaseCallback<Void>() {
            @Override public void onSuccess(Void v) { loadOrders(); }
            @Override public void onFailure(String error) {}
        });
    }
}