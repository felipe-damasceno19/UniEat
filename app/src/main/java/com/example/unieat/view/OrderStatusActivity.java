package com.example.unieat.view;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.OrderStatusAdapter;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.presenter.OrderStatusPresenter;
import com.example.unieat.util.DateUtils;
import com.example.unieat.util.OrderUtils;

public class OrderStatusActivity extends AppCompatActivity {

    private OrderStatusPresenter presenter;
    private TextView tvOrderNumber, tvOrderDate, tvCurrentStatus, tvStatusDescription, tvTotal;
    private RecyclerView rvOrderItems;
    private String orderId;

    private final Handler handler = new Handler();
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadOrder();
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        presenter = new OrderStatusPresenter(this);
        orderId = getIntent().getStringExtra("order_id");

        bindViews();
        setupListeners();
        loadOrder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(refreshRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
    }

    private void bindViews() {
        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);
        tvTotal = findViewById(R.id.tvTotal);
        rvOrderItems = findViewById(R.id.rvOrderItems);
    }

    private void setupListeners() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadOrder() {
        if (orderId == null) return;

        Order order = presenter.getOrderById(orderId);
        if (order == null) return;

        tvOrderNumber.setText("Pedido #" + orderId.substring(0, 4).toUpperCase());
        tvOrderDate.setText(DateUtils.formatDate(order.getTime()));
        tvCurrentStatus.setText(OrderUtils.formatStatus(order.getStatus()));
        tvStatusDescription.setText(getStatusDescription(order.getStatus()));

        double total = 0;
        for (com.example.unieat.model.OrderItem item : order.getItems()) {
            total += item.getDish().getPrice() * item.getQuantity();
        }
        tvTotal.setText(String.format("R$ %.2f", total));

        rvOrderItems.setAdapter(new OrderStatusAdapter(order.getItems()));
    }

    private String getStatusDescription(OrderStatus status) {
        switch (status) {
            case PENDENTE:   return "Seu pedido foi recebido pela cozinha!";
            case PREPARANDO: return "Aguarde um momento...";
            case PRONTO:     return "Dirija-se ao balcão para retirar!";
            case ENTREGUE: return "Bom apetite!";
            default:        return "";
        }
    }
}