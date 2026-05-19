package com.example.unieat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.unieat.data.DatabaseHelper;
import com.example.unieat.model.Rating;

import java.util.ArrayList;
import java.util.List;

public class RatingDAO {

    private DatabaseHelper dbHelper;

    public RatingDAO(Context context){
        dbHelper = new DatabaseHelper(context);
    }

    public void insert(Rating avaliation){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", avaliation.getId());
        values.put("dish_id", avaliation.getDishId());
        values.put("rating", avaliation.getRating());
        values.put("comment", avaliation.getComment());
        db.insert("avaliation", null, values);
        db.close();
    }

    public List<Rating> findAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Rating> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM avaliation", null);

        if(cursor.moveToFirst()){
            do{
                Rating avaliation = new Rating(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("dish_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("rating")),
                        cursor.getString(cursor.getColumnIndexOrThrow("comment"))
                );
                list.add(avaliation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Rating> findByDishId(String dishId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Rating> list = new ArrayList<>();
        Cursor cursor = db.query("avaliation", null, "dish_id = ?", new String[]{dishId}, null, null, null);

        if(cursor.moveToFirst()){
            do {
                Rating avaliation = new Rating(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("dish_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("rating")),
                        cursor.getString(cursor.getColumnIndexOrThrow("comment"))
                );
                list.add(avaliation);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void delete(String id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("avaliation", "id = ?", new String[]{id});
        db.close();
    }
}
