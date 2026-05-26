package com.example.unieat.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.example.unieat.R;
import com.example.unieat.dao.DishDAO;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;
import com.example.unieat.presenter.kitchen.KitchenMenuPresenter;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class EditDishActivity extends BaseActivity
        implements KitchenMenuPresenter.KitchenView {

    private EditText editDishName, editDishDescription, editDishPrice, editImageUrl;
    private Spinner spinnerDishType;
    private SwitchCompat switchAvailable;
    private KitchenMenuPresenter presenter;
    private Dish currentDish;
    private String dishId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dish);

        presenter = new KitchenMenuPresenter(this, new DishDAO());
        dishId = getIntent().getStringExtra("dish_id");

        bindViews();
        setupSpinner();
        setupListeners();

        if (dishId != null) {
            loadDish();
        }
    }

    private void bindViews() {
        editDishName = findViewById(R.id.editDishName);
        editDishDescription = findViewById(R.id.editDishDescription);
        editDishPrice = findViewById(R.id.editDishPrice);
        editImageUrl = findViewById(R.id.editImageUrl);
        spinnerDishType = findViewById(R.id.spinnerDishType);
        switchAvailable = findViewById(R.id.switchAvailable);
    }

    private void setupSpinner() {
        FoodType[] types = FoodType.values();
        ArrayAdapter<FoodType> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, types
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDishType.setAdapter(adapter);
    }

    private void loadDish() {
        new DishDAO().findById(dishId, new com.example.unieat.dao.FirebaseCallback<Dish>() {
            @Override public void onSuccess(Dish dish) {
                if (dish == null) return;
                currentDish = dish;
                editDishName.setText(dish.getName());
                editDishDescription.setText(dish.getDescription());
                editDishPrice.setText(String.format(Locale.US, "%.2f", dish.getPrice()).replace(".", ","));
                editImageUrl.setText(dish.getImageName() != null ? dish.getImageName() : "");
                switchAvailable.setChecked(dish.isAvailable());

                FoodType[] types = FoodType.values();
                for (int i = 0; i < types.length; i++) {
                    if (types[i] == dish.getType()) {
                        spinnerDishType.setSelection(i);
                        break;
                    }
                }
            }
            @Override public void onFailure(String error) {
                Toast.makeText(EditDishActivity.this, "Erro ao carregar prato", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> finish());

        ImageView btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            if (dishId != null) {
                presenter.deleteDish(dishId);
            } else {
                finish();
            }
        });

        MaterialButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveDish());
    }

    private void saveDish() {
        String name = editDishName.getText().toString().trim();
        String description = editDishDescription.getText().toString().trim();
        String priceStr = editDishPrice.getText().toString().trim().replace(",", ".");
        String imageUrl = editImageUrl.getText().toString().trim();
        FoodType type = (FoodType) spinnerDishType.getSelectedItem();
        boolean available = switchAvailable.isChecked();

        if (priceStr.isEmpty()) {
            Toast.makeText(this, "Informe o preço", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);

            Dish dish;
            if (currentDish != null) {
                dish = currentDish;
                dish.setName(name);
                dish.setDescription(description);
                dish.setPrice(price);
                dish.setType(type);
                dish.setImageName(imageUrl);
                dish.setAvailable(available);
            } else {
                dish = new Dish(null, name, description, price, type);
                dish.setImageName(imageUrl);
                dish.setAvailable(available);
            }

            presenter.saveDish(dish);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Preço inválido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showDishes(java.util.List<Dish> dishes) {}

    @Override
    public void showEmptyState(String message) {}

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSaveSuccess() {
        Toast.makeText(this, "Prato salvo com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void showDeleteSuccess() {
        Toast.makeText(this, "Prato removido!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void showValidationError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}