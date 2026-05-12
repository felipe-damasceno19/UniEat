package com.example.unieat.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.unieat.R;
import com.google.android.material.card.MaterialCardView;

public class PaymentActivity extends AppCompatActivity {

    private MaterialCardView cardPix, cardCash;
    private ImageView imgPix, imgCash;
    private Button btnConfirmOrder;
    private boolean isPixSelected = true;

    private int colorSelected, colorDefault;
    private int iconBgSelected, iconBgDefault;
    private int iconTintSelected, iconTintDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initViews();
        setupColors();
        setupListeners();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        cardPix = findViewById(R.id.cardPix);
        cardCash = findViewById(R.id.cardCash);
        imgPix = findViewById(R.id.imgPix);
        imgCash = findViewById(R.id.imgCash);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
    }

    private void setupColors() {
        colorSelected = Color.parseColor("#7B1C1C");
        colorDefault = Color.parseColor("#EEEEEE");
        iconBgSelected = Color.parseColor("#FFB84C"); // Amarelo
        iconBgDefault = Color.parseColor("#F0E8E8"); // Cinza
        iconTintSelected = Color.parseColor("#7B1C1C"); // Vermelho escuro
        iconTintDefault = Color.parseColor("#666666"); // Cinza escuro
    }

    private void setupListeners() {
        cardPix.setOnClickListener(v -> selectPix());
        cardCash.setOnClickListener(v -> selectCash());
        btnConfirmOrder.setOnClickListener(v -> {
            if (isPixSelected) {
                Intent intent = new Intent(this, PaymentPixActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, OrderSuccessActivity.class);
                startActivity(intent);
            }
        });
    }

    private void selectPix() {
        isPixSelected = true;
        // Selecionar Pix
        cardPix.setStrokeColor(colorSelected);
        cardPix.setStrokeWidth(6);
        imgPix.setBackgroundTintList(ColorStateList.valueOf(iconBgSelected));
        imgPix.setImageTintList(ColorStateList.valueOf(iconTintSelected));

        // Desmarcar Dinheiro
        cardCash.setStrokeColor(colorDefault);
        cardCash.setStrokeWidth(2);
        imgCash.setBackgroundTintList(ColorStateList.valueOf(iconBgDefault));
        imgCash.setImageTintList(ColorStateList.valueOf(iconTintDefault));
    }

    private void selectCash() {
        isPixSelected = false;
        // Selecionar Dinheiro
        cardCash.setStrokeColor(colorSelected);
        cardCash.setStrokeWidth(6);
        imgCash.setBackgroundTintList(ColorStateList.valueOf(iconBgSelected));
        imgCash.setImageTintList(ColorStateList.valueOf(iconTintSelected));

        // Desmarcar Pix
        cardPix.setStrokeColor(colorDefault);
        cardPix.setStrokeWidth(2);
        imgPix.setBackgroundTintList(ColorStateList.valueOf(iconBgDefault));
        imgPix.setImageTintList(ColorStateList.valueOf(iconTintDefault));
    }
}
