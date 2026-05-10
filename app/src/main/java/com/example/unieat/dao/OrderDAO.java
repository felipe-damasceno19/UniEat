package com.example.unieat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.unieat.data.DatabaseHelper;
import com.example.unieat.enums.OrderStatus;
import com.example.unieat.model.Dish;
import com.example.unieat.model.Order;
import com.example.unieat.model.OrderItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDAO {

    private DatabaseHelper dbHelper;
    private DishDAO dishDAO;

    public OrderDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        dishDAO = new DishDAO(context);
    }

    public void insert(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("id", order.getId());
            values.put("annotation", order.getAnnotation());
            values.put("order_status", order.getStatus().name());
            values.put("time", order.getTime().getTime()); // Date → long
            db.insert("orders", null, values);

            for (OrderItem item : order.getItems()) {
                ContentValues itemValues = new ContentValues();
                itemValues.put("id", item.getId());
                itemValues.put("quantity", item.getQuantity());
                itemValues.put("dish_id", item.getDish().getId());
                itemValues.put("order_id", order.getId());
                db.insert("order_items", null, itemValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public Order findById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Order order = null;
        Cursor cursor = db.query("orders", null, "id = ?", new String[]{id}, null, null, null);

        if (cursor.moveToFirst()) {
            order = new Order(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    findItemsByOrderId(id, db),
                    OrderStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("order_status"))),
                    new Date(cursor.getLong(cursor.getColumnIndexOrThrow("time"))), 
                    cursor.getString(cursor.getColumnIndexOrThrow("annotation"))
            );
        }

        cursor.close();
        db.close();
        return order;
    }

    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM orders", null);

        if (cursor.moveToFirst()) {
            do {
                String orderId = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                Order order = new Order(
                        orderId,
                        findItemsByOrderId(orderId, db),
                        OrderStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("order_status"))),
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow("time"))),
                        cursor.getString(cursor.getColumnIndexOrThrow("annotation"))
                );
                list.add(order);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<Order> findByStatus(OrderStatus status) {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("orders", null, "order_status = ?",
                new String[]{status.name()}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String orderId = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                Order order = new Order(
                        orderId,
                        findItemsByOrderId(orderId, db),
                        OrderStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("order_status"))),
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow("time"))),
                        cursor.getString(cursor.getColumnIndexOrThrow("annotation"))
                );
                list.add(order);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public void updateStatus(String id, OrderStatus status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_status", status.name());
        db.update("orders", values, "id = ?", new String[]{id});
        db.close();
    }

    public void delete(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("order_items", "order_id = ?", new String[]{id});
            db.delete("orders", "id = ?", new String[]{id});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private List<OrderItem> findItemsByOrderId(String orderId, SQLiteDatabase db) {
        List<OrderItem> items = new ArrayList<>();
        Cursor cursor = db.query("order_items", null, "order_id = ?",
                new String[]{orderId}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Dish dish = dishDAO.findById(cursor.getString(cursor.getColumnIndexOrThrow("dish_id")));
                OrderItem item = new OrderItem(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                        dish
                );
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return items;
    }
}