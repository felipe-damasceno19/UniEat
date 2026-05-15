package com.example.unieat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.model.Dish;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DishCardAdapter extends RecyclerView.Adapter<DishCardAdapter.DishViewHolder> {

    public interface OnDishClickListener {
        void onOrderClick(Dish dish);
    }

    private static final Map<String, Integer> DISH_IMAGES = new HashMap<>();

    static {
        // adicione aqui conforme for criando as imagens em res/drawable
        // exemplo: DISH_IMAGES.put("dish_frango_grelhado", R.drawable.dish_frango_grelhado);
    }

    private final List<Dish> dishes;
    private final Context context;
    private final OnDishClickListener listener;

    public DishCardAdapter(Context context, List<Dish> dishes, OnDishClickListener listener) {
        this.context = context;
        this.dishes = dishes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dish_card, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Dish dish = dishes.get(position);

        holder.tvName.setText(dish.getName());
        holder.tvPrice.setText(String.format("R$ %.2f", dish.getPrice()));

        if (dish.getImageName() != null && DISH_IMAGES.containsKey(dish.getImageName())) {
            holder.imgDish.setImageResource(DISH_IMAGES.get(dish.getImageName()));
        } else {
            holder.imgDish.setImageResource(R.drawable.shape_logo_placeholder);
        }

        holder.btnOrder.setOnClickListener(v -> listener.onOrderClick(dish));
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    static class DishViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDish;
        TextView tvName, tvPrice;
        MaterialButton btnOrder;

        DishViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDish = itemView.findViewById(R.id.imgDish);
            tvName = itemView.findViewById(R.id.tvDishName);
            tvPrice = itemView.findViewById(R.id.tvDishPrice);
            btnOrder = itemView.findViewById(R.id.btnOrder);
        }
    }
}