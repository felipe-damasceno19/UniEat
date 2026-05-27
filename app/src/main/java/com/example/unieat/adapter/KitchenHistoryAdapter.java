package com.example.unieat.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
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
import com.example.unieat.util.DateUtils;
import com.example.unieat.util.OrderUtils;

import java.util.List;
import java.util.Locale;

public class KitchenHistoryAdapter extends RecyclerView.Adapter<KitchenHistoryAdapter.HistoryViewHolder> {

    private static final int[] AVATAR_COLORS = {
        0xFFFFB84C,
        0xFF1565C0,
        0xFF2E7D32,
        0xFF7B1C1C
    };

    private List<Order> orders;
    private final Context context;
    private final UserDAO userDAO = new UserDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public KitchenHistoryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    public void updateData(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_kitchen_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvHistOrderNumber.setText("Pedido " + OrderUtils.formatOrderNumber(order.getId()));
        holder.tvHistOrderDate.setText(DateUtils.formatDate(order.getTime()));

        applyStatusStyle(holder, order.getStatus());

        StringBuilder itemsBuilder = new StringBuilder();
        for (int i = 0; i < order.getItems().size(); i++) {
            OrderItem item = order.getItems().get(i);
            if (i > 0) itemsBuilder.append(", ");
            itemsBuilder.append(item.getQuantity()).append("x ").append(item.getDish().getName());
        }
        holder.tvHistItems.setText(itemsBuilder.toString());

        holder.tvHistAvatar.setText("...");
        holder.tvHistAvatar.setBackgroundTintList(ColorStateList.valueOf(0xFFCCCCCC));
        holder.tvHistCustomerName.setText("Carregando...");

        if (order.getUserId() != null && !order.getUserId().isEmpty()) {
            userDAO.findById(order.getUserId(), new FirebaseCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    int current = holder.getBindingAdapterPosition();
                    if (current == RecyclerView.NO_POSITION) return;
                    if (current >= orders.size()) return;
                    if (!orders.get(current).getId().equals(order.getId())) return;

                    String name = user != null && user.getName() != null ? user.getName() : "—";
                    holder.tvHistCustomerName.setText(name);

                    String initial = name.length() > 0 ? name.substring(0, 1).toUpperCase() : "?";
                    holder.tvHistAvatar.setText(initial);
                    char c = initial.charAt(0);
                    int colorIndex;
                    if (c >= 'A' && c <= 'F') colorIndex = 0;
                    else if (c >= 'G' && c <= 'M') colorIndex = 1;
                    else if (c >= 'N' && c <= 'S') colorIndex = 2;
                    else colorIndex = 3;
                    holder.tvHistAvatar.setBackgroundTintList(
                            ColorStateList.valueOf(AVATAR_COLORS[colorIndex]));
                    holder.tvHistAvatar.setTextColor(0xFFFFFFFF);
                }
                @Override
                public void onFailure(String error) {
                    holder.tvHistCustomerName.setText("—");
                    holder.tvHistAvatar.setText("?");
                }
            });
        } else {
            holder.tvHistCustomerName.setText("—");
            holder.tvHistAvatar.setText("?");
        }

        holder.tvHistPaymentMethod.setText("—");
        holder.tvHistPaymentMethod.setTextColor(0xFF555555);
        holder.tvHistPaymentMethod.setBackgroundTintList(ColorStateList.valueOf(0xFFEEEEEE));
        holder.tvHistTotal.setText("");

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
                    label = "PIX";
                    textColor = 0xFF1565C0;
                    bgColor = 0xFFE3F2FD;
                } else if (method == PaymentMethod.CASH) {
                    label = "Dinheiro";
                    textColor = 0xFF2E7D32;
                    bgColor = 0xFFE8F5E9;
                } else if (method == PaymentMethod.BALANCE) {
                    label = "Saldo";
                    textColor = 0xFF7B1C1C;
                    bgColor = 0xFFFFEEE8;
                } else {
                    label = "—";
                    textColor = 0xFF555555;
                    bgColor = 0xFFEEEEEE;
                }

                holder.tvHistPaymentMethod.setText(label);
                holder.tvHistPaymentMethod.setTextColor(textColor);
                holder.tvHistPaymentMethod.setBackgroundTintList(ColorStateList.valueOf(bgColor));
                holder.tvHistTotal.setText(
                        String.format(Locale.getDefault(), "R$ %.2f", payment.getAmount()));
            }
            @Override public void onFailure(String error) {}
        });
    }

    private void applyStatusStyle(HistoryViewHolder holder, OrderStatus status) {
        switch (status) {
            case ENTREGUE:
                holder.viewStatusIndicator.setBackgroundColor(0xFF2E7D32);
                holder.tvHistStatus.setText("ENTREGUE");
                holder.tvHistStatus.setTextColor(0xFF2E7D32);
                holder.tvHistStatus.setBackgroundTintList(ColorStateList.valueOf(0xFFE8F5E9));
                break;
            case REJEITADO:
                holder.viewStatusIndicator.setBackgroundColor(0xFFCC2222);
                holder.tvHistStatus.setText("REJEITADO");
                holder.tvHistStatus.setTextColor(0xFFCC2222);
                holder.tvHistStatus.setBackgroundTintList(ColorStateList.valueOf(0xFFFFEAEA));
                break;
            case PENDENTE:
                holder.viewStatusIndicator.setBackgroundColor(0xFFFFB84C);
                holder.tvHistStatus.setText("PENDENTE");
                holder.tvHistStatus.setTextColor(0xFF7B4A00);
                holder.tvHistStatus.setBackgroundTintList(ColorStateList.valueOf(0xFFFFF8E8));
                break;
            case PREPARANDO:
                holder.viewStatusIndicator.setBackgroundColor(0xFF1565C0);
                holder.tvHistStatus.setText("EM PREPARO");
                holder.tvHistStatus.setTextColor(0xFF1565C0);
                holder.tvHistStatus.setBackgroundTintList(ColorStateList.valueOf(0xFFE3F0FC));
                break;
            default:
                holder.viewStatusIndicator.setBackgroundColor(0xFF2E7D32);
                holder.tvHistStatus.setText("PRONTO");
                holder.tvHistStatus.setTextColor(0xFF2E7D32);
                holder.tvHistStatus.setBackgroundTintList(ColorStateList.valueOf(0xFFE8F5E9));
                break;
        }
    }

    @Override
    public int getItemCount() { return orders.size(); }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        View viewStatusIndicator;
        TextView tvHistOrderNumber, tvHistOrderDate, tvHistStatus;
        TextView tvHistAvatar, tvHistCustomerName, tvHistItems;
        TextView tvHistPaymentMethod, tvHistTotal;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
            tvHistOrderNumber   = itemView.findViewById(R.id.tvHistOrderNumber);
            tvHistOrderDate     = itemView.findViewById(R.id.tvHistOrderDate);
            tvHistStatus        = itemView.findViewById(R.id.tvHistStatus);
            tvHistAvatar        = itemView.findViewById(R.id.tvHistAvatar);
            tvHistCustomerName  = itemView.findViewById(R.id.tvHistCustomerName);
            tvHistItems         = itemView.findViewById(R.id.tvHistItems);
            tvHistPaymentMethod = itemView.findViewById(R.id.tvHistPaymentMethod);
            tvHistTotal         = itemView.findViewById(R.id.tvHistTotal);
        }
    }
}
