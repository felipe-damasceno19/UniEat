package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unieat.R;
import com.example.unieat.enums.UserType;
import com.example.unieat.presenter.LoginPresenter;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private LoginPresenter presenter;
    private Button btnLogin;
    private TextInputEditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        presenter = new LoginPresenter(this);

        if (presenter.isLoggedIn()) {
            if (presenter.isStudent()) {
                navigateToHome(UserType.ALUNO);
            } else {
                navigateToHome(UserType.COZINHEIRO);
            }
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
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            UserType loggedType = presenter.login(email, password);

            if (loggedType != null) {
                navigateToHome(loggedType);
            } else {
                Toast.makeText(this, "E-mail ou senha incorretos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRegister() {
        TextView tvCadastrar = findViewById(R.id.tvCadastrar);
        tvCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void navigateToHome(UserType type) {
        Intent intent;
        if (type == UserType.ALUNO) {
            intent = new Intent(this, StudentHomeActivity.class);
        } else {
            intent = new Intent(this, KitchenHomeActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
