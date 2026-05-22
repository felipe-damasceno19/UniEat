package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.OrderItemAdapter;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;
import com.example.unieat.presenter.OrderPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrderActivity extends BaseActivity {

    private OrderPresenter presenter;
    private OrderItemAdapter adapter;
    private TextView tvSubtotal, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        presenter = new OrderPresenter(this);

        setupNavigation();
        setupViews();
        setupRecyclerView();
    }

    private void setupNavigation() {
        ImageView btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> finish());

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_orders);
    }

    private void setupViews() {
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotal = findViewById(R.id.tvTotal);

        Button btnGoToPayment = findViewById(R.id.btnGoToPayment);
        btnGoToPayment.setOnClickListener(v -> {
            if (presenter.isCartEmpty()) {
                Toast.makeText(this, "Seu carrinho está vazio", Toast.LENGTH_SHORT).show();
                return;
            }
            String annotation = ((EditText) findViewById(R.id.etAnnotation))
                    .getText().toString().trim();

            Order order = presenter.placeOrder(annotation);

            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("order_amount", presenter.calculateTotal());
            intent.putExtra("order_id", order.getId());

            if(!presenter.getCart().isEmpty()) {
                intent.putExtra("dish_id", presenter.getCart().get(0).getDish().getId());
            }
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        RecyclerView rvItems = findViewById(R.id.rvOrderItems);
        adapter = new OrderItemAdapter(this, presenter.getCart(), new OrderItemAdapter.OnQuantityChangeListener() {
            @Override
            public void onIncrease(OrderItem item) {
                presenter.changeQuantity(item, item.getQuantity() + 1);
                updateTotals();
            }

            @Override
            public void onDecrease(OrderItem item) {
                presenter.changeQuantity(item, item.getQuantity() - 1);
                updateTotals();
            }
        });
        rvItems.setAdapter(adapter);
        updateTotals();
    }

    private void updateTotals() {
        double total = presenter.calculateTotal();
        tvSubtotal.setText(String.format("R$ %.2f", total));
        tvTotal.setText(String.format("R$ %.2f", total));
    }
}
