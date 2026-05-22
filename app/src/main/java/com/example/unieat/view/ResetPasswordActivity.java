package com.example.unieat.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.unieat.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ResetPasswordActivity extends BaseActivity {

    private TextInputEditText etNovaSenha, etConfirmarSenha;
    private RadioButton rbMinChars, rbMinNumber, rbSpecialChar;
    private MaterialButton btnRedefinirSenha;
    private TextView tvCancelarVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        bindViews();
        setupToolbar();
        setupPasswordWatcher();
        setupRedefinirSenha();
        setupCancelarVoltar();
    }

    private void bindViews() {
        etNovaSenha = findViewById(R.id.etNovaSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha);
        rbMinChars = findViewById(R.id.rbMinChars);
        rbMinNumber = findViewById(R.id.rbMinNumber);
        rbSpecialChar = findViewById(R.id.rbSpecialChar);
        btnRedefinirSenha = findViewById(R.id.btnRedefinirSenha);
        tvCancelarVoltar = findViewById(R.id.tvCancelarVoltar);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupPasswordWatcher() {
        etNovaSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateRequirements(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validateRequirements(String password) {
        rbMinChars.setChecked(password.length() >= 8);
        rbMinNumber.setChecked(password.matches(".*\\d.*"));
        rbSpecialChar.setChecked(password.matches(".*[@$!%*?].*"));
    }

    private void setupRedefinirSenha() {
        btnRedefinirSenha.setOnClickListener(v -> {
            String novaSenha = etNovaSenha.getText() != null ? etNovaSenha.getText().toString() : "";
            String confirmar = etConfirmarSenha.getText() != null ? etConfirmarSenha.getText().toString() : "";

            if (novaSenha.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!rbMinChars.isChecked() || !rbMinNumber.isChecked() || !rbSpecialChar.isChecked()) {
                Toast.makeText(this, "A senha não atende aos requisitos!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!novaSenha.equals(confirmar)) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Senha redefinida com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setupCancelarVoltar() {
        tvCancelarVoltar.setOnClickListener(v -> finish());
    }
}
