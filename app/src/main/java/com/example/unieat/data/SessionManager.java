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
    private static final String KEY_REGISTRATION_DATE = "registration_date";
    private static final String KEY_PROFILE_PICTURE = "profile_picture";
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
        if (!prefs.contains(KEY_REGISTRATION_DATE)) {
            editor.putString(KEY_REGISTRATION_DATE, buildCurrentMonthYear());
        }
        editor.apply();
    }

    public void clearSession() {
        String savedDate = prefs.getString(KEY_REGISTRATION_DATE, null);
        editor.clear();
        if (savedDate != null) {
            editor.putString(KEY_REGISTRATION_DATE, savedDate);
        }
        editor.apply();
    }

    private String buildCurrentMonthYear() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String[] months = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        return months[cal.get(java.util.Calendar.MONTH)] + " " + cal.get(java.util.Calendar.YEAR);
    }

    public boolean isLoggedIn()   { return prefs.getBoolean(KEY_IS_LOGGED, false); }
    public boolean isStudent()    { return UserType.ALUNO.name().equals(getUserType().name()); }
    public boolean isKitchen()    { return UserType.COZINHEIRO.name().equals(getUserType().name()); }


    public String getId()         { return prefs.getString(KEY_ID, ""); }
    public String getName()       { return prefs.getString(KEY_NAME, ""); }
    public String getUsername()   { return prefs.getString(KEY_USERNAME, ""); }
    public double getBalance()    { return prefs.getFloat(KEY_BALANCE, 0f); }
    public UserType getUserType() { return UserType.valueOf(prefs.getString(KEY_USER_TYPE, UserType.ALUNO.name())); }

    public String getRegistrationDate() { return prefs.getString(KEY_REGISTRATION_DATE, ""); }

    public int getProfilePicture() { return prefs.getInt(KEY_PROFILE_PICTURE, 1); }
    public void setProfilePicture(int index) {
        editor.putInt(KEY_PROFILE_PICTURE, index);
        editor.apply();
    }

    public void updateBalance(double newBalance) {
        editor.putFloat(KEY_BALANCE, (float) newBalance);
        editor.apply();
    }

    private static final String KEY_SERVICE_FEE            = "service_fee";
    private static final String KEY_SERVICE_FEE_IS_PERCENT = "service_fee_is_percent";

    public double getServiceFee()          { return prefs.getFloat(KEY_SERVICE_FEE, 10f); }
    public boolean isServiceFeePercent()   { return prefs.getBoolean(KEY_SERVICE_FEE_IS_PERCENT, true); }

    public void setServiceFee(double v) {
        editor.putFloat(KEY_SERVICE_FEE, (float) v);
        editor.apply();
    }

    public void setServiceFeeIsPercent(boolean v) {
        editor.putBoolean(KEY_SERVICE_FEE_IS_PERCENT, v);
        editor.apply();
    }

}
