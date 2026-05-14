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

    public UserType login(String email, String password, boolean isStudent) {
        User user = userDAO.findByEmail(email);

        if(user == null) return null;
        if(!user.getPassword().equals(password)) return null;

        if(isStudent && user.getType() != UserType.ALUNO) return null;
        if(!isStudent && user.getType() != UserType.COZINHEIRO) return null;

        sessionManager.saveSession(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getBalance(),
                user.getType()
        );

        return user.getType();
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
