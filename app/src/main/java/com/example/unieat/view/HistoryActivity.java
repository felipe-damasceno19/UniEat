package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.HistoryAdapter;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;
import com.example.unieat.presenter.HistoryPresenter;
import com.example.unieat.presenter.OrderPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class HistoryActivity extends BaseActivity implements HistoryPresenter.HistoryView {

    private HistoryPresenter presenter;
    private RecyclerView rvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        presenter = new HistoryPresenter(this, this);
        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        setupNavigation();
        presenter.getHistory();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_history);
    }

    @Override
    public void onHistoryLoaded(List<Order> orders) {
        rvHistory.setAdapter(new HistoryAdapter(this, orders, new HistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onReorder(Order order) {
                OrderPresenter orderPresenter = new OrderPresenter(HistoryActivity.this);
                for (OrderItem item : order.getItems()) {
                    orderPresenter.addItem(item.getDish());
                }
                startActivity(new Intent(HistoryActivity.this, OrderActivity.class));
            }

            @Override
            public void onDetails(Order order) {
                Intent intent = new Intent(HistoryActivity.this, OrderStatusActivity.class);
                intent.putExtra("order_id", order.getId());
                startActivity(intent);
            }
        }));
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}