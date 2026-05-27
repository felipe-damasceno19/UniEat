package com.example.unieat.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.KitchenHistoryAdapter;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.dao.PaymentDAO;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.model.Payment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class KitchenHistoryActivity extends BaseActivity {

    private RecyclerView recyclerHistory;
    private KitchenHistoryAdapter adapter;
    private OrderDAO orderDAO;
    private PaymentDAO paymentDAO;

    private TextView histFilterTodos;
    private TextView histFilterEntregues;
    private TextView histFilterRejeitados;
    private TextView histFilterHoje;
    private TextView tvStatOrders;
    private TextView tvStatRevenue;
    private TextView tvStatRejected;
    private EditText etSearch;

    private List<Order> allOrders = new ArrayList<>();
    private OrderStatus currentFilter = null;
    private boolean filterToday = false;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_history);

        orderDAO = new OrderDAO();
        paymentDAO = new PaymentDAO();

        bindViews();
        setupFilters();
        setupSearch();
        setupNavigation();
        loadOrders();
    }

    private void bindViews() {
        recyclerHistory = findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        histFilterTodos = findViewById(R.id.histFilterTodos);
        histFilterEntregues = findViewById(R.id.histFilterEntregues);
        histFilterRejeitados = findViewById(R.id.histFilterRejeitados);
        histFilterHoje = findViewById(R.id.histFilterHoje);
        tvStatOrders = findViewById(R.id.tvStatOrders);
        tvStatRevenue = findViewById(R.id.tvStatRevenue);
        tvStatRejected = findViewById(R.id.tvStatRejected);
        etSearch = findViewById(R.id.etSearch);
    }

    private void setupFilters() {
        histFilterTodos.setOnClickListener(v -> {
            currentFilter = null;
            filterToday = false;
            updateFilterSelection(histFilterTodos);
            applyFilter();
        });
        histFilterEntregues.setOnClickListener(v -> {
            currentFilter = OrderStatus.ENTREGUE;
            filterToday = false;
            updateFilterSelection(histFilterEntregues);
            applyFilter();
        });
        histFilterRejeitados.setOnClickListener(v -> {
            currentFilter = OrderStatus.REJEITADO;
            filterToday = false;
            updateFilterSelection(histFilterRejeitados);
            applyFilter();
        });
        histFilterHoje.setOnClickListener(v -> {
            currentFilter = null;
            filterToday = true;
            updateFilterSelection(histFilterHoje);
            applyFilter();
        });
    }

    private void updateFilterSelection(TextView selected) {
        TextView[] all = {histFilterTodos, histFilterEntregues, histFilterRejeitados, histFilterHoje};
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

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim().toLowerCase(Locale.getDefault());
                applyFilter();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupKitchenNavigation(this, bottomNav, R.id.nav_kitchen_history);
    }

    private void loadOrders() {
        orderDAO.findAll(new FirebaseCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> orders) {
                allOrders = orders;
                applyFilter();
                updateStats(orders);
            }
            @Override public void onFailure(String error) {}
        });
    }

    private void applyFilter() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfToday = cal.getTimeInMillis();

        List<Order> filtered = new ArrayList<>();
        for (Order order : allOrders) {
            if (currentFilter != null && order.getStatus() != currentFilter) continue;
            if (filterToday && order.getTime() != null && order.getTime().getTime() < startOfToday) continue;
            if (!searchQuery.isEmpty()) {
                String orderNum = order.getId() != null ? order.getId().toLowerCase(Locale.getDefault()) : "";
                if (!orderNum.contains(searchQuery)) continue;
            }
            filtered.add(order);
        }

        if (adapter == null) {
            adapter = new KitchenHistoryAdapter(this, filtered);
            recyclerHistory.setAdapter(adapter);
        } else {
            adapter.updateData(filtered);
        }
    }

    private void updateStats(List<Order> orders) {
        int total = orders.size();
        int rejected = 0;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.REJEITADO) rejected++;
        }
        tvStatOrders.setText(String.valueOf(total));
        tvStatRejected.setText(String.valueOf(rejected));

        paymentDAO.findAll(new FirebaseCallback<List<Payment>>() {
            @Override public void onSuccess(List<Payment> payments) {
                double revenue = 0;
                for (Payment p : payments) {
                    if (p != null) revenue += p.getAmount();
                }
                tvStatRevenue.setText(String.format(Locale.getDefault(), "R$ %.2f", revenue));
            }
            @Override public void onFailure(String error) {}
        });
    }
}
