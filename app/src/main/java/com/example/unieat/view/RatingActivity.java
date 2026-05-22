package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.unieat.R;
import com.example.unieat.presenter.student.RatingPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RatingActivity extends BaseActivity {

    private RatingPresenter presenter;
    private RatingBar ratingBar;
    private TextInputEditText etComment;

    private String dishId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        presenter = new RatingPresenter(this);

        dishId = getIntent().getStringExtra("dish_id");

        setupNavigation();
        setupViews();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_orders);
    }

    private void setupViews() {
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);

        MaterialButton btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> submitRating());

        TextView tvSkip = findViewById(R.id.tvSkip);
        tvSkip.setOnClickListener(v -> navigateToHome());
    }

    private void submitRating() {
        int rating = (int) ratingBar.getRating();
        String comment = etComment.getText() != null
                ? etComment.getText().toString().trim()
                : "";

        if (dishId == null || dishId.isEmpty()) {
            Toast.makeText(this, "Prato não identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = presenter.submitRating(dishId, rating, comment);

        if (success) {
            Toast.makeText(this, "Avaliação enviada!", Toast.LENGTH_SHORT).show();
            navigateToHome();
        } else {
            Toast.makeText(this, "Selecione pelo menos 1 estrela", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, StudentHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}