package com.example.unieat.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.unieat.R;
import com.example.unieat.enums.FoodType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EditDishActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dish);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        setupSpinner();
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinnerDishType);
        List<String> types = Arrays.stream(FoodType.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
