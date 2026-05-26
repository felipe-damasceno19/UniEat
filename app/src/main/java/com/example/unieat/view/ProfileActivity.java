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

import com.google.android.material.card.MaterialCardView;

import com.example.unieat.R;
import com.example.unieat.dao.BannerDAO;
import com.example.unieat.dao.CouponDAO;
import com.example.unieat.dao.DishDAO;
import com.example.unieat.dao.FirebaseCallback;
import com.example.unieat.dao.SettingsDAO;
import com.example.unieat.dao.UserDAO;
import com.example.unieat.data.SessionManager;
import com.example.unieat.enums.UserType;
import com.example.unieat.model.Banner;
import com.example.unieat.model.Coupon;
import com.example.unieat.model.Dish;
import com.example.unieat.presenter.ProfilePresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

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
    private List<String> selectedBannerDishIds = new java.util.ArrayList<>();
    private List<Dish> allDishes = new java.util.ArrayList<>();

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

        if (sessionManager.getUserType() == UserType.COZINHEIRO) {
            TextView tvOrderCountLabel = findViewById(R.id.tvOrderCountLabel);
            tvOrderCountLabel.setText("Pedidos Recebidos");
            setupPixSection();
            setupServiceFeeSection();
            setupCouponCreationSection();
            setupBannerSection();
        } else {
            setupCouponRedemptionSection();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvBalance.setText(String.format("R$ %.2f", presenter.getBalance()));
        if (sessionManager.getUserType() == UserType.COZINHEIRO) {
            presenter.getTotalOrdersReceived(this);
        } else {
            presenter.getTotalOrders(this);
        }
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

        LinearLayout rowServiceFee          = findViewById(R.id.rowServiceFee);
        MaterialCardView cardServiceFeeContent = findViewById(R.id.cardServiceFeeContent);
        ImageView ivServiceFeeArrow         = findViewById(R.id.ivServiceFeeArrow);
        rowServiceFee.setOnClickListener(v -> toggleSection(cardServiceFeeContent, ivServiceFeeArrow));

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

        LinearLayout rowPix         = findViewById(R.id.rowPix);
        MaterialCardView cardPixContent = findViewById(R.id.cardPixContent);
        ImageView ivPixArrow        = findViewById(R.id.ivPixArrow);

        rowPix.setOnClickListener(v -> toggleSection(cardPixContent, ivPixArrow));

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

    private void setupCouponRedemptionSection() {
        LinearLayout section = findViewById(R.id.sectionBeneficios);
        section.setVisibility(View.VISIBLE);

        LinearLayout rowUseCoupon = findViewById(R.id.rowUseCoupon);
        rowUseCoupon.setOnClickListener(v -> showCouponDialog());
    }

    private void showCouponDialog() {
        android.widget.EditText etCode = new android.widget.EditText(this);
        etCode.setHint("Ex: BEMVINDO10");
        etCode.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        etCode.setPadding(48, 24, 48, 24);

        new AlertDialog.Builder(this)
                .setTitle("Usar Cupom")
                .setMessage("Digite o código do cupom para adicionar saldo:")
                .setView(etCode)
                .setPositiveButton("Aplicar", (dialog, which) -> {
                    String code = etCode.getText().toString().trim().toUpperCase();
                    if (code.isEmpty()) {
                        Toast.makeText(this, "Informe o código do cupom", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    redeemCoupon(code);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void redeemCoupon(String code) {
        String userId = sessionManager.getId();
        CouponDAO couponDAO = new CouponDAO();

        couponDAO.findByCode(code, new FirebaseCallback<Coupon>() {
            @Override public void onSuccess(Coupon coupon) {
                if (coupon == null) {
                    Toast.makeText(ProfileActivity.this, "Cupom inválido", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (coupon.isUsedBy(userId)) {
                    Toast.makeText(ProfileActivity.this, "Você já usou este cupom", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (coupon.isExhausted()) {
                    Toast.makeText(ProfileActivity.this, "Cupom esgotado", Toast.LENGTH_SHORT).show();
                    return;
                }
                double newBalance = sessionManager.getBalance() + coupon.getValue();
                couponDAO.redeem(code, userId, new FirebaseCallback<Void>() {
                    @Override public void onSuccess(Void v) {
                        new UserDAO().updateBalance(userId, newBalance, new FirebaseCallback<Void>() {
                            @Override public void onSuccess(Void v) {}
                            @Override public void onFailure(String e) {}
                        });
                        sessionManager.updateBalance(newBalance);
                        tvBalance.setText(String.format("R$ %.2f", newBalance));
                        Toast.makeText(ProfileActivity.this,
                                String.format("Cupom aplicado! +R$ %.2f adicionados ao saldo", coupon.getValue()),
                                Toast.LENGTH_LONG).show();
                    }
                    @Override public void onFailure(String error) {
                        Toast.makeText(ProfileActivity.this, "Erro ao aplicar cupom", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override public void onFailure(String error) {
                Toast.makeText(ProfileActivity.this, "Erro ao verificar cupom", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCouponCreationSection() {
        LinearLayout section = findViewById(R.id.sectionCoupons);
        section.setVisibility(View.VISIBLE);

        LinearLayout rowCoupons          = findViewById(R.id.rowCoupons);
        MaterialCardView cardCouponsContent = findViewById(R.id.cardCouponsContent);
        ImageView ivCouponsArrow         = findViewById(R.id.ivCouponsArrow);
        rowCoupons.setOnClickListener(v -> {
            toggleSection(cardCouponsContent, ivCouponsArrow);
            if (cardCouponsContent.getVisibility() == View.VISIBLE) {
                loadCouponList();
            }
        });

        EditText etCode    = findViewById(R.id.etCouponCode);
        EditText etValue   = findViewById(R.id.etCouponValue);
        EditText etMaxUses = findViewById(R.id.etCouponMaxUses);
        Button btnCreate   = findViewById(R.id.btnCreateCoupon);

        CouponDAO couponDAO = new CouponDAO();

        btnCreate.setOnClickListener(v -> {
            String code       = etCode.getText().toString().trim().toUpperCase();
            String valueStr   = etValue.getText().toString().trim();
            String maxUsesStr = etMaxUses.getText().toString().trim();

            if (code.isEmpty() || valueStr.isEmpty() || maxUsesStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double couponValue;
            int maxUses;
            try {
                couponValue = Double.parseDouble(valueStr);
                maxUses     = Integer.parseInt(maxUsesStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Valor ou quantidade inválidos", Toast.LENGTH_SHORT).show();
                return;
            }

            Coupon coupon = new Coupon(code, couponValue, maxUses);
            couponDAO.insert(coupon, new FirebaseCallback<Void>() {
                @Override public void onSuccess(Void v) {
                    etCode.setText("");
                    etValue.setText("");
                    etMaxUses.setText("");
                    Toast.makeText(ProfileActivity.this, "Cupom \"" + code + "\" criado!", Toast.LENGTH_SHORT).show();
                    loadCouponList();
                }
                @Override public void onFailure(String error) {
                    Toast.makeText(ProfileActivity.this, "Erro ao criar cupom", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadCouponList() {
        LinearLayout llCouponList = findViewById(R.id.llCouponList);
        llCouponList.removeAllViews();

        new CouponDAO().findAll(new FirebaseCallback<java.util.List<Coupon>>() {
            @Override public void onSuccess(java.util.List<Coupon> coupons) {
                llCouponList.removeAllViews();
                if (coupons == null || coupons.isEmpty()) {
                    TextView empty = new TextView(ProfileActivity.this);
                    empty.setText("Nenhum cupom cadastrado");
                    empty.setTextColor(0xFF888888);
                    empty.setTextSize(13f);
                    empty.setPadding(0, 0, 0, 8);
                    llCouponList.addView(empty);
                    return;
                }
                for (Coupon coupon : coupons) {
                    addCouponRow(llCouponList, coupon);
                }
            }
            @Override public void onFailure(String error) {}
        });
    }

    private void addCouponRow(LinearLayout parent, Coupon coupon) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, 0, 0, 8);
        row.setLayoutParams(rowParams);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        info.setLayoutParams(infoParams);

        TextView tvCode = new TextView(this);
        tvCode.setText(coupon.getCode());
        tvCode.setTextColor(0xFF222222);
        tvCode.setTextSize(14f);
        tvCode.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDetail = new TextView(this);
        tvDetail.setText(String.format("R$ %.2f  •  %d/%d usos",
                coupon.getValue(), coupon.getUsedCount(), coupon.getMaxUses()));
        tvDetail.setTextColor(0xFF888888);
        tvDetail.setTextSize(12f);

        info.addView(tvCode);
        info.addView(tvDetail);

        Button btnDelete = new Button(this);
        btnDelete.setText("Excluir");
        btnDelete.setTextSize(12f);
        btnDelete.setTextColor(0xFFCC2222);
        btnDelete.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFEBEE));
        btnDelete.setPadding(16, 4, 16, 4);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMarginStart(8);
        btnDelete.setLayoutParams(btnParams);

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Excluir Cupom")
                    .setMessage("Excluir o cupom \"" + coupon.getCode() + "\"?")
                    .setPositiveButton("Excluir", (d, w) ->
                            new CouponDAO().delete(coupon.getCode(), new FirebaseCallback<Void>() {
                                @Override public void onSuccess(Void ignored) {
                                    parent.removeView(row);
                                    Toast.makeText(ProfileActivity.this,
                                            "Cupom excluído", Toast.LENGTH_SHORT).show();
                                }
                                @Override public void onFailure(String error) {
                                    Toast.makeText(ProfileActivity.this,
                                            "Erro ao excluir", Toast.LENGTH_SHORT).show();
                                }
                            }))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        row.addView(info);
        row.addView(btnDelete);
        parent.addView(row);
    }

    private void setupBannerSection() {
        LinearLayout section = findViewById(R.id.sectionBanners);
        section.setVisibility(View.VISIBLE);

        LinearLayout rowBanners            = findViewById(R.id.rowBanners);
        MaterialCardView cardBannersContent = findViewById(R.id.cardBannersContent);
        ImageView ivBannersArrow           = findViewById(R.id.ivBannersArrow);
        rowBanners.setOnClickListener(v -> {
            toggleSection(cardBannersContent, ivBannersArrow);
            if (cardBannersContent.getVisibility() == View.VISIBLE) loadBannerList();
        });

        EditText etTag      = findViewById(R.id.etBannerTag);
        EditText etTitle    = findViewById(R.id.etBannerTitle);
        EditText etSubtitle = findViewById(R.id.etBannerSubtitle);
        EditText etImageUrl = findViewById(R.id.etBannerImageUrl);
        Button btnSelect    = findViewById(R.id.btnSelectBannerDishes);
        Button btnCreate    = findViewById(R.id.btnCreateBanner);

        new DishDAO().findAll(new FirebaseCallback<List<Dish>>() {
            @Override public void onSuccess(List<Dish> dishes) { allDishes = dishes != null ? dishes : new java.util.ArrayList<>(); }
            @Override public void onFailure(String e) {}
        });

        btnSelect.setOnClickListener(v -> showDishPickerDialog(btnSelect));

        btnCreate.setOnClickListener(v -> {
            String tag      = etTag.getText().toString().trim().toUpperCase();
            String title    = etTitle.getText().toString().trim();
            String subtitle = etSubtitle.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Informe o título do banner", Toast.LENGTH_SHORT).show();
                return;
            }

            Banner banner = new Banner(null, tag, title, subtitle, imageUrl, new java.util.ArrayList<>(selectedBannerDishIds));
            new BannerDAO().insert(banner, new FirebaseCallback<String>() {
                @Override public void onSuccess(String id) {
                    etTag.setText(""); etTitle.setText(""); etSubtitle.setText(""); etImageUrl.setText("");
                    selectedBannerDishIds.clear();
                    btnSelect.setText("Selecionar Produtos");
                    Toast.makeText(ProfileActivity.this, "Banner criado!", Toast.LENGTH_SHORT).show();
                    loadBannerList();
                }
                @Override public void onFailure(String error) {
                    Toast.makeText(ProfileActivity.this, "Erro ao criar banner", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showDishPickerDialog(Button btnSelect) {
        if (allDishes.isEmpty()) {
            Toast.makeText(this, "Nenhum produto cadastrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] names = new String[allDishes.size()];
        boolean[] checked = new boolean[allDishes.size()];
        for (int i = 0; i < allDishes.size(); i++) {
            names[i] = allDishes.get(i).getName();
            checked[i] = selectedBannerDishIds.contains(allDishes.get(i).getId());
        }

        new AlertDialog.Builder(this)
                .setTitle("Selecionar Produtos")
                .setMultiChoiceItems(names, checked, (dialog, which, isChecked) -> {
                    String dishId = allDishes.get(which).getId();
                    if (isChecked) { if (!selectedBannerDishIds.contains(dishId)) selectedBannerDishIds.add(dishId); }
                    else selectedBannerDishIds.remove(dishId);
                })
                .setPositiveButton("Confirmar", (d, w) ->
                        btnSelect.setText(selectedBannerDishIds.isEmpty()
                                ? "Selecionar Produtos"
                                : selectedBannerDishIds.size() + " produto(s) selecionado(s)"))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void loadBannerList() {
        LinearLayout llBannerList = findViewById(R.id.llBannerList);
        llBannerList.removeAllViews();

        new BannerDAO().findAll(new FirebaseCallback<List<Banner>>() {
            @Override public void onSuccess(List<Banner> banners) {
                llBannerList.removeAllViews();
                if (banners == null || banners.isEmpty()) {
                    TextView empty = new TextView(ProfileActivity.this);
                    empty.setText("Nenhum banner cadastrado");
                    empty.setTextColor(0xFF888888);
                    empty.setTextSize(13f);
                    empty.setPadding(0, 0, 0, 8);
                    llBannerList.addView(empty);
                    return;
                }
                for (Banner banner : banners) addBannerRow(llBannerList, banner);
            }
            @Override public void onFailure(String error) {}
        });
    }

    private void addBannerRow(LinearLayout parent, Banner banner) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, 0, 0, 8);
        row.setLayoutParams(rowParams);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvTitle = new TextView(this);
        tvTitle.setText(banner.getTitle());
        tvTitle.setTextColor(0xFF222222);
        tvTitle.setTextSize(14f);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDetail = new TextView(this);
        tvDetail.setText((banner.getTag().isEmpty() ? "" : banner.getTag() + "  •  ")
                + banner.getDishIds().size() + " produto(s)");
        tvDetail.setTextColor(0xFF888888);
        tvDetail.setTextSize(12f);

        info.addView(tvTitle);
        info.addView(tvDetail);

        Button btnDelete = new Button(this);
        btnDelete.setText("Excluir");
        btnDelete.setTextSize(12f);
        btnDelete.setTextColor(0xFFCC2222);
        btnDelete.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFEBEE));
        btnDelete.setPadding(16, 4, 16, 4);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMarginStart(8);
        btnDelete.setLayoutParams(btnParams);

        btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Excluir Banner")
                        .setMessage("Excluir o banner \"" + banner.getTitle() + "\"?")
                        .setPositiveButton("Excluir", (d, w) ->
                                new BannerDAO().delete(banner.getId(), new FirebaseCallback<Void>() {
                                    @Override public void onSuccess(Void ignored) {
                                        parent.removeView(row);
                                        Toast.makeText(ProfileActivity.this, "Banner excluído", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override public void onFailure(String error) {
                                        Toast.makeText(ProfileActivity.this, "Erro ao excluir", Toast.LENGTH_SHORT).show();
                                    }
                                }))
                        .setNegativeButton("Cancelar", null)
                        .show());

        row.addView(info);
        row.addView(btnDelete);
        parent.addView(row);
    }

    private void toggleSection(MaterialCardView contentCard, ImageView arrow) {
        boolean expanding = contentCard.getVisibility() != View.VISIBLE;
        contentCard.setVisibility(expanding ? View.VISIBLE : View.GONE);
        arrow.setRotation(expanding ? 270f : 180f);
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
