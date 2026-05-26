package com.example.unieat.presenter;

import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.enums.UserType;
import com.example.unieat.model.User;

import java.util.UUID;

public class RegisterPresenter {

    public interface View {
        void onRegisterSuccess();
        void onRegisterError(String message);
    }

    private final UserDAO userDAO;
    private final View view;

    public RegisterPresenter(View view) {
        this.userDAO = new UserDAO();
        this.view = view;
    }

    public void register(String name, String username, String email, String password, UserType userType) {
        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            view.onRegisterError("Preencha todos os campos");
            return;
        }

        if (username.length() < 3) {
            view.onRegisterError("O username deve ter pelo menos 3 caracteres");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.onRegisterError("Informe um email válido");
            return;
        }

        if (password.length() < 6) {
            view.onRegisterError("A senha deve ter pelo menos 6 caracteres");
            return;
        }

        userDAO.findByUsername(username, new FirebaseCallback<User>() {
            @Override public void onSuccess(User existing) {
                if (existing != null) {
                    view.onRegisterError("Esse username já está sendo utilizado");
                    return;
                }
                userDAO.findByEmail(email, new FirebaseCallback<User>() {
                    @Override public void onSuccess(User existing) {
                        if (existing != null) {
                            view.onRegisterError("Esse email já está sendo utilizado");
                            return;
                        }
                        User user = new User(
                                UUID.randomUUID().toString(),
                                name, username, password,
                                email, 0.0, userType
                        );
                        userDAO.insert(user, new FirebaseCallback<String>() {
                            @Override public void onSuccess(String id) { view.onRegisterSuccess(); }
                            @Override public void onFailure(String error) { view.onRegisterError("Erro ao cadastrar: " + error); }
                        });
                    }
                    @Override public void onFailure(String error) { view.onRegisterError("Erro ao verificar email: " + error); }
                });
            }
            @Override public void onFailure(String error) { view.onRegisterError("Erro ao verificar username: " + error); }
        });
    }
}