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

public class PaymentPixActivity extends AppCompatActivity {

    private String pixKey = "12.345.678/0001-99";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_pix);

        setupListeners();
    }

    private void setupListeners() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView tvPixKey = findViewById(R.id.tvPixKey);
        tvPixKey.setText(pixKey);

        ImageView btnCopyIcon = findViewById(R.id.btnCopyIcon);
        btnCopyIcon.setOnClickListener(v -> copyToClipboard());

        Button btnCopyKey = findViewById(R.id.btnCopyKey);
        btnCopyKey.setOnClickListener(v -> copyToClipboard());

        Button btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnConfirmOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderSuccessActivity.class);
            startActivity(intent);
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
