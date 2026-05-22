package com.example.unieat.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.unieat.R;
import com.example.unieat.presenter.ProfilePresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends BaseActivity {

    private ProfilePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        presenter = new ProfilePresenter(this);

        setupNavigation();
        setupViews();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_profile);
    }

    private void setupViews() {
        TextView tvBalance = findViewById(R.id.tvBalance);
        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvUserEmail = findViewById(R.id.tvUserEmail);
        TextView tvOrderCount = findViewById(R.id.tvOrderCount);
        TextView tvMemberSince = findViewById(R.id.tvMemberSince);
        LinearLayout rowChangePassword = findViewById(R.id.rowChangePassword);
        LinearLayout rowDeleteAccount = findViewById(R.id.rowDeleteAccount);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvBalance.setText(String.format("R$ %.2f", presenter.getBalance()));
        tvUserName.setText(presenter.getUserName());
        tvUserEmail.setText(presenter.getUserEmail());
        tvOrderCount.setText(String.valueOf(presenter.getTotalOrders()));
        tvMemberSince.setText(presenter.getMemberSince());

        rowChangePassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );

        rowDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());

        btnLogout.setOnClickListener(v -> {
            presenter.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    presenter.deleteAccount();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
