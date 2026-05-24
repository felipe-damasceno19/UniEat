package com.example.unieat.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unieat.R;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.UserType;
import com.example.unieat.presenter.ProfilePresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends BaseActivity
        implements ProfilePresenter.ProfileView {

    private static final int[] AVATAR_IDS = {
        R.id.avatar1, R.id.avatar2, R.id.avatar3, R.id.avatar4,
        R.id.avatar5, R.id.avatar6, R.id.avatar7, R.id.avatar8
    };

    private ProfilePresenter presenter;
    private SessionManager sessionManager;
    private ImageView ivProfilePicture;
    private TextView tvUserEmail;
    private TextView tvOrderCount;
    private int selectedProfilePicture = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        presenter = new ProfilePresenter(this);
        sessionManager = new SessionManager(this);

        setupNavigation();
        setupViews();

        TextView tvUserName    = findViewById(R.id.tvUserName);
        TextView tvBalance     = findViewById(R.id.tvBalance);
        TextView tvMemberSince = findViewById(R.id.tvMemberSince);
        tvUserName.setText(presenter.getUserName());
        tvBalance.setText(String.format("R$ %.2f", presenter.getBalance()));
        tvMemberSince.setText(presenter.getMemberSince());

        loadProfilePicture();
        presenter.getUserEmail(this);
        presenter.getTotalOrders(this);
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (sessionManager.getUserType() == UserType.COZINHEIRO) {
            bottomNav.getMenu().clear();
            bottomNav.inflateMenu(R.menu.kitchen_nav_menu);
            NavigationHelper.setupKitchenNavigation(this, bottomNav, R.id.nav_kitchen_profile);
        } else {
            NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_profile);
        }
    }

    private void setupViews() {
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUserEmail      = findViewById(R.id.tvUserEmail);
        tvOrderCount     = findViewById(R.id.tvOrderCount);

        ImageView btnEditAvatar = findViewById(R.id.btnEditAvatar);
        btnEditAvatar.setOnClickListener(v -> showAvatarPicker());

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

    private void loadProfilePicture() {
        int cached = sessionManager.getProfilePicture();
        ivProfilePicture.setImageResource(getProfileDrawable(cached));

        new UserDAO().findById(sessionManager.getId(), new FirebaseCallback<com.example.unieat.model.User>() {
            @Override public void onSuccess(com.example.unieat.model.User user) {
                if (user == null) return;
                int pic = user.getProfilePicture();
                sessionManager.setProfilePicture(pic);
                ivProfilePicture.setImageResource(getProfileDrawable(pic));
            }
            @Override public void onFailure(String error) {}
        });
    }

    private void showAvatarPicker() {
        selectedProfilePicture = sessionManager.getProfilePicture();

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pick_avatar, null);
        applyAvatarSelection(dialogView, selectedProfilePicture);

        for (int i = 0; i < AVATAR_IDS.length; i++) {
            int index = i + 1;
            dialogView.findViewById(AVATAR_IDS[i]).setOnClickListener(v -> {
                selectedProfilePicture = index;
                applyAvatarSelection(dialogView, selectedProfilePicture);
            });
        }

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> saveProfilePicture(selectedProfilePicture))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void applyAvatarSelection(View parent, int selected) {
        for (int i = 0; i < AVATAR_IDS.length; i++) {
            ImageView iv = parent.findViewById(AVATAR_IDS[i]);
            iv.setAlpha(i + 1 == selected ? 1.0f : 0.4f);
            iv.setScaleX(i + 1 == selected ? 1.1f : 1.0f);
            iv.setScaleY(i + 1 == selected ? 1.1f : 1.0f);
        }
    }

    private void saveProfilePicture(int index) {
        new UserDAO().updateProfilePicture(sessionManager.getId(), index, new FirebaseCallback<Void>() {
            @Override public void onSuccess(Void v) {
                sessionManager.setProfilePicture(index);
                ivProfilePicture.setImageResource(getProfileDrawable(index));
                Toast.makeText(ProfileActivity.this, "Foto atualizada!", Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(String error) {
                Toast.makeText(ProfileActivity.this, "Erro ao salvar foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static int getProfileDrawable(int index) {
        switch (index) {
            case 2: return R.drawable.profile_2;
            case 3: return R.drawable.profile_3;
            case 4: return R.drawable.profile_4;
            case 5: return R.drawable.profile_5;
            case 6: return R.drawable.profile_6;
            case 7: return R.drawable.profile_7;
            case 8: return R.drawable.profile_8;
            default: return R.drawable.profile_1;
        }
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) ->
                        presenter.deleteAccount(new FirebaseCallback<Void>() {
                            @Override public void onSuccess(Void v) {
                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            @Override public void onFailure(String error) {
                                Toast.makeText(ProfileActivity.this,
                                        "Erro ao excluir conta: " + error, Toast.LENGTH_SHORT).show();
                            }
                        })
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override public void onEmailLoaded(String email) { tvUserEmail.setText(email); }
    @Override public void onTotalOrdersLoaded(int total) { tvOrderCount.setText(String.valueOf(total)); }
    @Override public void onError(String message) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); }
}
