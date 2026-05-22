package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.OrderDAO;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.model.User;

public class ProfilePresenter {

    private final SessionManager sessionManager;
    private final OrderDAO orderDAO;
    private final UserDAO userDAO;

    public ProfilePresenter(Context context) {
        sessionManager = new SessionManager(context);
        orderDAO = new OrderDAO(context);
        userDAO = new UserDAO(context);
    }

    public String getUserName() {
        return sessionManager.getName();
    }

    public String getUserEmail() {
        User user = userDAO.findById(sessionManager.getId());
        return user != null ? user.getEmail() : "";
    }

    public double getBalance() {
        return sessionManager.getBalance();
    }

    public int getTotalOrders() {
        return orderDAO.findAll().size();
    }

    public String getMemberSince() {
        return sessionManager.getRegistrationDate();
    }

    public void logout() {
        sessionManager.clearSession();
    }

    public void deleteAccount() {
        userDAO.delete(sessionManager.getId());
        sessionManager.clearSession();
    }
}
