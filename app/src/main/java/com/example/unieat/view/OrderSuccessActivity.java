package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.unieat.R;

public class OrderSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        String dishId = getIntent().getStringExtra("dish_id");

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
