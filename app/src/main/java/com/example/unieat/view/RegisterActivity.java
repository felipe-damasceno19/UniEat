package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unieat.R;
import com.example.unieat.enums.UserType;
import com.example.unieat.presenter.RegisterPresenter;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends BaseActivity implements RegisterPresenter.View {

    private TextInputEditText etName, etUsername, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvBackToLogin;
    private RegisterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        presenter = new RegisterPresenter(this);

        bindViews();
        setupRegister();

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void bindViews() {
        etName        = findViewById(R.id.etName);
        etUsername    = findViewById(R.id.etUsername);
        etEmail       = findViewById(R.id.etEmail);
        etPassword    = findViewById(R.id.etPassword);
        btnRegister   = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void setupRegister() {
        btnRegister.setOnClickListener(v -> {
            String name     = etName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            btnRegister.setEnabled(false);
            presenter.register(name, username, email, password, UserType.ALUNO);
        });
    }


    @Override
    public void onRegisterSuccess() {
        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegisterError(String message) {
        btnRegister.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
