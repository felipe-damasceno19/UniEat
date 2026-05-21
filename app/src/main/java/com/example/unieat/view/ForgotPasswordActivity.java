package com.example.unieat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unieat.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private MaterialButton btnEnviarCodigo;
    private TextView tvVoltarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        bindViews();
        setupEnviarCodigo();
        setupVoltarLogin();
    }

    private void bindViews() {
        etEmail = findViewById(R.id.etEmail);
        btnEnviarCodigo = findViewById(R.id.btnEnviarCodigo);
        tvVoltarLogin = findViewById(R.id.tvVoltarLogin);
    }

    private void setupEnviarCodigo() {
        btnEnviarCodigo.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

            if (email.isEmpty()) {
                Toast.makeText(this, "Informe seu e-mail!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Código enviado para " + email, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, ResetPasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });
    }

    private void setupVoltarLogin() {
        tvVoltarLogin.setOnClickListener(v -> finish());
    }
}
