package com.example.unieat.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unieat.R;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.presenter.ProfilePresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends BaseActivity
        implements ProfilePresenter.ProfileView {

    private ProfilePresenter presenter;
    private TextView tvUserEmail;
    private TextView tvOrderCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        presenter = new ProfilePresenter(this);

        setupNavigation();
        setupViews();

        TextView tvUserName    = findViewById(R.id.tvUserName);
        TextView tvBalance     = findViewById(R.id.tvBalance);
        TextView tvMemberSince = findViewById(R.id.tvMemberSince);
        tvUserName.setText(presenter.getUserName());
        tvBalance.setText(String.format("R$ %.2f", presenter.getBalance()));
        tvMemberSince.setText(presenter.getMemberSince());

        presenter.getUserEmail(this);
        presenter.getTotalOrders(this);
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_profile);
    }

    private void setupViews() {
        tvUserEmail  = findViewById(R.id.tvUserEmail);
        tvOrderCount = findViewById(R.id.tvOrderCount);

        LinearLayout rowChangePassword = findViewById(R.id.rowChangePassword);
        LinearLayout rowDeleteAccount  = findViewById(R.id.rowDeleteAccount);
        Button btnLogout               = findViewById(R.id.btnLogout);

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
                .setPositiveButton("Excluir", (dialog, which) ->
                        presenter.deleteAccount(new FirebaseCallback<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(ProfileActivity.this,
                                        "Erro ao excluir conta: " + error, Toast.LENGTH_SHORT).show();
                            }
                        })
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }


    @Override
    public void onEmailLoaded(String email) {
        tvUserEmail.setText(email);
    }

    @Override
    public void onTotalOrdersLoaded(int total) {
        tvOrderCount.setText(String.valueOf(total));
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}