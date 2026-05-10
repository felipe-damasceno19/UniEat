package com.example.unieat.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.unieat.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnSouAluno = findViewById(R.id.btnSouAluno);
        Button btnSouCozinha = findViewById(R.id.btnSouCozinha);

        // Define as cores
        int colorRed = Color.parseColor("#7B1C1C");
        int colorWhite = Color.WHITE;
        int colorInactive = Color.parseColor("#F0E8E8"); // Cor de fundo do toggle (desativado)

        btnSouAluno.setOnClickListener(v -> {
            // Selecionar Aluno (Fica Vermelho)
            btnSouAluno.setBackgroundTintList(ColorStateList.valueOf(colorRed));
            btnSouAluno.setTextColor(colorWhite);
            
            // Desmarcar Cozinha (Fica Transparente/Cinza)
            btnSouCozinha.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
            btnSouCozinha.setTextColor(colorRed);
        });

        btnSouCozinha.setOnClickListener(v -> {
            // Selecionar Cozinha (Fica Vermelho)
            btnSouCozinha.setBackgroundTintList(ColorStateList.valueOf(colorRed));
            btnSouCozinha.setTextColor(colorWhite);
            
            // Desmarcar Aluno (Fica Transparente/Cinza)
            btnSouAluno.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
            btnSouAluno.setTextColor(colorRed);
        });
    }
}
