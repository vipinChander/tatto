package com.example.tattomobile.utility;

import android.app.Application;

public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Prefs.init(getApplicationContext());

    }


}
