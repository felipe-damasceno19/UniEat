package com.example.unieat.presenter;

import android.content.Context;

import com.example.unieat.dao.UserDAO;
import com.example.unieat.enums.UserType;
import com.example.unieat.model.User;

import java.util.UUID;

public class RegisterPresenter {

    public interface View {
        void onRegisterSuccess();
        void onRegisterError(String message);
    }

    private UserDAO userDAO;
    private final View view;

    public RegisterPresenter(Context context, View view) {
        this.userDAO = new UserDAO(context);
        this.view = view;
    }

    public void register(String name, String username, String email, String password, UserType userType) {
        if(name.isEmpty() || username.isEmpty() || email.isEmpty()|| password.isEmpty()) {
            view.onRegisterError("Preencha todos os campos");
            return;
        }

        if(userDAO.findByUsername(username) != null) {
            view.onRegisterError("Esse username já está sendo utilizado");
            return;
        }

        if(userDAO.findByEmail(email) != null) {
            view.onRegisterError("Esse email já está sendo utilizado");
            return;
        }

        if(password.length() < 6) {
            view.onRegisterError("A senha deve ter pelo menos 6 caracteres");
            return;
        }

        User user = new User(
                UUID.randomUUID().toString(),
                name,
                username,
                password,
                email,
                0.0,
                userType
        );

        userDAO.insert(user);
        view.onRegisterSuccess();
    }
}
