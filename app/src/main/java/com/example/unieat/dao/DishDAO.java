package com.example.unieat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.unieat.data.DatabaseHelper;
import com.example.unieat.enums.FoodType;
import com.example.unieat.model.Dish;

import java.util.ArrayList;
import java.util.List;

public class DishDAO {

    private final DatabaseHelper dbHelper;

    public DishDAO(Context context){
        dbHelper = new DatabaseHelper(context);
    }

    public void insert(Dish dish){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", dish.getId());
        values.put("name", dish.getName());
        values.put("description", dish.getDescription());
        values.put("price", dish.getPrice());
        values.put("food_type", dish.getType().name());
        values.put("image_name", dish.getImageName());
        values.put("available", dish.isAvailable() ? 1 : 0);
        db.insert("dish", null, values);
        db.close();
    }

    public List<Dish> findAll(){
        List<Dish> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM dish", null);

        if(cursor.moveToFirst()){
            do{
                Dish d = new Dish(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        FoodType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("food_type")))
                );
                d.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow("available")) == 1);
                d.setImageName(cursor.getString(cursor.getColumnIndexOrThrow("image_name")));
                list.add(d);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }
    
    public List<Dish> getAvailableDishes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Dish> list = new ArrayList<>();
        Cursor cursor = db.query("dish", null, "available = ?", new String[]{"1"}, null, null, null);
        
        if(cursor.moveToFirst()) {
            do{
                Dish d = new Dish(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        FoodType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("food_type")))
                );
                d.setImageName(cursor.getString(cursor.getColumnIndexOrThrow("image_name")));
                d.setAvailable(true);
                list.add(d);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Dish> getDishesByType(FoodType type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Dish> list = new ArrayList<>();
        Cursor cursor = db.query("dish", null, "available = 1 AND food_type = ?", new String[]{type.name()}, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                Dish d = new Dish(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        FoodType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("food_type")))
                );
                d.setImageName(cursor.getString(cursor.getColumnIndexOrThrow("image_name")));
                d.setAvailable(true);
                list.add(d);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public Dish findById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Dish dish = null;
        Cursor cursor = db.query("dish", null, "id = ?", new String[]{id}, null, null, null);

        if (cursor.moveToFirst()) {
            dish = new Dish(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                    FoodType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("food_type")))
            );
            dish.setImageName(cursor.getString(cursor.getColumnIndexOrThrow("image_name")));
            dish.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow("available")) == 1);
        }

        cursor.close();
        db.close();
        return dish;
    }

    public void update(Dish dish) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", dish.getName());
        values.put("description", dish.getDescription());
        values.put("price", dish.getPrice());
        values.put("food_type", dish.getType().name());
        values.put("available", dish.isAvailable());
        values.put("image_name", dish.getImageName());

        db.update("dish", values, "id = ?", new String[]{dish.getId()});
        db.close();
    }

    public void updateAvailability(String id, boolean available){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("available", available ? 1 : 0);
        db.update("dish", values, "id = ?", new String[]{id});
        db.close();
    }

    public void delete(String id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("dish", "id = ?", new String[]{id});
        db.close();
    }

    public List<Dish> searchByName(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Dish> list = new ArrayList<>();
        Cursor cursor = db.query("dish", null, "name LIKE ?", new String[]{"%" + name + "%"}, null, null, null);

    if(cursor.moveToFirst()) {
        do {
            Dish d = new Dish(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                    FoodType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("food_type")))
            );
            d.setImageName(cursor.getString(cursor.getColumnIndexOrThrow("image_name")));
            d.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow("available")) == 1);
            list.add(d);
        } while (cursor.moveToNext());
    }
    cursor.close();
    db.close();
    return list;
}
}
