package com.example.unieat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.unieat.data.DatabaseHelper;
import com.example.unieat.enums.PaymentMethod;
import com.example.unieat.model.Payment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentDAO {

    private DatabaseHelper dbhelper;

    public PaymentDAO(Context context){
        dbhelper = new DatabaseHelper(context);
    }

    public void insert(Payment payment){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", payment.getId());
        values.put("order_id", payment.getOrderId());
        values.put("method", payment.getMethod().name());
        values.put("amount", payment.getAmount());
        values.put("time", payment.getTime().getTime());

        db.insert("payment", null, values);
        db.close();
    }

    public Payment findByOrderId(String id) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Payment payment = null;
        Cursor cursor = db.query("payment", null, "order_id = ?", new String[]{id}, null, null, null);

        if(cursor.moveToFirst()){
            payment = new Payment(
            cursor.getString(cursor.getColumnIndexOrThrow("id")),
            cursor.getString(cursor.getColumnIndexOrThrow("order_id")),
            PaymentMethod.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("method"))),
            cursor.getFloat(cursor.getColumnIndexOrThrow("amount")),
            new Date(cursor.getLong(cursor.getColumnIndexOrThrow("time")))
            );
        }
        cursor.close();
        db.close();
        return payment;
    }

    public List<Payment> findAll() {
        List<Payment> list = new ArrayList<>();
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM payment", null);

        if(cursor.moveToFirst()) {
            do {
                Payment payment = new Payment(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("order_id")),
                        PaymentMethod.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("method"))),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("amount")),
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow("time")))
                );
                list.add(payment);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}
