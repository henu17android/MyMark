package com.example.mymark;

import android.app.Application;

public class MyApplication extends Application {

    public static Application appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;

    }
}
