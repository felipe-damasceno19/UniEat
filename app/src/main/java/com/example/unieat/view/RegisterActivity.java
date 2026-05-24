package com.example.unieat.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
    private Button btnRoleStudent, btnRoleKitchen, btnRegister;
    private TextView tvBackToLogin;
    private UserType selectedType = UserType.ALUNO;
    private RegisterPresenter presenter;

    private final int colorRed      = Color.parseColor("#7B1C1C");
    private final int colorWhite    = Color.WHITE;
    private final int colorInactive = Color.parseColor("#F0E8E8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        presenter = new RegisterPresenter(this);

        bindViews();
        setupToggle();
        setupRegister();

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void bindViews() {
        etName        = findViewById(R.id.etName);
        etUsername    = findViewById(R.id.etUsername);
        etEmail       = findViewById(R.id.etEmail);
        etPassword    = findViewById(R.id.etPassword);
        btnRoleStudent = findViewById(R.id.btnRoleStudent);
        btnRoleKitchen = findViewById(R.id.btnRoleKitchen);
        btnRegister   = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void setupToggle() {
        btnRoleStudent.setOnClickListener(v -> {
            selectedType = UserType.ALUNO;
            btnRoleStudent.setBackgroundTintList(ColorStateList.valueOf(colorRed));
            btnRoleStudent.setTextColor(colorWhite);
            btnRoleKitchen.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
            btnRoleKitchen.setTextColor(colorRed);
        });

        btnRoleKitchen.setOnClickListener(v -> {
            selectedType = UserType.COZINHEIRO;
            btnRoleKitchen.setBackgroundTintList(ColorStateList.valueOf(colorRed));
            btnRoleKitchen.setTextColor(colorWhite);
            btnRoleStudent.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
            btnRoleStudent.setTextColor(colorRed);
        });
    }

    private void setupRegister() {
        btnRegister.setOnClickListener(v -> {
            String name     = etName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            btnRegister.setEnabled(false);
            presenter.register(name, username, email, password, selectedType);
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