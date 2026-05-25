package com.example.unieat.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unieat.R;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;
import com.example.unieat.model.User;
import com.example.unieat.util.DateUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class KitchenOrderAdapter extends RecyclerView.Adapter<KitchenOrderAdapter.KitchenOrderViewHolder> {

    public interface OnOrderActionListener {
        void onChangeStatus(Order order, OrderStatus newStatus);
    }

    private static final String[] STATUS_LABELS   = {"Pendente", "Em Preparo", "Pronto", "Entregue"};
    private static final OrderStatus[] STATUS_VALUES = {
            OrderStatus.PENDENTE, OrderStatus.PREPARANDO, OrderStatus.PRONTO, OrderStatus.ENTREGUE
    };

    private List<Order> orders;
    private final Context context;
    private final OnOrderActionListener listener;
    private final UserDAO userDAO = new UserDAO();

    public KitchenOrderAdapter(Context context, List<Order> orders, OnOrderActionListener listener) {
        this.context  = context;
        this.orders   = orders;
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

        holder.tvOrderNumber.setText("Pedido #" + order.getId().substring(0, 4).toUpperCase());
        holder.tvOrderTime.setText(DateUtils.formatDate(order.getTime()));

        holder.tvCustomerName.setText("Carregando...");
        if (order.getUserId() != null && !order.getUserId().isEmpty()) {
            userDAO.findById(order.getUserId(), new FirebaseCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    int current = holder.getBindingAdapterPosition();
                    if (current != RecyclerView.NO_ID && current < orders.size()
                            && orders.get(current).getId().equals(order.getId())) {
                        holder.tvCustomerName.setText(
                                user != null && user.getName() != null ? user.getName() : "—"
                        );
                    }
                }
                @Override
                public void onFailure(String error) {
                    holder.tvCustomerName.setText("—");
                }
            });
        } else {
            holder.tvCustomerName.setText("—");
        }

        applyStatusStyle(holder, order.getStatus());

        holder.chipGroupItems.removeAllViews();
        for (OrderItem item : order.getItems()) {
            Chip chip = new Chip(context);
            chip.setText(item.getQuantity() + "x " + item.getDish().getName());
            chip.setClickable(false);
            holder.chipGroupItems.addView(chip);
        }

        holder.btnOrderAction.setText("Alterar Status");
        holder.btnOrderAction.setOnClickListener(v -> showStatusDialog(order));
    }

    private void showStatusDialog(Order order) {
        int current = -1;
        for (int i = 0; i < STATUS_VALUES.length; i++) {
            if (STATUS_VALUES[i] == order.getStatus()) { current = i; break; }
        }

        new AlertDialog.Builder(context)
                .setTitle("Alterar Status do Pedido")
                .setSingleChoiceItems(STATUS_LABELS, current, (dialog, which) -> {
                    dialog.dismiss();
                    listener.onChangeStatus(order, STATUS_VALUES[which]);
                })
                .setNegativeButton("Cancelar", null)
                .show();
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
                holder.tvStatusBadge.setText("ENTREGUE");
                holder.tvStatusBadge.setTextColor(0xFF888888);
                holder.tvStatusBadge.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFEEEEEE));
                break;
        }
    }

    @Override
    public int getItemCount() { return orders.size(); }

    static class KitchenOrderViewHolder extends RecyclerView.ViewHolder {
        View viewStatusIndicator;
        TextView tvOrderNumber, tvCustomerName, tvOrderTime, tvStatusBadge;
        ChipGroup chipGroupItems;
        MaterialButton btnOrderAction;

        KitchenOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
            tvOrderNumber       = itemView.findViewById(R.id.tvOrderNumber);
            tvCustomerName      = itemView.findViewById(R.id.tvCustomerName);
            tvOrderTime         = itemView.findViewById(R.id.tvOrderTime);
            tvStatusBadge       = itemView.findViewById(R.id.tvStatusBadge);
            chipGroupItems      = itemView.findViewById(R.id.chipGroupItems);
            btnOrderAction      = itemView.findViewById(R.id.btnOrderAction);
        }
    }
}