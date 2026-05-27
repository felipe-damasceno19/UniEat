package com.example.unieat.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "unieat.db";
    private static final int VERSION = 4;

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE dish("+
                "id TEXT PRIMARY KEY,"+
                "name TEXT NOT NULL,"+
                "description TEXT,"+
                "price REAL NOT NULL,"+
                "food_type TEXT NOT NULL,"+
                "image_name TEXT,"+
                "available INTEGER)");

        db.execSQL("CREATE TABLE orders("+
                "id TEXT PRIMARY KEY,"+
                "annotation TEXT,"+
                "order_status TEXT,"+
                "time TEXT)"
        );

        db.execSQL("CREATE TABLE order_items("+
                "id TEXT PRIMARY KEY,"+
                "quantity INTEGER NOT NULL,"+
                "dish_id TEXT,"+
                "order_id TEXT,"+
                "FOREIGN KEY(dish_id) REFERENCES dish(id),"+
                "FOREIGN KEY(order_id) REFERENCES orders(id))"
        );

        db.execSQL("CREATE TABLE user("+
                "id TEXT PRIMARY KEY,"+
                "name TEXT NOT NULL,"+
                "username TEXT NOT NULL,"+
                "password TEXT NOT NULL,"+
                "email TEXT NOT NULL,"+
                "balance REAL,"+
                "user_type TEXT)"
        );

        db.execSQL("CREATE TABLE avaliation("+
                "id TEXT PRIMARY KEY,"+
                "dish_id TEXT,"+
                "rating INTEGER,"+
                "comment TEXT,"+
                "FOREIGN KEY(dish_id) REFERENCES dish(id))"
        );

        db.execSQL("CREATE TABLE payment("+
                "id TEXT PRIMARY KEY,"+
                "order_id TEXT,"+
                "method TEXT,"+
                "amount REAL,"+
                "time TEXT,"+
                "FOREIGN KEY(order_id) REFERENCES orders(id))"
        );

        db.execSQL("CREATE TABLE cart("+
                "item_id TEXT PRIMARY KEY,"+
                "dish_id TEXT NOT NULL,"+
                "dish_name TEXT NOT NULL,"+
                "dish_price REAL NOT NULL,"+
                "dish_description TEXT,"+
                "dish_type TEXT,"+
                "dish_image TEXT,"+
                "quantity INTEGER NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS dish");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS avaliation");
        db.execSQL("DROP TABLE IF EXISTS payment");
        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }
}
