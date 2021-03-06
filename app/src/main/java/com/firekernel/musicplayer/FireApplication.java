package com.firekernel.musicplayer;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import timber.log.Timber;

/**
 * Created by Ashish on 5/15/2017.
 * Main Multidex application
 */

public class FireApplication extends Application {
    public static Context appContext;

    public static Context getInstance() {
        return appContext;
    }

    @Override
    public void onCreate() {
        //FireLog.d(TAG, "(++) onCreate");
        super.onCreate();
        appContext = getApplicationContext();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
