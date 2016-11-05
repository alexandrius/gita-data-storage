package com.gita.datastorage;

import android.os.Build;

/**
 * Created by alex on 11/5/2016
 */

public class Tools {
    public static int getAndroidVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static boolean atLeastMarshmallow() {
        return getAndroidVersion() >= Build.VERSION_CODES.M;
    }

    public static boolean atLeastKitKat() {
        return getAndroidVersion() >= Build.VERSION_CODES.M;
    }

    public static boolean atLeastJellyBeanMR1() {
        return getAndroidVersion() >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean atLeastNougatMR1() {
        return getAndroidVersion() >= Build.VERSION_CODES.N_MR1;
    }
}
