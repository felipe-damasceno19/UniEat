package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.UserType;
import com.example.unieat.model.User;

public class LoginPresenter {

    public interface LoginView {
        void onLoginSuccess(UserType userType);
        void onLoginError(String message);
    }

    private final UserDAO userDAO;
    private final SessionManager sessionManager;
    private final LoginView view;
    public LoginPresenter(Context context, LoginView view) {
        this.userDAO = new UserDAO();
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
                view.onLoginSuccess(user.getType());
            }

            @Override
            public void onFailure(String erro) {
                view.onLoginError("Erro de conexão: " + erro);
            }
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
