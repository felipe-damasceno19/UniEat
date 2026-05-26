package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.OrderDAO;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.model.Order;
import com.example.unieat.model.User;

import java.util.List;

public class ProfilePresenter {

    public interface ProfileView {
        void onEmailLoaded(String email);
        void onTotalOrdersLoaded(int total);
        void onError(String message);
    }

    private final SessionManager sessionManager;
    private final OrderDAO orderDAO;
    private final UserDAO userDAO;

    public ProfilePresenter(Context context) {
        this.sessionManager = new SessionManager(context);
        this.orderDAO = new OrderDAO();
        this.userDAO = new UserDAO();
    }

    public String getUserName()    { return sessionManager.getName(); }
    public double getBalance()     { return sessionManager.getBalance(); }
    public String getMemberSince() { return sessionManager.getRegistrationDate(); }

    public void getUserEmail(ProfileView view) {
        userDAO.findById(sessionManager.getId(), new FirebaseCallback<User>() {
            @Override public void onSuccess(User user) {
                view.onEmailLoaded(user != null ? user.getEmail() : "");
            }
            @Override public void onFailure(String error) { view.onError(error); }
        });
    }

    public void getTotalOrders(ProfileView view) {
        String userId = sessionManager.getId();
        orderDAO.findAll(new FirebaseCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> orders) {
                int count = 0;
                if (orders != null) {
                    for (Order o : orders) {
                        if (userId.equals(o.getUserId())) count++;
                    }
                }
                view.onTotalOrdersLoaded(count);
            }
            @Override public void onFailure(String error) { view.onError(error); }
        });
    }

    public void logout() { sessionManager.clearSession(); }

    public void deleteAccount(FirebaseCallback<Void> cb) {
        userDAO.delete(sessionManager.getId(), new FirebaseCallback<Void>() {
            @Override public void onSuccess(Void v) {
                sessionManager.clearSession();
                cb.onSuccess(null);
            }
            @Override public void onFailure(String error) { cb.onFailure(error); }
        });
    }
}