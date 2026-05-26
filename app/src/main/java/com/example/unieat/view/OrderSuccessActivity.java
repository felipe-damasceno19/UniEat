package com.example.unieat.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unieat.R;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.util.OrderUtils;
import com.google.firebase.database.ValueEventListener;

public class OrderSuccessActivity extends BaseActivity {

    private String orderId;
    private String dishId;
    private OrderDAO orderDAO;
    private ValueEventListener statusListener;
    private SessionManager sessionManager;

    private ImageView ivStepRecebido, ivStepPreparando, ivStepPronto, ivStepEntregue;
    private TextView tvStepRecebido, tvStepPreparando, tvStepPronto, tvStepEntregue;
    private View vLine1, vLine2, vLine3;
    private TextView tvOrderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        orderId = getIntent().getStringExtra("order_id");
        dishId  = getIntent().getStringExtra("dish_id");
        sessionManager = new SessionManager(this);
        orderDAO = new OrderDAO();

        bindStepperViews();
        setupViews();
        setupListeners();
        startListeningStatus();
    }

    private void bindStepperViews() {
        ivStepRecebido   = findViewById(R.id.ivStepRecebido);
        ivStepPreparando = findViewById(R.id.ivStepPreparando);
        ivStepPronto     = findViewById(R.id.ivStepPronto);
        ivStepEntregue   = findViewById(R.id.ivStepEntregue);
        tvStepRecebido   = findViewById(R.id.tvStepRecebido);
        tvStepPreparando = findViewById(R.id.tvStepPreparando);
        tvStepPronto     = findViewById(R.id.tvStepPronto);
        tvStepEntregue   = findViewById(R.id.tvStepEntregue);
        vLine1           = findViewById(R.id.vLine1);
        vLine2           = findViewById(R.id.vLine2);
        vLine3           = findViewById(R.id.vLine3);
        tvOrderStatus    = findViewById(R.id.tvOrderStatus);
    }

    private void setupViews() {
        TextView tvOrderNumber = findViewById(R.id.tvOrderNumber);
        if (orderId != null) {
            tvOrderNumber.setText(OrderUtils.formatOrderNumber(orderId));
        }

        TextView tvBalance = findViewById(R.id.tvBalance);
        if (tvBalance != null) {
            tvBalance.setText(String.format("R$ %.2f", sessionManager.getBalance()));
        }
    }

    private void setupListeners() {
        findViewById(R.id.btnBackToHomeIcon).setOnClickListener(v -> {
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

        Button btnTrackOrder = findViewById(R.id.btnTrackOrder);
        btnTrackOrder.setVisibility(View.VISIBLE);
        btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderStatusActivity.class);
            intent.putExtra("order_id", orderId);
            startActivity(intent);
        });
    }

    private void startListeningStatus() {
        if (orderId == null) return;
        statusListener = orderDAO.listenToOrder(orderId, new FirebaseCallback<Order>() {
            @Override public void onSuccess(Order order) {
                if (order == null) return;
                updateStepper(order.getStatus());
                if (order.getStatus() == OrderStatus.ENTREGUE) {
                    sessionManager.clearActiveOrderId();
                }
            }
            @Override public void onFailure(String error) {}
        });
    }

    private void updateStepper(OrderStatus status) {
        int activeBg   = Color.parseColor("#7B1C1C");
        int inactiveBg = Color.parseColor("#F0E8E8");
        int activeIcon   = Color.WHITE;
        int inactiveIcon = Color.parseColor("#888888");
        int activeText   = Color.parseColor("#7B1C1C");
        int inactiveText = Color.parseColor("#888888");
        int activeLine   = Color.parseColor("#7B1C1C");
        int inactiveLine = Color.parseColor("#DDDDDD");

        boolean s2 = status == OrderStatus.PREPARANDO || status == OrderStatus.PRONTO || status == OrderStatus.ENTREGUE;
        boolean s3 = status == OrderStatus.PRONTO     || status == OrderStatus.ENTREGUE;
        boolean s4 = status == OrderStatus.ENTREGUE;

        setStep(ivStepRecebido,   tvStepRecebido,   true, activeBg, inactiveBg, activeIcon, inactiveIcon, activeText, inactiveText);
        setStep(ivStepPreparando, tvStepPreparando, s2,   activeBg, inactiveBg, activeIcon, inactiveIcon, activeText, inactiveText);
        setStep(ivStepPronto,     tvStepPronto,     s3,   activeBg, inactiveBg, activeIcon, inactiveIcon, activeText, inactiveText);
        setStep(ivStepEntregue,   tvStepEntregue,   s4,   activeBg, inactiveBg, activeIcon, inactiveIcon, activeText, inactiveText);

        vLine1.setBackgroundColor(s2 ? activeLine : inactiveLine);
        vLine2.setBackgroundColor(s3 ? activeLine : inactiveLine);
        vLine3.setBackgroundColor(s4 ? activeLine : inactiveLine);

        switch (status) {
            case PENDENTE:
                tvOrderStatus.setText("Recebido pela cozinha");
                tvOrderStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF5E1")));
                tvOrderStatus.setTextColor(Color.parseColor("#B0893F"));
                break;
            case PREPARANDO:
                tvOrderStatus.setText("Em preparo...");
                tvOrderStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF5E1")));
                tvOrderStatus.setTextColor(Color.parseColor("#B0893F"));
                break;
            case PRONTO:
                tvOrderStatus.setText("Pronto para retirada!");
                tvOrderStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
                tvOrderStatus.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case ENTREGUE:
                tvOrderStatus.setText("Pedido entregue!");
                tvOrderStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E3F2FD")));
                tvOrderStatus.setTextColor(Color.parseColor("#1565C0"));
                break;
        }
    }

    private void setStep(ImageView iv, TextView tv, boolean active,
                         int activeBg, int inactiveBg,
                         int activeIcon, int inactiveIcon,
                         int activeText, int inactiveText) {
        iv.setBackgroundTintList(ColorStateList.valueOf(active ? activeBg : inactiveBg));
        iv.setImageTintList(ColorStateList.valueOf(active ? activeIcon : inactiveIcon));
        tv.setTextColor(active ? activeText : inactiveText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (statusListener != null && orderId != null) {
            orderDAO.removeOrderListener(orderId, statusListener);
        }
    }
}
