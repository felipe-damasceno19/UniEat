package com.example.unieat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.unieat.data.DatabaseHelper;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;
import com.example.unieat.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    private final DatabaseHelper dbHelper;

    public CartDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public void saveItem(OrderItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("item_id", item.getId());
        cv.put("dish_id", item.getDish().getId());
        cv.put("dish_name", item.getDish().getName());
        cv.put("dish_price", item.getDish().getPrice());
        cv.put("dish_description", item.getDish().getDescription());
        cv.put("dish_type", item.getDish().getType().name());
        cv.put("dish_image", item.getDish().getImageName());
        cv.put("quantity", item.getQuantity());
        db.insertWithOnConflict("cart", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void updateQuantity(String itemId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("quantity", quantity);
        db.update("cart", cv, "item_id = ?", new String[]{itemId});
        db.close();
    }

    public void removeItem(String itemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("cart", "item_id = ?", new String[]{itemId});
        db.close();
    }

    public void clearCart() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("cart", null, null);
        db.close();
    }

    public List<OrderItem> loadCart() {
        List<OrderItem> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("cart", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String itemId       = cursor.getString(cursor.getColumnIndexOrThrow("item_id"));
                String dishId       = cursor.getString(cursor.getColumnIndexOrThrow("dish_id"));
                String dishName     = cursor.getString(cursor.getColumnIndexOrThrow("dish_name"));
                double dishPrice    = cursor.getDouble(cursor.getColumnIndexOrThrow("dish_price"));
                String dishDesc     = cursor.getString(cursor.getColumnIndexOrThrow("dish_description"));
                String dishType     = cursor.getString(cursor.getColumnIndexOrThrow("dish_type"));
                String dishImage    = cursor.getString(cursor.getColumnIndexOrThrow("dish_image"));
                int quantity        = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

                Dish dish = new Dish(dishId, dishName, dishDesc, dishPrice, FoodType.valueOf(dishType));
                dish.setImageName(dishImage != null ? dishImage : "");

                items.add(new OrderItem(itemId, quantity, dish));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }
}
