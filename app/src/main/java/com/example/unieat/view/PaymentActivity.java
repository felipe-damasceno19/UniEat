package com.example.unieat.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.unieat.R;
import com.example.unieat.enums.PaymentMethod;
import com.example.unieat.presenter.PaymentPresenter;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;

public class PaymentActivity extends BaseActivity {

    private MaterialCardView cardPix, cardCash;
    private ImageView imgPix, imgCash;
    private Button btnConfirmOrder;
    private TextView tvOrderSubtotal, tvServiceFee, tvTotalAmount;

    private boolean isPixSelected = true;

    private PaymentPresenter presenter;
    private double orderAmount;

    private int colorSelected, colorDefault;
    private int iconBgSelected, iconBgDefault;
    private int iconTintSelected, iconTintDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        presenter   = new PaymentPresenter(this);
        orderAmount = getIntent().getDoubleExtra("order_amount", 0.0);

        initViews();
        setupColors();
        setupListeners();
        loadSummary();
        selectPix();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        cardPix          = findViewById(R.id.cardPix);
        cardCash         = findViewById(R.id.cardCash);
        imgPix           = findViewById(R.id.imgPix);
        imgCash          = findViewById(R.id.imgCash);
        btnConfirmOrder  = findViewById(R.id.btnConfirmOrder);
        tvOrderSubtotal  = findViewById(R.id.tvOrderSubtotal);
        tvServiceFee     = findViewById(R.id.tvServiceFee);
        tvTotalAmount    = findViewById(R.id.tvTotalAmount);
    }

    private void loadSummary() {
        double fee   = presenter.calculateServiceFee(orderAmount);
        double total = presenter.calculateTotal(orderAmount);
        tvOrderSubtotal.setText(String.format("R$ %.2f", orderAmount));
        tvServiceFee.setText(String.format("R$ %.2f", fee));
        tvTotalAmount.setText(String.format("R$ %.2f", total));
    }

    private void setupColors() {
        colorSelected   = Color.parseColor("#7B1C1C");
        colorDefault    = Color.parseColor("#EEEEEE");
        iconBgSelected  = Color.parseColor("#FFB84C");
        iconBgDefault   = Color.parseColor("#F0E8E8");
        iconTintSelected = Color.parseColor("#7B1C1C");
        iconTintDefault  = Color.parseColor("#666666");
    }

    private void setupListeners() {
        cardPix.setOnClickListener(v -> selectPix());
        cardCash.setOnClickListener(v -> selectCash());

        btnConfirmOrder.setOnClickListener(v -> {
            double total   = presenter.calculateTotal(orderAmount);
            String orderId = getIntent().getStringExtra("order_id");

            if (isPixSelected) {
                Intent intent = new Intent(this, PaymentPixActivity.class);
                intent.putExtra("order_amount", total);
                intent.putExtra("order_id", orderId);
                intent.putExtra("dish_id", getIntent().getStringExtra("dish_id"));
                startActivity(intent);
            } else {
                btnConfirmOrder.setEnabled(false);
                presenter.processPayment(orderId, PaymentMethod.CASH, total,
                    new PaymentPresenter.PaymentView() {
                        @Override public void onPaymentSuccess(com.example.unieat.model.Payment p) {
                            Toast.makeText(PaymentActivity.this, "Pagamento registrado!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PaymentActivity.this, OrderSuccessActivity.class);
                            intent.putExtra("dish_id",  getIntent().getStringExtra("dish_id"));
                            intent.putExtra("order_id", orderId);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        @Override public void onPaymentError(String message) {
                            btnConfirmOrder.setEnabled(true);
                            Toast.makeText(PaymentActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
    }

    private void selectPix() {
        isPixSelected = true;
        cardPix.setStrokeColor(colorSelected);
        cardPix.setStrokeWidth(6);
        imgPix.setBackgroundTintList(ColorStateList.valueOf(iconBgSelected));
        imgPix.setImageTintList(ColorStateList.valueOf(iconTintSelected));

        cardCash.setStrokeColor(colorDefault);
        cardCash.setStrokeWidth(2);
        imgCash.setBackgroundTintList(ColorStateList.valueOf(iconBgDefault));
        imgCash.setImageTintList(ColorStateList.valueOf(iconTintDefault));
    }

    private void selectCash() {
        isPixSelected = false;
        cardCash.setStrokeColor(colorSelected);
        cardCash.setStrokeWidth(6);
        imgCash.setBackgroundTintList(ColorStateList.valueOf(iconBgSelected));
        imgCash.setImageTintList(ColorStateList.valueOf(iconTintSelected));

        cardPix.setStrokeColor(colorDefault);
        cardPix.setStrokeWidth(2);
        imgPix.setBackgroundTintList(ColorStateList.valueOf(iconBgDefault));
        imgPix.setImageTintList(ColorStateList.valueOf(iconTintDefault));
    }
}
