package com.example.unieat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;
import com.example.unieat.util.DateUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class KitchenOrderAdapter extends RecyclerView.Adapter<KitchenOrderAdapter.KitchenOrderViewHolder> {

    public interface OnOrderActionListener {
        void onAdvanceStatus(Order order);
    }

    private List<Order> orders;
    private final Context context;
    private final OnOrderActionListener listener;

    public KitchenOrderAdapter(Context context, List<Order> orders, OnOrderActionListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    public void updateData(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KitchenOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_kitchen_order, parent, false);
        return new KitchenOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KitchenOrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderNumber.setText("Order #" + order.getId().substring(0, 4).toUpperCase());
        holder.tvOrderTime.setText(DateUtils.formatDate(order.getTime()));

        applyStatusStyle(holder, order.getStatus());

        holder.chipGroupItems.removeAllViews();
        for (OrderItem item : order.getItems()) {
            Chip chip = new Chip(context);
            chip.setText(item.getQuantity() + "x " + item.getDish().getName());
            chip.setClickable(false);
            holder.chipGroupItems.addView(chip);
        }

        holder.btnOrderAction.setText(getActionLabel(order.getStatus()));
        holder.btnOrderAction.setOnClickListener(v -> listener.onAdvanceStatus(order));

        if (order.getStatus() == OrderStatus.ENTREGUE) {
            holder.btnOrderAction.setVisibility(View.GONE);
        } else {
            holder.btnOrderAction.setVisibility(View.VISIBLE);
        }
    }

    private void applyStatusStyle(KitchenOrderViewHolder holder, OrderStatus status) {
        switch (status) {
            case PENDENTE:
                holder.viewStatusIndicator.setBackgroundColor(0xFFFFB84C);
                holder.tvStatusBadge.setText("NOVO");
                holder.tvStatusBadge.setTextColor(0xFFFFB84C);
                holder.tvStatusBadge.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFFFF5E1));
                break;
            case PREPARANDO:
                holder.viewStatusIndicator.setBackgroundColor(0xFF2196F3);
                holder.tvStatusBadge.setText("EM PREPARO");
                holder.tvStatusBadge.setTextColor(0xFF1A4A7A);
                holder.tvStatusBadge.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFD6E4F7));
                break;
            case PRONTO:
                holder.viewStatusIndicator.setBackgroundColor(0xFF4CAF50);
                holder.tvStatusBadge.setText("PRONTO");
                holder.tvStatusBadge.setTextColor(0xFF1A6A45);
                holder.tvStatusBadge.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFD6F5E8));
                break;
            default:
                holder.viewStatusIndicator.setBackgroundColor(0xFF888888);
                holder.tvStatusBadge.setText("CONCLUÍDO");
                holder.tvStatusBadge.setTextColor(0xFF888888);
                holder.tvStatusBadge.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFEEEEEE));
                break;
        }
    }

    private String getActionLabel(OrderStatus status) {
        switch (status) {
            case PENDENTE:   return "Accept Order";
            case PREPARANDO: return "Mark as Ready";
            case PRONTO:     return "Mark as Delivered";
            default:        return "";
        }
    }

    @Override
    public int getItemCount() { return orders.size(); }

    static class KitchenOrderViewHolder extends RecyclerView.ViewHolder {
        View viewStatusIndicator;
        TextView tvOrderNumber, tvOrderTime, tvStatusBadge;
        ChipGroup chipGroupItems;
        MaterialButton btnOrderAction;

        KitchenOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            chipGroupItems = itemView.findViewById(R.id.chipGroupItems);
            btnOrderAction = itemView.findViewById(R.id.btnOrderAction);
        }
    }
}