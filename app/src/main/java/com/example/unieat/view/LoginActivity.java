package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.unieat.R;
import com.example.unieat.data.DataSeeder;
import com.example.unieat.enums.UserType;
import com.example.unieat.presenter.LoginPresenter;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends BaseActivity implements LoginPresenter.LoginView {

    private LoginPresenter presenter;
    private Button btnLogin;
    private TextInputEditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DataSeeder.seed();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        presenter = new LoginPresenter(this, this);

        if (presenter.isLoggedIn()) {
            navigateToHome(presenter.isStudent() ? UserType.ALUNO : UserType.COZINHEIRO);
            return;
        }

        bindViews();
        setupLogin();
        setupRegister();
    }

    private void bindViews() {
        btnLogin = findViewById(R.id.btnEntrar);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etSenha);
    }

    private void setupLogin() {
        btnLogin.setOnClickListener(v -> {
            String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false);
            presenter.login(email, password);
        });
    }

    @Override
    public void onLoginSuccess(UserType userType) {
        navigateToHome(userType);
    }

    @Override
    public void onLoginError(String message) {
        btnLogin.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setupRegister() {
        TextView tvCadastrar = findViewById(R.id.tvCadastrar);
        tvCadastrar.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void navigateToHome(UserType type) {
        Intent intent = type == UserType.ALUNO
                ? new Intent(this, StudentHomeActivity.class)
                : new Intent(this, KitchenHomeActivity.class);
        startActivity(intent);
        finish();
    }
}
