package com.gita.datastorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by alex on 11/5/2016
 */

public class Settings {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;



    public static void initialize(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }


    public static void saveString(String key, String value) {
        editor.putString(key, value);
        commit();
    }

    public static void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        commit();
    }

    private static void commit() {
        editor.commit();
    }

    public static String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public static boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}
