package com.sia.als.mail;

import android.content.Context;

import androidx.multidex.MultiDex;

import com.orm.SugarApp;
import com.sia.als.mail.network.AnalyticsAPI;

/**
 * Created by rish on 21/6/16.
 */

public class ALSmailApplication extends SugarApp {
    @Override
    public void onCreate() {
        super.onCreate();
        AnalyticsAPI.setupAnalyticsAPI(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}