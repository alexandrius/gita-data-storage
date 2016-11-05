package com.gita.datastorage;

import android.app.Application;

/**
 * Created by alex on 11/5/2016
 */

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Settings.initialize(this);
    }
}
