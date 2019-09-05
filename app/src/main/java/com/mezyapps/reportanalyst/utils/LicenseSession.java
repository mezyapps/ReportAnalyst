package com.mezyapps.reportanalyst.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mezyapps.reportanalyst.view.activity.LincenseActivity;

import java.util.HashMap;

public class LicenseSession {

    private SharedPreferences pref;
    private Editor editor;
    private Context mContext;
    private int PRIVATE_MODE = 0;
    private static final String NAME = "License";
    private static final String LOGIN = "IsLoggedIn";
    public static final String NAM = "name";
    public static final String password = "pasword";


    public LicenseSession(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLicenseSession(String name, String pasword) {
        editor.putBoolean(LOGIN, true);
        editor.putString(NAM, name);
        editor.putString(password, pasword);
        editor.commit();
    }

    public void checkLogin() {
        if (!this.isLicenseedIn()) {
            Intent i = new Intent(mContext, LincenseActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        }

    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(NAM, pref.getString(NAM, null));
        user.put(password, pref.getString(password, null));
        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(mContext, LincenseActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }
    public boolean isLicenseedIn() {
        return pref.getBoolean(LOGIN, false);
    }
}