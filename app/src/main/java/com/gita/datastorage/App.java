package com.gita.datastorage;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by alex on 11/5/2016
 */

public class App extends Application {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Settings.initialize(this);
        String lang = Settings.getString("lang");
        if (!lang.isEmpty()) {
            changeLanguage(lang);
        }
    }

    public void changeLanguage(String lang) {
        Settings.saveString("lang", lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        Locale locale = new Locale(lang);
        if (Tools.atLeastJellyBeanMR1()) {
            conf.setLocale(locale);
        } else {
            conf.locale = locale;
        }
        if (Tools.atLeastNougatMR1()) {
            createConfigurationContext(conf);
        } else {
            res.updateConfiguration(conf, dm);
        }
    }
}
