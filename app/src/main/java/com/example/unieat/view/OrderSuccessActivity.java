package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import com.example.unieat.R;
import com.example.unieat.data.SessionManager;

public class OrderSuccessActivity extends BaseActivity {

    private String orderId;
    private String dishId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        orderId = getIntent().getStringExtra("order_id");
        dishId = getIntent().getStringExtra("dish_id");

        setupViews();
        setupListeners();
    }

    private void setupViews() {
        // número do pedido
        TextView tvOrderNumber = findViewById(R.id.tvOrderNumber);
        if (orderId != null) {
            tvOrderNumber.setText("#" + orderId.substring(0, 4).toUpperCase());
        }

        // saldo atualizado
        TextView tvBalance = findViewById(R.id.tvBalance);
        if (tvBalance != null) {
            SessionManager session = new SessionManager(this);
            tvBalance.setText(String.format("R$ %.2f", session.getBalance()));
        }
    }

    private void setupListeners() {
        Button btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        Button btnRate = findViewById(R.id.btnRate);
        btnRate.setOnClickListener(v -> {
            Intent intent = new Intent(this, RatingActivity.class);
            intent.putExtra("dish_id", dishId);
            startActivity(intent);
            finish();
        });
    }
}