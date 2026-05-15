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
import com.example.unieat.model.OrderItem;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    public interface OnQuantityChangeListener {
        void onIncrease(OrderItem item);
        void onDecrease(OrderItem item);
    }

    private final List<OrderItem> items;
    private final Context context;
    private final OnQuantityChangeListener listener;

    public OrderItemAdapter(Context context, List<OrderItem> items, OnQuantityChangeListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_card, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = items.get(position);

        holder.tvName.setText(item.getDish().getName());
        holder.tvPrice.setText(String.format("R$ %.2f", item.getDish().getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvSubtotal.setText(String.format("R$ %.2f",
                item.getDish().getPrice() * item.getQuantity()));

        holder.btnIncrease.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                listener.onIncrease(item);
                notifyItemChanged(currentPos);
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                listener.onDecrease(item);
                if (item.getQuantity() <= 0) {
                    items.remove(currentPos);
                    notifyItemRemoved(currentPos);
                    notifyItemRangeChanged(currentPos, items.size());
                } else {
                    notifyItemChanged(currentPos);
                }
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDish;
        TextView tvName, tvPrice, tvQuantity, tvSubtotal;
        TextView btnIncrease;
        ImageView btnDecrease;

        OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDish = itemView.findViewById(R.id.imgDish);
            tvName = itemView.findViewById(R.id.tvDishName);
            tvPrice = itemView.findViewById(R.id.tvDishPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }
}