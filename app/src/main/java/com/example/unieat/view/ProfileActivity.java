package com.example.unieat.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unieat.R;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.SettingsDAO;
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
    private TextView tvUserEmail, tvBalance;
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

        tvBalance = findViewById(R.id.tvBalance);
        TextView tvUserName    = findViewById(R.id.tvUserName);
        TextView tvMemberSince = findViewById(R.id.tvMemberSince);
        tvUserName.setText(presenter.getUserName());
        tvBalance.setText(String.format("R$ %.2f", presenter.getBalance()));
        tvMemberSince.setText(presenter.getMemberSince());

        loadProfilePicture();
        presenter.getUserEmail(this);
        presenter.getTotalOrders(this);

        if (sessionManager.getUserType() == UserType.COZINHEIRO) {
            setupPixSection();
            setupServiceFeeSection();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvBalance.setText(String.format("R$ %.2f", presenter.getBalance()));
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

    private void setupServiceFeeSection() {
        LinearLayout section = findViewById(R.id.sectionServiceFee);
        section.setVisibility(View.VISIBLE);

        android.widget.RadioGroup rgFeeType = findViewById(R.id.rgFeeType);
        android.widget.RadioButton rbPercent = findViewById(R.id.rbPercent);
        android.widget.RadioButton rbFixed   = findViewById(R.id.rbFixed);
        EditText etValue = findViewById(R.id.etServiceFeeValue);
        Button btnSave   = findViewById(R.id.btnSaveServiceFee);

        new SettingsDAO().getServiceFee(new FirebaseCallback<double[]>() {
            @Override public void onSuccess(double[] data) {
                boolean isPercent = data[0] == 1.0;
                rbPercent.setChecked(isPercent);
                rbFixed.setChecked(!isPercent);
                etValue.setText(data[1] % 1 == 0
                        ? String.format("%.0f", data[1])
                        : String.format("%.2f", data[1]));
            }
            @Override public void onFailure(String error) {}
        });

        btnSave.setOnClickListener(v -> {
            String valueStr = etValue.getText().toString().trim();
            if (valueStr.isEmpty()) {
                Toast.makeText(this, "Informe um valor", Toast.LENGTH_SHORT).show();
                return;
            }
            double value;
            try { value = Double.parseDouble(valueStr); }
            catch (NumberFormatException e) {
                Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean isPercent = rgFeeType.getCheckedRadioButtonId() == R.id.rbPercent;
            new SettingsDAO().saveServiceFee(isPercent, value, new FirebaseCallback<Void>() {
                @Override public void onSuccess(Void ignored) {
                    sessionManager.setServiceFee(value);
                    sessionManager.setServiceFeeIsPercent(isPercent);
                    Toast.makeText(ProfileActivity.this, "Taxa salva!", Toast.LENGTH_SHORT).show();
                }
                @Override public void onFailure(String error) {
                    Toast.makeText(ProfileActivity.this, "Erro ao salvar", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupPixSection() {
        LinearLayout sectionPix = findViewById(R.id.sectionPix);
        sectionPix.setVisibility(View.VISIBLE);

        EditText etPixKey   = findViewById(R.id.etPixKey);
        EditText etPixQrUrl = findViewById(R.id.etPixQrUrl);
        Button btnSavePix   = findViewById(R.id.btnSavePix);

        new SettingsDAO().getPixInfo(new FirebaseCallback<String[]>() {
            @Override public void onSuccess(String[] info) {
                etPixKey.setText(info[0]);
                etPixQrUrl.setText(info[1]);
            }
            @Override public void onFailure(String error) {}
        });

        btnSavePix.setOnClickListener(v -> {
            String key   = etPixKey.getText().toString().trim();
            String qrUrl = etPixQrUrl.getText().toString().trim();
            new SettingsDAO().savePixInfo(key, qrUrl, new FirebaseCallback<Void>() {
                @Override public void onSuccess(Void ignored) {
                    Toast.makeText(ProfileActivity.this, "Informações PIX salvas!", Toast.LENGTH_SHORT).show();
                }
                @Override public void onFailure(String error) {
                    Toast.makeText(ProfileActivity.this, "Erro ao salvar PIX", Toast.LENGTH_SHORT).show();
                }
            });
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
