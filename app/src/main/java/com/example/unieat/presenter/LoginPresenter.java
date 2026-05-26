package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.enums.UserType;
import com.example.unieat.model.Order;
import com.example.unieat.model.User;

import java.util.List;

public class LoginPresenter {

    public interface LoginView {
        void onLoginSuccess(UserType userType);
        void onLoginError(String message);
    }

    private final UserDAO userDAO;
    private final OrderDAO orderDAO;
    private final SessionManager sessionManager;
    private final LoginView view;

    public LoginPresenter(Context context, LoginView view) {
        this.userDAO = new UserDAO();
        this.orderDAO = new OrderDAO();
        this.sessionManager = new SessionManager(context);
        this.view = view;
    }

    public void login(String email, String password) {
        userDAO.findByEmail(email, new FirebaseCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if(user == null) {
                    view.onLoginError("Usuário não encontrado");
                    return;
                }
                if(!user.getPassword().equals(password)) {
                    view.onLoginError("Senha incorreta");
                    return;
                }
                sessionManager.saveSession(
                        user.getId(),
                        user.getName(),
                        user.getUsername(),
                        user.getBalance(),
                        user.getType()
                );
                restoreActiveOrder(user.getId());
                view.onLoginSuccess(user.getType());
            }

            @Override
            public void onFailure(String erro) {
                view.onLoginError("Erro de conexão: " + erro);
            }
        });
    }

    private void restoreActiveOrder(String userId) {
        orderDAO.findByUserId(userId, new FirebaseCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> orders) {
                if (orders == null) return;
                for (Order order : orders) {
                    OrderStatus s = order.getStatus();
                    if (s == OrderStatus.PENDENTE || s == OrderStatus.PREPARANDO || s == OrderStatus.PRONTO) {
                        sessionManager.setActiveOrderId(order.getId());
                        return;
                    }
                }
            }
            @Override public void onFailure(String error) {}
        });
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public void logout() {
        sessionManager.clearSession();
    }

    public boolean isStudent() {
        return sessionManager.isStudent();
    }

}
