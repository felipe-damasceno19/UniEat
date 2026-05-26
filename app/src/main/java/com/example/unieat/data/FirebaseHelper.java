package com.example.unieat.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private static FirebaseDatabase instance;
    private static DatabaseReference usersRef;

    public static FirebaseDatabase getInstance() {
        if(instance == null) {
            instance = FirebaseDatabase.getInstance();
            instance.setPersistenceEnabled(true);
        }
        return instance;
    }

    public static DatabaseReference users() {
        if (usersRef == null) {
            usersRef = getInstance().getReference("users");
            usersRef.keepSynced(true);
        }
        return usersRef;
    }
    public static DatabaseReference dishes()   { return getInstance().getReference("dishes"); }
    public static DatabaseReference orders()   { return getInstance().getReference("orders"); }
    public static DatabaseReference payments() { return getInstance().getReference("payments"); }
    public static DatabaseReference ratings()  { return getInstance().getReference("avaliations"); }
    public static DatabaseReference settings() { return getInstance().getReference("settings"); }
    public static DatabaseReference coupons()  { return getInstance().getReference("coupons"); }
}
