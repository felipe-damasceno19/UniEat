package com.example.unieat.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.unieat.enums.UserType;

public class SessionManager {

    private static final String PREF_NAME = "unieat_session";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_USER_TYPE= "user_type";
    private static final String KEY_IS_LOGGED= "is_logged";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context){
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String id, String name, String username
            , double balance, UserType userType) {
        editor.putBoolean(KEY_IS_LOGGED, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_USERNAME, username);
        editor.putFloat(KEY_BALANCE,(float) balance);
        editor.putString(KEY_USER_TYPE, userType.name());
        editor.apply();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn()   { return prefs.getBoolean(KEY_IS_LOGGED, false); }
    public boolean isStudent()    { return UserType.ALUNO.name().equals(getUserType().name()); }
    public boolean isKitchen()    { return UserType.COZINHEIRO.name().equals(getUserType().name()); }


    public String getId()         { return prefs.getString(KEY_ID, ""); }
    public String getName()       { return prefs.getString(KEY_NAME, ""); }
    public String getUsername()   { return prefs.getString(KEY_USERNAME, ""); }
    public double getBalance()    { return prefs.getFloat(KEY_BALANCE, 0f); }
    public UserType getUserType() { return UserType.valueOf(prefs.getString(KEY_USER_TYPE, UserType.ALUNO.name())); }

    public void updateBalance(double newBalance) {
        editor.putFloat(KEY_BALANCE, (float) newBalance);
        editor.apply();
    }

}
