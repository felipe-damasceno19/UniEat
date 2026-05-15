package com.example.unieat.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class KitchenOrderAdapter extends RecyclerView.Adapter<KitchenOrderAdapter.ViewHolder> {

    private List<Order> orders;
    private final Context context;
    private final OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onActionClick(Order order);
    }

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kitchen_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderNumber.setText("Order #" + order.getId().substring(0, 4));
        holder.tvCustomerName.setText("Active User"); // Mock
        holder.tvLocation.setText("UNINTA • " + (order.getAnnotation() != null ? order.getAnnotation() : "Campus"));
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        holder.tvTime.setText(sdf.format(order.getTime()));

        setupStatusUI(holder, order.getStatus());

        holder.chipGroupItems.removeAllViews();
        for (OrderItem item : order.getItems()) {
            Chip chip = new Chip(context);
            chip.setText(item.getQuantity() + "x " + item.getDish().getName());
            chip.setTextSize(10);
            chip.setChipMinHeight(0f);
            chip.setEnsureMinTouchTargetSize(false);
            holder.chipGroupItems.addView(chip);
        }

        holder.btnOrderAction.setOnClickListener(v -> listener.onActionClick(order));
    }

    private void setupStatusUI(ViewHolder holder, OrderStatus status) {
        int color;
        int bgColor;
        String statusText;

        switch (status) {
            case PENDENTE:
                color = ContextCompat.getColor(context, android.R.color.holo_orange_dark);
                bgColor = ColorStateList.valueOf(color).withAlpha(30).getDefaultColor();
                statusText = "NEW";
                holder.btnOrderAction.setText("Accept Order");
                holder.btnOrderAction.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.holo_red_dark));
                holder.btnOrderAction.setEnabled(true);
                holder.btnOrderAction.setAlpha(1.0f);
                break;
            case PREPARANDO:
                color = ContextCompat.getColor(context, android.R.color.holo_blue_dark);
                bgColor = ColorStateList.valueOf(color).withAlpha(30).getDefaultColor();
                statusText = "IN PREP";
                holder.btnOrderAction.setText("Mark as Ready");
                holder.btnOrderAction.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.holo_green_dark));
                holder.btnOrderAction.setEnabled(true);
                holder.btnOrderAction.setAlpha(1.0f);
                break;
            case PRONTO:
                color = ContextCompat.getColor(context, android.R.color.holo_green_dark);
                bgColor = ColorStateList.valueOf(color).withAlpha(30).getDefaultColor();
                statusText = "READY";
                holder.btnOrderAction.setText("Waiting for Pickup");
                holder.btnOrderAction.setBackgroundTintList(null);
                holder.btnOrderAction.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                holder.btnOrderAction.setEnabled(false);
                holder.btnOrderAction.setAlpha(0.7f);
                break;
            default:
                color = ContextCompat.getColor(context, android.R.color.darker_gray);
                bgColor = ColorStateList.valueOf(color).withAlpha(30).getDefaultColor();
                statusText = status.name();
                holder.btnOrderAction.setVisibility(View.GONE);
                break;
        }
        
        holder.tvStatusBadge.setText(statusText);
        holder.tvStatusBadge.setTextColor(color);
        holder.tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        holder.viewStatusIndicator.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View viewStatusIndicator;
        TextView tvOrderNumber, tvCustomerName, tvLocation, tvTime, tvStatusBadge;
        ChipGroup chipGroupItems;
        MaterialButton btnOrderAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvTime = itemView.findViewById(R.id.tvOrderTime);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            chipGroupItems = itemView.findViewById(R.id.chipGroupItems);
            btnOrderAction = itemView.findViewById(R.id.btnOrderAction);
        }
    }
}
