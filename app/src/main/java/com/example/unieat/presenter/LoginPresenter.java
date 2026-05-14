package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.UserDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.UserType;
import com.example.unieat.model.User;

public class LoginPresenter {

    private UserDAO userDAO;
    private SessionManager sessionManager;

    public LoginPresenter(Context context) {
        userDAO = new UserDAO(context);
        sessionManager = new SessionManager(context);
    }

    public boolean login(String username, String password, boolean isStudent) {
        User user = userDAO.findByUsername(username);

        if(user == null) return false;
        if(!user.getPassword().equals(password)) return false;

        if(isStudent && user.getType() != UserType.ALUNO) return false;
        if(!isStudent && user.getType() != UserType.COZINHEIRO) return false;

        sessionManager.saveSession(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getBalance(),
                user.getType()
        );

        return true;
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
