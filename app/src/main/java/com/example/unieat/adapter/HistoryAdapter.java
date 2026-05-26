package com.example.unieat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unieat.R;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;
import com.example.unieat.util.DateUtils;
import com.example.unieat.util.OrderUtils;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnHistoryClickListener {
        void onReorder(Order order);
        void onDetails(Order order);
    }

    private final List<Order> orders;
    private final Context context;
    private final OnHistoryClickListener listener;

    public HistoryAdapter(Context context, List<Order> orders, OnHistoryClickListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_card, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText(OrderUtils.formatOrderNumber(order.getId()));
        holder.tvOrderDate.setText(DateUtils.formatDate(order.getTime()));

        String statusText = OrderUtils.formatStatus(order.getStatus());
        holder.tvOrderStatus.setText(statusText);
        applyStatusStyle(holder.tvOrderStatus, order.getStatus());

        if (!order.getItems().isEmpty() && order.getItems().get(0).getDish() != null) {
            String imageUrl = order.getItems().get(0).getDish().getImageName();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context).load(imageUrl)
                        .placeholder(R.drawable.shape_logo_placeholder)
                        .error(R.drawable.shape_logo_placeholder)
                        .into(holder.imgDish);
            } else {
                holder.imgDish.setImageResource(R.drawable.shape_logo_placeholder);
            }
        }

        holder.tvOrderItems.setText(buildItemsSummary(order.getItems()));

        double total = 0;
        for (OrderItem item : order.getItems()) {
            total += item.getDish().getPrice() * item.getQuantity();
        }
        holder.tvOrderTotal.setText(String.format("R$ %.2f", total));

        holder.btnReorder.setOnClickListener(v -> listener.onReorder(order));
        holder.btnDetails.setOnClickListener(v -> listener.onDetails(order));
    }

    private String buildItemsSummary(List<OrderItem> items) {
        if (items == null || items.isEmpty()) return "Sem itens";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, items.size()); i++) {
            OrderItem item = items.get(i);
            sb.append(item.getQuantity()).append("x ").append(item.getDish().getName());
            if (i < Math.min(2, items.size()) - 1) sb.append(" + ");
        }
        if (items.size() > 2) sb.append(" + mais...");
        return sb.toString();
    }

    private void applyStatusStyle(TextView tv, OrderStatus status) {
        switch (status) {
            case ENTREGUE:
                tv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
                tv.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case PREPARANDO:
                tv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFF3E0")));
                tv.setTextColor(Color.parseColor("#FF9800"));
                break;
            case PENDENTE:
                tv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E3F2FD")));
                tv.setTextColor(Color.parseColor("#2196F3"));
                break;
            case REJEITADO:
                tv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFEBEE")));
                tv.setTextColor(Color.parseColor("#CC2222"));
                break;
            default:
                tv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EEEEEE")));
                tv.setTextColor(Color.parseColor("#888888"));
                break;
        }
    }

    @Override
    public int getItemCount() { return orders.size(); }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDish;
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderItems, tvOrderTotal;
        Button btnReorder, btnDetails;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDish = itemView.findViewById(R.id.imgDish);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            btnReorder = itemView.findViewById(R.id.btnReorder);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}