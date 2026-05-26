package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unieat.R;
import com.example.unieat.adapter.DishCardAdapter;
import com.example.unieat.dao.BannerDAO;
import com.example.unieat.dao.DishDAO;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.data.SessionManager;
import com.example.unieat.model.Banner;
import com.example.unieat.model.Dish;
import com.example.unieat.presenter.OrderPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BannerProductsActivity extends AppCompatActivity {

    private OrderPresenter orderPresenter;
    private TextView tvCartBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_products);

        orderPresenter = new OrderPresenter(this);

        String bannerId = getIntent().getStringExtra("banner_id");
        if (bannerId == null) { finish(); return; }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvCartBadge = findViewById(R.id.tvCartBadge);

        TextView tvBalance = findViewById(R.id.tvBalance);
        tvBalance.setText(String.format("R$ %.2f", new SessionManager(this).getBalance()));

        FloatingActionButton fabCart = findViewById(R.id.fabCart);
        fabCart.setOnClickListener(v -> {
            if (!orderPresenter.isCartEmpty()) {
                startActivity(new Intent(this, OrderActivity.class));
            } else {
                String activeOrderId = new SessionManager(this).getActiveOrderId();
                if (activeOrderId != null) {
                    Intent intent = new Intent(this, OrderSuccessActivity.class);
                    intent.putExtra("order_id", activeOrderId);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, OrderActivity.class));
                }
            }
        });

        updateCartBadge();
        loadBanner(bannerId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
        TextView tvBalance = findViewById(R.id.tvBalance);
        tvBalance.setText(String.format("R$ %.2f", new SessionManager(this).getBalance()));
    }

    private void loadBanner(String bannerId) {
        new BannerDAO().findAll(new FirebaseCallback<List<Banner>>() {
            @Override public void onSuccess(List<Banner> banners) {
                Banner found = null;
                for (Banner b : banners) {
                    if (bannerId.equals(b.getId())) { found = b; break; }
                }
                if (found == null) { finish(); return; }
                bindBannerHeader(found);
                loadProducts(found.getDishIds());
            }
            @Override public void onFailure(String error) { finish(); }
        });
    }

    private void bindBannerHeader(Banner banner) {
        TextView tvTitle    = findViewById(R.id.tvBannerTitle);
        TextView tvTag      = findViewById(R.id.tvHeaderTag);
        TextView tvSubtitle = findViewById(R.id.tvHeaderSubtitle);
        ImageView ivImage   = findViewById(R.id.ivHeaderImage);
        View overlay        = findViewById(R.id.viewHeaderOverlay);

        tvTitle.setText(banner.getTitle());
        tvTag.setText(banner.getTag());
        tvSubtitle.setText(banner.getSubtitle());

        if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
            ivImage.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);
            Glide.with(this).load(banner.getImageUrl()).centerCrop().into(ivImage);
        }
    }

    private void loadProducts(List<String> dishIds) {
        if (dishIds == null || dishIds.isEmpty()) {
            Toast.makeText(this, "Nenhum produto vinculado", Toast.LENGTH_SHORT).show();
            return;
        }

        DishDAO dishDAO = new DishDAO();
        List<Dish> dishes = new ArrayList<>();
        final int[] remaining = {dishIds.size()};

        RecyclerView rv = findViewById(R.id.rvBannerProducts);
        rv.setLayoutManager(new LinearLayoutManager(this));

        for (String dishId : dishIds) {
            dishDAO.findById(dishId, new FirebaseCallback<Dish>() {
                @Override public void onSuccess(Dish dish) {
                    if (dish != null) dishes.add(dish);
                    if (--remaining[0] == 0) {
                        rv.setAdapter(new DishCardAdapter(BannerProductsActivity.this, dishes, d -> {
                            orderPresenter.addItem(d);
                            updateCartBadge();
                            Toast.makeText(BannerProductsActivity.this,
                                    d.getName() + " adicionado ao carrinho", Toast.LENGTH_SHORT).show();
                        }));
                    }
                }
                @Override public void onFailure(String error) {
                    if (--remaining[0] == 0) {
                        rv.setAdapter(new DishCardAdapter(BannerProductsActivity.this, dishes, d -> {
                            orderPresenter.addItem(d);
                            updateCartBadge();
                        }));
                    }
                }
            });
        }
    }

    private void updateCartBadge() {
        int count = orderPresenter.getCartItemCount();
        if (count > 0) {
            tvCartBadge.setText(String.valueOf(count));
            tvCartBadge.setVisibility(View.VISIBLE);
        } else if (new SessionManager(this).getActiveOrderId() != null) {
            tvCartBadge.setText("!");
            tvCartBadge.setVisibility(View.VISIBLE);
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }
}
