package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unieat.R;
import com.example.unieat.model.Rating;
import com.example.unieat.presenter.student.RatingPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class RatingActivity extends BaseActivity
        implements RatingPresenter.RatingView {

    private RatingPresenter presenter;
    private RatingBar ratingBar;
    private TextInputEditText etComment;
    private MaterialButton btnSubmit;

    private String dishId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        dishId    = getIntent().getStringExtra("dish_id");
        presenter = new RatingPresenter(this);

        setupNavigation();
        setupViews();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, -1);
    }

    private void setupViews() {
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> submitRating());

        TextView tvSkip = findViewById(R.id.tvSkip);
        tvSkip.setOnClickListener(v -> navigateToHome());
    }

    private void submitRating() {
        if (dishId == null || dishId.isEmpty()) {
            Toast.makeText(this, "Prato não identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        int rating = (int) ratingBar.getRating();
        String comment = etComment.getText() != null
                ? etComment.getText().toString().trim()
                : "";

        btnSubmit.setEnabled(false);
        presenter.submitRating(dishId, rating, comment);
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, StudentHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSubmitSuccess() {
        Toast.makeText(this, "Avaliação enviada!", Toast.LENGTH_SHORT).show();
        navigateToHome();
    }

    @Override
    public void onSubmitError(String message) {
        btnSubmit.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRatingsLoaded(List<Rating> ratings, double average) {
    }
}