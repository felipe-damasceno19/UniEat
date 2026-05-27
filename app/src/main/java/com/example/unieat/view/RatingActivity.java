package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.unieat.R;
import com.example.unieat.dao.DishDAO;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.model.Dish;
import com.example.unieat.model.Rating;
import com.example.unieat.presenter.student.RatingPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class RatingActivity extends BaseActivity implements RatingPresenter.RatingView {

    private RatingPresenter presenter;
    private RatingBar ratingBar;
    private TextInputEditText etComment;
    private MaterialButton btnSubmit;
    private TextView tvRatingLabel;
    private ChipGroup chipGroup;
    private String dishId;

    private static final String[] STAR_LABELS = {
            "", "Ruim", "Regular", "Bom", "Muito bom", "Excelente!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        dishId    = getIntent().getStringExtra("dish_id");
        presenter = new RatingPresenter(this);

        setupNavigation();
        setupViews();
        loadDishInfo();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, -1);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupViews() {
        ratingBar     = findViewById(R.id.ratingBar);
        etComment     = findViewById(R.id.etComment);
        btnSubmit     = findViewById(R.id.btnSubmit);
        tvRatingLabel = findViewById(R.id.tvRatingLabel);
        chipGroup     = findViewById(R.id.chipGroup);

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            int stars = (int) rating;
            tvRatingLabel.setText(stars > 0 ? STAR_LABELS[stars] : "");
        });

        btnSubmit.setOnClickListener(v -> submitRating());

        findViewById(R.id.tvSkip).setOnClickListener(v -> navigateToHome());
    }

    private void loadDishInfo() {
        if (dishId == null || dishId.isEmpty()) return;

        new DishDAO().findById(dishId, new FirebaseCallback<Dish>() {
            @Override public void onSuccess(Dish dish) {
                if (dish == null) return;

                TextView tvName = findViewById(R.id.tvDishNameRating);
                tvName.setText(dish.getName());

                String imageUrl = dish.getImageName();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(RatingActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.shape_logo_placeholder)
                            .error(R.drawable.shape_logo_placeholder)
                            .into(findViewById(R.id.imgDishRating));
                }
            }
            @Override public void onFailure(String error) {}
        });
    }

    private void submitRating() {
        if (dishId == null || dishId.isEmpty()) {
            Toast.makeText(this, "Prato não identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        int rating = (int) ratingBar.getRating();
        if (rating == 0) {
            Toast.makeText(this, "Selecione uma nota antes de enviar", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = etComment.getText() != null
                ? etComment.getText().toString().trim()
                : "";

        List<String> selectedTags = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) selectedTags.add(chip.getText().toString());
        }

        if (!selectedTags.isEmpty()) {
            String tags = String.join(" · ", selectedTags);
            comment = comment.isEmpty() ? tags : comment + "\n" + tags;
        }

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
    public void onRatingsLoaded(List<Rating> ratings, double average) {}
}
