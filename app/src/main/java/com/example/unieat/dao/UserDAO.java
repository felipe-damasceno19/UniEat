package com.example.unieat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.example.unieat.data.DatabaseHelper;
import com.example.unieat.enums.UserType;
import com.example.unieat.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {


    private DatabaseHelper dbHelper;

    public UserDAO(Context context){
        dbHelper = new DatabaseHelper(context);
    }

    public void insert(User user){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", user.getId());
        values.put("name", user.getName());
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("email", user.getEmail());
        values.put("balance", user.getBalance());
        values.put("user_type", user.getType().name());
        db.insert("user", null, values);
        db.close();
    }
    
    public List<User> findAll(){
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user", null);
        
        if(cursor.moveToFirst()){
            do{
                User user = new User(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("balance")),
                        UserType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("user_type")))
                );
                list.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
    
    public User findById(String id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query("user", null , "id = ?", new String[]{id}, null, null, null);
        
        if(cursor.moveToFirst()){
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("balance")),
                    UserType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("user_type")))
            );
        }
        cursor.close();
        db.close();
        return user;
    }
    
    public void update(User user){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("email", user.getEmail());
        values.put("balance", user.getBalance());
        values.put("user_type", user.getType().name());
        
        db.update("user", values, "id = ?", new String[]{user.getId()});
        db.close();
    }
    
    public void delete(String id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("user", "id = ?", new String[]{id});
        db.close();
    }
}
