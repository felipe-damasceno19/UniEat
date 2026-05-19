package com.example.unieat.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unieat.R;
import com.example.unieat.presenter.PaymentPresenter;

public class PaymentPixActivity extends AppCompatActivity {

    private String pixKey = "12.345.678/0001-99"; // sua chave pix
    private PaymentPresenter presenter;
    private double orderAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_pix);

        presenter = new PaymentPresenter(this);

        orderAmount = getIntent().getDoubleExtra("order_amount", 0.0);

        setupViews();
        setupListeners();
    }

    private void setupViews() {
        TextView tvAmountTop = findViewById(R.id.tvAmountTop);
        tvAmountTop.setText(String.format("R$ %.2f", orderAmount));

        ImageView imgQrCode = findViewById(R.id.imgQrCode);
        imgQrCode.setImageResource(R.drawable.circle_bg_dark);

        TextView tvPixKey = findViewById(R.id.tvPixKey);
        tvPixKey.setText(pixKey);
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

            presenter.deductBalance(orderAmount);
            Intent intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra("dish_id", getIntent().getStringExtra("dish_id"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        TextView tvChangePayment = findViewById(R.id.tvChangePayment);
        tvChangePayment.setOnClickListener(v -> finish());
    }

    private void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Chave Pix", pixKey);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Chave Pix copiada!", Toast.LENGTH_SHORT).show();
    }
}
