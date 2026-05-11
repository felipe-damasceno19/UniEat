package com.example.unieat.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unieat.R;
import com.example.unieat.presenter.LoginPresenter;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private LoginPresenter presenter;
    private Button btnStudent, btnKitchen, btnLogin;
    private TextInputEditText etEmail, etPassword;
    private boolean isStudentMode = true;

    private final int colorRed = Color.parseColor("#7B1C1C");
    private final int colorWhite = Color.WHITE;
    private final int colorInactive = Color.parseColor("#F0E8E8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        presenter = new LoginPresenter(this);

        // se já está logado, pula o login
        if (presenter.isLoggedIn()) {
            navigateToHome();
            return;
        }

        bindViews();
        setupToggle();
        setupLogin();
    }

    private void bindViews() {
        btnStudent = findViewById(R.id.btnSouAluno);
        btnKitchen = findViewById(R.id.btnSouCozinha);
        btnLogin = findViewById(R.id.btnEntrar);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etSenha);
    }

    private void setupToggle() {
        btnStudent.setOnClickListener(v -> {
            isStudentMode = true;
            btnStudent.setBackgroundTintList(ColorStateList.valueOf(colorRed));
            btnStudent.setTextColor(colorWhite);
            btnKitchen.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
            btnKitchen.setTextColor(colorRed);
        });

        btnKitchen.setOnClickListener(v -> {
            isStudentMode = false;
            btnKitchen.setBackgroundTintList(ColorStateList.valueOf(colorRed));
            btnKitchen.setTextColor(colorWhite);
            btnStudent.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
            btnStudent.setTextColor(colorRed);
        });
    }

    private void setupLogin() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = presenter.login(email, password, isStudentMode);

            if (success) {
                navigateToHome();
            } else {
                Toast.makeText(this, "Crendenciais inválidas ou tipo de conta errado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        Intent intent;
        if (presenter.isStudent()) {
            intent = new Intent(this, StudentHomeActivity.class);
        } else {
            intent = new Intent(); //(this, KitchenHomeActivity.class)
        }
        startActivity(intent);
        finish();
    }
}