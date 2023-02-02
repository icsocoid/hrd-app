package com.sia.als.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.sia.als.activity.LoginActivity;
import com.sia.als.config.Config;

import java.util.HashMap;

public class SessionManagement {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Context context;

    int PRIVATE_MODE = 0;

    public SessionManagement(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(Config.PREFS_NAME, PRIVATE_MODE);
        editor = prefs.edit();

    }

    public void createLoginSession(String id, String email, String name,String isAdmin, String macAddress)
    {
        editor.putBoolean(Config.IS_LOGIN, true);
        editor.putString(Config.KEY_ID, id);
        editor.putString(Config.KEY_EMAIL, email);
        editor.putString(Config.KEY_NAME, name);
        editor.putString(Config.KEY_IS_ADMIN, isAdmin);
        editor.putString(Config.KEY_MAC, macAddress);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(Config.KEY_ID, prefs.getString(Config.KEY_ID, null));
        // user email id
        user.put(Config.KEY_EMAIL, prefs.getString(Config.KEY_EMAIL, null));
        // user name
        user.put(Config.KEY_NAME, prefs.getString(Config.KEY_NAME, null));
        user.put(Config.KEY_MAC, prefs.getString(Config.KEY_MAC, null));
        return user;
    }

    public void logoutSession() {
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Config.IS_LOGIN, false);
    }
    public boolean isAdmin()
    {
        if(prefs.getString(Config.KEY_IS_ADMIN, null).equals("YES"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
