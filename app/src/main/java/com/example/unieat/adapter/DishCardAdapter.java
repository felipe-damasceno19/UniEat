package com.example.unieat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unieat.R;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.RatingDAO;
import com.example.unieat.model.Dish;
import com.example.unieat.model.Rating;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class DishCardAdapter extends RecyclerView.Adapter<DishCardAdapter.DishViewHolder> {

    public interface OnDishClickListener {
        void onOrderClick(Dish dish);
    }

    private final List<Dish> dishes;
    private final Context context;
    private final OnDishClickListener listener;
    private final RatingDAO ratingDAO = new RatingDAO();
    private final int layoutRes;

    public DishCardAdapter(Context context, List<Dish> dishes, OnDishClickListener listener) {
        this(context, dishes, listener, R.layout.item_dish_card);
    }

    public DishCardAdapter(Context context, List<Dish> dishes, OnDishClickListener listener, int layoutRes) {
        this.context   = context;
        this.dishes    = dishes;
        this.listener  = listener;
        this.layoutRes = layoutRes;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Dish dish = dishes.get(position);

        holder.tvName.setText(dish.getName());
        holder.tvPrice.setText(String.format("R$ %.2f", dish.getPrice()));
        holder.ratingBadge.setVisibility(View.GONE);

        String imageUrl = dish.getImageName();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.shape_logo_placeholder)
                    .error(R.drawable.shape_logo_placeholder)
                    .into(holder.imgDish);
        } else {
            holder.imgDish.setImageResource(R.drawable.shape_logo_placeholder);
        }

        holder.btnOrder.setOnClickListener(v -> listener.onOrderClick(dish));

        ratingDAO.findByDishId(dish.getId(), new FirebaseCallback<List<Rating>>() {
            @Override public void onSuccess(List<Rating> ratings) {
                int adapterPos = holder.getBindingAdapterPosition();
                if (adapterPos == RecyclerView.NO_POSITION) return;

                double avg = 0.0;
                if (ratings != null && !ratings.isEmpty()) {
                    double sum = 0;
                    for (Rating r : ratings) if (r.getRating() != null) sum += r.getRating();
                    avg = sum / ratings.size();
                }
                holder.tvRating.setText(String.format(Locale.getDefault(), "%.1f", avg));
                holder.ratingBadge.setVisibility(View.VISIBLE);
            }
            @Override public void onFailure(String error) {}
        });
    }

    @Override
    public int getItemCount() { return dishes.size(); }

    static class DishViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDish;
        LinearLayout ratingBadge;
        TextView tvName, tvPrice, tvRating;
        MaterialButton btnOrder;

        DishViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDish      = itemView.findViewById(R.id.imgDish);
            ratingBadge  = itemView.findViewById(R.id.ratingBadge);
            tvName       = itemView.findViewById(R.id.tvDishName);
            tvPrice      = itemView.findViewById(R.id.tvDishPrice);
            tvRating     = itemView.findViewById(R.id.tvRating);
            btnOrder     = itemView.findViewById(R.id.btnOrder);
        }
    }
}
