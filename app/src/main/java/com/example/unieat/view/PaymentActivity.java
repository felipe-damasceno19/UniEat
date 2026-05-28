package com.example.unieat.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unieat.R;
import com.example.unieat.enums.PaymentMethod;
import com.example.unieat.model.Order;
import com.example.unieat.model.Payment;
import com.example.unieat.presenter.OrderPresenter;
import com.example.unieat.presenter.student.PaymentPresenter;
import com.google.android.material.card.MaterialCardView;

public class PaymentActivity extends BaseActivity {

    private MaterialCardView cardPix, cardCash, cardBalance;
    private ImageView imgPix, imgCash, imgBalance;
    private Button btnConfirmOrder;
    private TextView tvOrderSubtotal, tvServiceFee, tvTotalAmount, tvBalanceAmount, tvBalance;

    private PaymentMethod selectedMethod = PaymentMethod.PIX;

    private PaymentPresenter presenter;
    private OrderPresenter orderPresenter;
    private double orderAmount;
    private String annotation;

    private int colorSelected, colorDefault;
    private int iconBgSelected, iconBgDefault;
    private int iconTintSelected, iconTintDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        presenter      = new PaymentPresenter(this);
        orderPresenter = new OrderPresenter(this);
        orderAmount    = getIntent().getDoubleExtra("order_amount", 0.0);
        annotation     = getIntent().getStringExtra("annotation");

        initViews();
        setupColors();
        setupListeners();
        loadSummary();
        presenter.loadServiceFeeSettings(new com.example.unieat.dao.FirebaseCallback<Void>() {
            @Override public void onSuccess(Void v) { loadSummary(); }
            @Override public void onFailure(String e) {}
        });
        selectPix();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        cardPix         = findViewById(R.id.cardPix);
        cardCash        = findViewById(R.id.cardCash);
        cardBalance     = findViewById(R.id.cardBalance);
        imgPix          = findViewById(R.id.imgPix);
        imgCash         = findViewById(R.id.imgCash);
        imgBalance      = findViewById(R.id.imgBalance);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        tvOrderSubtotal = findViewById(R.id.tvOrderSubtotal);
        tvServiceFee    = findViewById(R.id.tvServiceFee);
        tvTotalAmount   = findViewById(R.id.tvTotalAmount);
        tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
        tvBalance       = findViewById(R.id.tvBalance);
    }

    private void loadSummary() {
        double fee   = presenter.calculateServiceFee(orderAmount);
        double total = presenter.calculateTotal(orderAmount);
        tvOrderSubtotal.setText(String.format("R$ %.2f", orderAmount));
        tvServiceFee.setText(String.format("R$ %.2f", fee));
        tvTotalAmount.setText(String.format("R$ %.2f", total));
        tvBalanceAmount.setText(String.format("Disponível: R$ %.2f", presenter.getBalance()));
        tvBalance.setText(String.format("R$ %.2f", presenter.getBalance()));
    }

    private void setupColors() {
        colorSelected    = Color.parseColor("#7B1C1C");
        colorDefault     = Color.parseColor("#EEEEEE");
        iconBgSelected   = Color.parseColor("#FFB84C");
        iconBgDefault    = Color.parseColor("#F0E8E8");
        iconTintSelected = Color.parseColor("#7B1C1C");
        iconTintDefault  = Color.parseColor("#666666");
    }

    private void setupListeners() {
        cardPix.setOnClickListener(v -> selectPix());
        cardCash.setOnClickListener(v -> selectCash());
        cardBalance.setOnClickListener(v -> selectBalance());

        btnConfirmOrder.setOnClickListener(v -> {
            double total = presenter.calculateTotal(orderAmount);

            if (selectedMethod == PaymentMethod.PIX) {
                Intent intent = new Intent(this, PaymentPixActivity.class);
                intent.putExtra("order_amount", total);
                intent.putExtra("annotation", annotation);
                startActivity(intent);
            } else if (selectedMethod == PaymentMethod.CASH) {
                btnConfirmOrder.setEnabled(false);
                orderPresenter.placeOrder(annotation, new OrderPresenter.OrderView() {
                    @Override public void onOrderPlaced(Order order) {
                        presenter.processPayment(order.getId(), PaymentMethod.CASH, total,
                            new PaymentPresenter.PaymentView() {
                                @Override public void onPaymentSuccess(Payment p) {
                                    new com.example.unieat.data.SessionManager(PaymentActivity.this).setActiveOrderId(order.getId());
                                    Toast.makeText(PaymentActivity.this, "Pagamento registrado!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PaymentActivity.this, OrderSuccessActivity.class);
                                    intent.putExtra("order_id", order.getId());
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
                    @Override public void onOrderError(String message) {
                        btnConfirmOrder.setEnabled(true);
                        Toast.makeText(PaymentActivity.this, "Erro ao realizar pedido: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (!presenter.hasSufficientBalance(total)) {
                    Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show();
                    return;
                }
                btnConfirmOrder.setEnabled(false);
                orderPresenter.placeOrder(annotation, new OrderPresenter.OrderView() {
                    @Override public void onOrderPlaced(Order order) {
                        presenter.processPayment(order.getId(), PaymentMethod.BALANCE, total,
                            new PaymentPresenter.PaymentView() {
                                @Override public void onPaymentSuccess(Payment p) {
                                    presenter.deductBalance(total);
                                    new com.example.unieat.data.SessionManager(PaymentActivity.this).setActiveOrderId(order.getId());
                                    Toast.makeText(PaymentActivity.this, "Saldo debitado com sucesso!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PaymentActivity.this, OrderSuccessActivity.class);
                                    intent.putExtra("order_id", order.getId());
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
                    @Override public void onOrderError(String message) {
                        btnConfirmOrder.setEnabled(true);
                        Toast.makeText(PaymentActivity.this, "Erro ao realizar pedido: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void deselectAll() {
        MaterialCardView[] cards = {cardPix, cardCash, cardBalance};
        ImageView[] icons = {imgPix, imgCash, imgBalance};
        for (MaterialCardView card : cards) {
            card.setStrokeColor(colorDefault);
            card.setStrokeWidth(2);
        }
        for (ImageView img : icons) {
            img.setBackgroundTintList(ColorStateList.valueOf(iconBgDefault));
            img.setImageTintList(ColorStateList.valueOf(iconTintDefault));
        }
    }

    private void selectCard(MaterialCardView card, ImageView img) {
        deselectAll();
        card.setStrokeColor(colorSelected);
        card.setStrokeWidth(6);
        img.setBackgroundTintList(ColorStateList.valueOf(iconBgSelected));
        img.setImageTintList(ColorStateList.valueOf(iconTintSelected));
    }

    private void selectPix() {
        selectedMethod = PaymentMethod.PIX;
        selectCard(cardPix, imgPix);
    }

    private void selectCash() {
        selectedMethod = PaymentMethod.CASH;
        selectCard(cardCash, imgCash);
    }

    private void selectBalance() {
        selectedMethod = PaymentMethod.BALANCE;
        selectCard(cardBalance, imgBalance);
    }
}
