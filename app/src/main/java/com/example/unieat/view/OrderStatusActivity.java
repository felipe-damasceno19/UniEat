package com.example.unieat.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.adapter.OrderStatusAdapter;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.presenter.OrderStatusPresenter;
import com.example.unieat.util.DateUtils;
import com.example.unieat.util.OrderUtils;

public class OrderStatusActivity extends BaseActivity
        implements OrderStatusPresenter.OrderStatusView {

    private OrderStatusPresenter presenter;
    private TextView tvOrderNumber, tvOrderDate, tvCurrentStatus, tvTotal;
    private RecyclerView rvOrderItems;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        orderId   = getIntent().getStringExtra("order_id");
        presenter = new OrderStatusPresenter(this);

        bindViews();
        setupListeners();

        if (orderId != null) {
            presenter.listenToOrder(orderId, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderId != null) {
            presenter.stopListening(orderId);
        }
    }

    private void bindViews() {
        tvOrderNumber       = findViewById(R.id.tvOrderNumber);
        tvOrderDate         = findViewById(R.id.tvOrderDate);
        tvCurrentStatus     = findViewById(R.id.tvCurrentStatus);
        tvTotal             = findViewById(R.id.tvTotal);
        rvOrderItems        = findViewById(R.id.rvOrderItems);
    }

    private void setupListeners() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }


    @Override
    public void onOrderLoaded(Order order) {
        tvOrderNumber.setText("Pedido #" + orderId.substring(0, 4).toUpperCase());
        tvOrderDate.setText(presenter.formatDate(order.getTime()));
        tvCurrentStatus.setText(getStatusDescription(order.getStatus()));

        double total = 0;
        for (com.example.unieat.model.OrderItem item : order.getItems()) {
            total += item.getDish().getPrice() * item.getQuantity();
        }
        tvTotal.setText(String.format("R$ %.2f", total));

        rvOrderItems.setAdapter(new OrderStatusAdapter(order.getItems()));
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private String getStatusDescription(OrderStatus status) {
        switch (status) {
            case PENDENTE:   return "Seu pedido foi recebido pela cozinha!";
            case PREPARANDO: return "Aguarde um momento...";
            case PRONTO:     return "Dirija-se ao balcão para retirar!";
            case ENTREGUE:   return "Bom apetite!";
            default:         return "";
        }
    }
}