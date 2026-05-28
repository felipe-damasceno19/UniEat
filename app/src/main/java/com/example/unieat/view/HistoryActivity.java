package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.HistoryAdapter;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;
import com.example.unieat.presenter.HistoryPresenter;
import com.example.unieat.presenter.OrderPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends BaseActivity implements HistoryPresenter.HistoryView {

    private HistoryPresenter presenter;
    private RecyclerView rvHistory;
    private TextView tvBalance;
    private List<Order> currentOrders = new ArrayList<>();
    private boolean sortNewestFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        presenter = new HistoryPresenter(this, this);
        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        tvBalance = findViewById(R.id.tvBalance);

        setupNavigation();
        setupSortButton();
    }

    private void setupSortButton() {
        ImageView btnSort = findViewById(R.id.btnSort);
        btnSort.setOnClickListener(v -> {
            sortNewestFirst = !sortNewestFirst;
            applySort();
        });
    }

    private void applySort() {
        List<Order> sorted = new ArrayList<>(currentOrders);
        if (sortNewestFirst) {
            Collections.sort(sorted, (a, b) -> b.getTime().compareTo(a.getTime()));
        } else {
            Collections.sort(sorted, (a, b) -> a.getTime().compareTo(b.getTime()));
        }
        showOrders(sorted);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvBalance.setText(String.format("R$ %.2f", new com.example.unieat.data.SessionManager(this).getBalance()));
        presenter.getHistory();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_history);
    }

    @Override
    public void onHistoryLoaded(List<Order> orders) {
        currentOrders = orders != null ? orders : new ArrayList<>();
        applySort();
    }

    private void showOrders(List<Order> orders) {
        rvHistory.setAdapter(new HistoryAdapter(this, orders, new HistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onReorder(Order order) {
                OrderPresenter orderPresenter = new OrderPresenter(HistoryActivity.this);
                for (OrderItem item : order.getItems()) {
                    if (item.getDish() != null) orderPresenter.addItem(item.getDish());
                }
                startActivity(new Intent(HistoryActivity.this, OrderActivity.class));
            }

            @Override
            public void onDetails(Order order) {
                Intent intent;
                if (order.getStatus() != OrderStatus.ENTREGUE) {
                    intent = new Intent(HistoryActivity.this, OrderSuccessActivity.class);
                } else {
                    intent = new Intent(HistoryActivity.this, OrderStatusActivity.class);
                }
                intent.putExtra("order_id", order.getId());
                startActivity(intent);
            }

            @Override
            public void onRate(Order order) {
                openRatingForOrder(order);
            }
        }));
    }

    private void openRatingForOrder(Order order) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            Toast.makeText(this, "Nenhum item encontrado no pedido", Toast.LENGTH_SHORT).show();
            return;
        }
        List<OrderItem> items = order.getItems();
        if (items.size() == 1) {
            launchRating(items.get(0).getDish().getId());
        } else {
            String[] names = new String[items.size()];
            for (int i = 0; i < items.size(); i++) {
                OrderItem item = items.get(i);
                names[i] = item.getDish() != null ? item.getDish().getName() : "Prato";
            }
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Qual item deseja avaliar?")
                    .setItems(names, (dialog, which) -> launchRating(items.get(which).getDish().getId()))
                    .show();
        }
    }

    private void launchRating(String dishId) {
        Intent intent = new Intent(this, RatingActivity.class);
        intent.putExtra("dish_id", dishId);
        startActivity(intent);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}