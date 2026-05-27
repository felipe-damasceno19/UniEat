package com.example.unieat.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.unieat.R;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;
import com.example.unieat.model.Rating;
import com.example.unieat.model.User;
import com.example.unieat.presenter.OrderPresenter;
import com.example.unieat.presenter.student.RatingPresenter;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class DishDetailActivity extends BaseActivity implements RatingPresenter.RatingView {

    private int quantity = 1;
    private Dish dish;
    private TextView tvQuantity;
    private MaterialButton btnAddToCart;
    private RatingPresenter ratingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        loadDishFromIntent();
        setupViews();
        ratingPresenter = new RatingPresenter(this);
        ratingPresenter.getRatingsByDishId(dish.getId());
    }

    private void loadDishFromIntent() {
        String typeStr = getIntent().getStringExtra("dish_type");
        FoodType foodType;
        try {
            foodType = FoodType.valueOf(typeStr != null ? typeStr : "");
        } catch (Exception e) {
            foodType = FoodType.REFEICAO;
        }

        dish = new Dish(
                getIntent().getStringExtra("dish_id"),
                getIntent().getStringExtra("dish_name"),
                getIntent().getStringExtra("dish_description"),
                getIntent().getDoubleExtra("dish_price", 0.0),
                foodType
        );
        dish.setImageName(getIntent().getStringExtra("dish_image"));
    }

    private void setupViews() {
        ImageView imgDish       = findViewById(R.id.imgDish);
        TextView tvName         = findViewById(R.id.tvDishName);
        TextView tvPrice        = findViewById(R.id.tvPrice);
        TextView tvDescription  = findViewById(R.id.tvDescription);
        TextView tvCategory     = findViewById(R.id.tvCategory);
        tvQuantity              = findViewById(R.id.tvQuantity);
        MaterialButton btnDec   = findViewById(R.id.btnDecrement);
        MaterialButton btnInc   = findViewById(R.id.btnIncrement);
        btnAddToCart            = findViewById(R.id.btnAddToCart);
        View btnBack            = findViewById(R.id.btnBack);

        tvName.setText(dish.getName());
        tvPrice.setText(String.format(Locale.getDefault(), "R$ %.2f", dish.getPrice()));
        tvDescription.setText(
                (dish.getDescription() != null && !dish.getDescription().isEmpty())
                        ? dish.getDescription()
                        : "Sem descrição disponível."
        );
        tvCategory.setText(formatFoodType(dish.getType()));

        String imageUrl = dish.getImageName();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.shape_logo_placeholder)
                    .error(R.drawable.shape_logo_placeholder)
                    .into(imgDish);
        }

        btnBack.setOnClickListener(v -> finish());

        btnDec.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateCartButton();
            }
        });

        btnInc.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updateCartButton();
        });

        updateCartButton();

        btnAddToCart.setOnClickListener(v -> {
            OrderPresenter orderPresenter = new OrderPresenter(this);
            for (int i = 0; i < quantity; i++) {
                orderPresenter.addItem(dish);
            }
            Toast.makeText(this,
                    quantity + "x " + dish.getName() + " adicionado ao carrinho",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateCartButton() {
        double total = dish.getPrice() * quantity;
        btnAddToCart.setText(String.format(Locale.getDefault(), "Adicionar · R$ %.2f", total));
    }

    private String formatFoodType(FoodType type) {
        if (type == null) return "";
        switch (type) {
            case REFEICAO:          return "Refeição";
            case SALGADO_ASSADO:    return "Salgado Assado";
            case SALGADO_FRITO:     return "Salgado Frito";
            case SANDUICHE_NATURAL: return "Sanduíche Natural";
            case CUSCUZ:            return "Cuscuz";
            case TAPIOCA:           return "Tapioca";
            case DOCE_CAKE:         return "Doce / Bolo";
            case SNACK:             return "Snack";
            case BEBIDA_QUENTE:     return "Bebida Quente";
            case BEBIDA_GELADA:     return "Bebida Gelada";
            case SOBREMESA_GELADA:  return "Sobremesa Gelada";
            default:                return type.name();
        }
    }

    @Override
    public void onRatingsLoaded(List<Rating> ratings, double average) {
        TextView tvRatingCount      = findViewById(R.id.tvRatingCount);
        LinearLayout layoutAvg      = findViewById(R.id.layoutRatingAvg);
        TextView tvNoRatings        = findViewById(R.id.tvNoRatings);
        TextView tvRatingAvg        = findViewById(R.id.tvRatingAvg);
        RatingBar ratingBarDisplay  = findViewById(R.id.ratingBarDisplay);
        TextView tvTotalReviews     = findViewById(R.id.tvTotalReviews);
        LinearLayout layoutReviews  = findViewById(R.id.layoutReviews);

        int count = (ratings != null) ? ratings.size() : 0;
        tvRatingCount.setText(count + (count == 1 ? " avaliação" : " avaliações"));

        if (count > 0) {
            tvNoRatings.setVisibility(View.GONE);
            layoutAvg.setVisibility(View.VISIBLE);
            tvRatingAvg.setText(String.format(Locale.getDefault(), "%.1f", average));
            ratingBarDisplay.setRating((float) average);
            tvTotalReviews.setText("de " + count + (count == 1 ? " avaliação" : " avaliações"));

            LayoutInflater inflater = LayoutInflater.from(this);
            UserDAO userDAO = new UserDAO();
            for (Rating r : ratings) {
                View reviewView = inflater.inflate(R.layout.item_review, layoutReviews, false);
                TextView tvAvatar  = reviewView.findViewById(R.id.tvReviewAvatar);
                TextView tvName    = reviewView.findViewById(R.id.tvReviewerName);
                RatingBar bar      = reviewView.findViewById(R.id.reviewRatingBar);
                TextView tvComment = reviewView.findViewById(R.id.tvReviewComment);
                TextView tvTags    = reviewView.findViewById(R.id.tvReviewTags);

                if (r.getRating() != null) bar.setRating(r.getRating());

                String rawComment = r.getComment() != null ? r.getComment() : "";
                String[] parts = rawComment.split("\n", 2);
                String commentText = parts[0].trim();
                String tagsText = parts.length > 1 ? parts[1].trim() : "";

                if (!commentText.isEmpty()) {
                    tvComment.setVisibility(View.VISIBLE);
                    tvComment.setText(commentText);
                }
                if (!tagsText.isEmpty()) {
                    tvTags.setVisibility(View.VISIBLE);
                    tvTags.setText(tagsText);
                }

                if (r.getUserId() != null) {
                    userDAO.findById(r.getUserId(), new FirebaseCallback<User>() {
                        @Override public void onSuccess(User user) {
                            String displayName = (user != null && user.getName() != null && !user.getName().isEmpty())
                                    ? user.getName()
                                    : (user != null && user.getUsername() != null ? user.getUsername() : "Usuário");
                            tvName.setText(displayName);
                            tvAvatar.setText(String.valueOf(displayName.charAt(0)).toUpperCase(Locale.getDefault()));
                        }
                        @Override public void onFailure(String error) {
                            tvName.setText("Usuário");
                            tvAvatar.setText("U");
                        }
                    });
                } else {
                    tvName.setText("Usuário");
                    tvAvatar.setText("U");
                }

                layoutReviews.addView(reviewView);
            }
        } else {
            tvNoRatings.setVisibility(View.VISIBLE);
            layoutAvg.setVisibility(View.GONE);
        }
    }

    @Override public void onSubmitSuccess() {}
    @Override public void onSubmitError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    @Override public void onExistingRatingLoaded(Rating rating) {}
    @Override public void onDeleteSuccess() {}
}
