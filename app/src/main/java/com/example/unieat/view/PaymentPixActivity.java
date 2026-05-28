package com.example.unieat.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.unieat.R;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.SettingsDAO;
import com.example.unieat.enums.PaymentMethod;
import com.example.unieat.presenter.student.PaymentPresenter;
import android.widget.Toast;

public class PaymentPixActivity extends BaseActivity {

    private String pixKey   = "";
    private String pixQrUrl = "";

    private PaymentPresenter presenter;
    private double orderAmount;

    private TextView tvAmountTop, tvPixKey;
    private ImageView imgQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_pix);

        presenter   = new PaymentPresenter(this);
        orderAmount = getIntent().getDoubleExtra("order_amount", 0.0);

        tvAmountTop = findViewById(R.id.tvAmountTop);
        imgQrCode   = findViewById(R.id.imgQrCode);
        tvPixKey    = findViewById(R.id.tvPixKey);

        tvAmountTop.setText(String.format("R$ %.2f", orderAmount));

        new SettingsDAO().getPixInfo(new FirebaseCallback<String[]>() {
            @Override public void onSuccess(String[] info) {
                pixKey   = info[0];
                pixQrUrl = info[1];
                bindPixData();
            }
            @Override public void onFailure(String error) {
                bindPixData();
            }
        });

        setupListeners();
    }

    private void bindPixData() {
        tvPixKey.setText(pixKey.isEmpty() ? "Chave não cadastrada" : pixKey);

        if (!pixQrUrl.isEmpty()) {
            Glide.with(this)
                    .load(pixQrUrl)
                    .placeholder(R.drawable.shape_logo_placeholder)
                    .error(R.drawable.shape_logo_placeholder)
                    .into(imgQrCode);
        } else {
            imgQrCode.setImageResource(R.drawable.shape_logo_placeholder);
        }
    }

    private void setupListeners() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageView btnCopyIcon = findViewById(R.id.btnCopyIcon);
        btnCopyIcon.setOnClickListener(v -> copyToClipboard());

        Button btnCopyKey = findViewById(R.id.btnCopyKey);
        btnCopyKey.setOnClickListener(v -> copyToClipboard());

        Button btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnConfirmOrder.setOnClickListener(v -> {
            String orderId = getIntent().getStringExtra("order_id");
            btnConfirmOrder.setEnabled(false);
            presenter.processPayment(orderId, PaymentMethod.PIX, orderAmount,
                new PaymentPresenter.PaymentView() {
                    @Override public void onPaymentSuccess(com.example.unieat.model.Payment p) {
                        new com.example.unieat.data.SessionManager(PaymentPixActivity.this).setActiveOrderId(orderId);
                        Toast.makeText(PaymentPixActivity.this, "Pagamento via PIX confirmado!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PaymentPixActivity.this, OrderSuccessActivity.class);
                        intent.putExtra("order_id",  orderId);
                        intent.putExtra("dish_id",   getIntent().getStringExtra("dish_id"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    @Override public void onPaymentError(String message) {
                        btnConfirmOrder.setEnabled(true);
                        Toast.makeText(PaymentPixActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
        });

        TextView tvChangePayment = findViewById(R.id.tvChangePayment);
        tvChangePayment.setOnClickListener(v -> finish());
    }

    private void copyToClipboard() {
        if (pixKey.isEmpty()) {
            Toast.makeText(this, "Nenhuma chave PIX cadastrada", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Chave Pix", pixKey));
        Toast.makeText(this, "Chave PIX copiada!", Toast.LENGTH_SHORT).show();
    }
}
