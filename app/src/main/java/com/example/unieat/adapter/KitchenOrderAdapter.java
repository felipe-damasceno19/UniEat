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
import com.example.unieat.dao.PaymentDAO;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.enums.PaymentMethod;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;
import com.example.unieat.model.Payment;
import com.example.unieat.model.User;
import com.example.unieat.util.OrderUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.util.Locale;

public class KitchenOrderAdapter extends RecyclerView.Adapter<KitchenOrderAdapter.KitchenOrderViewHolder> {

    public interface OnOrderActionListener {
        void onChangeStatus(Order order, OrderStatus newStatus);
        void onReject(Order order);
    }

    private static final String[] STATUS_LABELS   = {"Pendente", "Em Preparo", "Pronto", "Entregue"};
    private static final OrderStatus[] STATUS_VALUES = {
            OrderStatus.PENDENTE, OrderStatus.PREPARANDO, OrderStatus.PRONTO, OrderStatus.ENTREGUE
    };

    private List<Order> orders;
    private final Context context;
    private final OnOrderActionListener listener;
    private final UserDAO userDAO = new UserDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

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

        String orderNum = OrderUtils.formatOrderNumber(order.getId());
        holder.tvOrderNumber.setText(orderNum + " • ...");

        if (order.getUserId() != null && !order.getUserId().isEmpty()) {
            userDAO.findById(order.getUserId(), new FirebaseCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    int current = holder.getBindingAdapterPosition();
                    if (current != RecyclerView.NO_ID && current < orders.size()
                            && orders.get(current).getId().equals(order.getId())) {
                        String name = user != null && user.getName() != null ? user.getName() : "—";
                        holder.tvOrderNumber.setText(orderNum + " • " + name);
                    }
                }
                @Override
                public void onFailure(String error) {
                    holder.tvOrderNumber.setText(orderNum + " • —");
                }
            });
        } else {
            holder.tvOrderNumber.setText(orderNum + " • —");
        }

        applyStatusStyle(holder, order.getStatus());

        holder.chipGroupItems.removeAllViews();
        for (OrderItem item : order.getItems()) {
            Chip chip = new Chip(context);
            chip.setText(item.getQuantity() + "x " + item.getDish().getName());
            chip.setClickable(false);
            holder.chipGroupItems.addView(chip);
        }

        String annotation = order.getAnnotation();
        if (annotation != null && !annotation.isEmpty()) {
            holder.tvAnnotation.setVisibility(View.VISIBLE);
            holder.tvAnnotation.setText("Obs: " + annotation);
        } else {
            holder.tvAnnotation.setVisibility(View.GONE);
        }

        holder.tvPaymentMethod.setText("—");
        holder.tvPaymentMethod.setTextColor(0xFF555555);
        holder.tvPaymentMethod.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFFEEEEEE));
        holder.tvOrderTotal.setText("");

        paymentDAO.findByOrderId(order.getId(), new FirebaseCallback<Payment>() {
            @Override public void onSuccess(Payment payment) {
                int pos = holder.getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;
                if (payment == null) return;

                String label;
                int textColor;
                int bgColor;

                PaymentMethod method = payment.getMethod();
                if (method == PaymentMethod.PIX) {
                    label     = "PIX";
                    textColor = 0xFF1565C0;
                    bgColor   = 0xFFE3F2FD;
                } else if (method == PaymentMethod.CASH) {
                    label     = "Dinheiro";
                    textColor = 0xFF2E7D32;
                    bgColor   = 0xFFE8F5E9;
                } else if (method == PaymentMethod.BALANCE) {
                    label     = "Saldo";
                    textColor = 0xFF7B1C1C;
                    bgColor   = 0xFFFFEEE8;
                } else {
                    label     = "—";
                    textColor = 0xFF555555;
                    bgColor   = 0xFFEEEEEE;
                }

                holder.tvPaymentMethod.setText(label);
                holder.tvPaymentMethod.setTextColor(textColor);
                holder.tvPaymentMethod.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(bgColor));
                holder.tvOrderTotal.setText(
                        String.format(Locale.getDefault(), "R$ %.2f", payment.getAmount()));
            }
            @Override public void onFailure(String error) {}
        });

        holder.btnOrderAction.setText("Alterar Status");
        holder.btnOrderAction.setOnClickListener(v -> showStatusDialog(order));
        holder.btnRejectOrder.setOnClickListener(v -> showRejectConfirmation(order));
    }

    private void showRejectConfirmation(Order order) {
        new AlertDialog.Builder(context)
                .setTitle("Rejeitar Pedido")
                .setMessage("Tem certeza que deseja rejeitar o pedido " + OrderUtils.formatOrderNumber(order.getId()) + "?")
                .setPositiveButton("Rejeitar", (dialog, which) ->
                        listener.onReject(order))
                .setNegativeButton("Cancelar", null)
                .show();
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
                holder.tvStatusBadge.setTextColor(0xFF7B4A00);
                holder.tvStatusBadge.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFFFF8E8));
                break;
            case PREPARANDO:
                holder.viewStatusIndicator.setBackgroundColor(0xFF1565C0);
                holder.tvStatusBadge.setText("EM PREPARO");
                holder.tvStatusBadge.setTextColor(0xFF1565C0);
                holder.tvStatusBadge.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFE3F0FC));
                break;
            case PRONTO:
                holder.viewStatusIndicator.setBackgroundColor(0xFF2E7D32);
                holder.tvStatusBadge.setText("PRONTO");
                holder.tvStatusBadge.setTextColor(0xFF2E7D32);
                holder.tvStatusBadge.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFE8F5E9));
                break;
            case REJEITADO:
                holder.viewStatusIndicator.setBackgroundColor(0xFFCC2222);
                holder.tvStatusBadge.setText("REJEITADO");
                holder.tvStatusBadge.setTextColor(0xFFCC2222);
                holder.tvStatusBadge.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFFFEAEA));
                break;
            default:
                holder.viewStatusIndicator.setBackgroundColor(0xFF2E7D32);
                holder.tvStatusBadge.setText("ENTREGUE");
                holder.tvStatusBadge.setTextColor(0xFF2E7D32);
                holder.tvStatusBadge.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFE8F5E9));
                break;
        }
    }

    @Override
    public int getItemCount() { return orders.size(); }

    static class KitchenOrderViewHolder extends RecyclerView.ViewHolder {
        View viewStatusIndicator;
        TextView tvOrderNumber, tvStatusBadge, tvAnnotation;
        TextView tvPaymentMethod, tvOrderTotal;
        ChipGroup chipGroupItems;
        MaterialButton btnOrderAction, btnRejectOrder;

        KitchenOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
            tvOrderNumber       = itemView.findViewById(R.id.tvOrderNumber);
            tvAnnotation        = itemView.findViewById(R.id.tvAnnotation);
            tvStatusBadge       = itemView.findViewById(R.id.tvStatusBadge);
            tvPaymentMethod     = itemView.findViewById(R.id.tvPaymentMethod);
            tvOrderTotal        = itemView.findViewById(R.id.tvOrderTotal);
            chipGroupItems      = itemView.findViewById(R.id.chipGroupItems);
            btnOrderAction      = itemView.findViewById(R.id.btnOrderAction);
            btnRejectOrder      = itemView.findViewById(R.id.btnRejectOrder);
        }
    }
}
