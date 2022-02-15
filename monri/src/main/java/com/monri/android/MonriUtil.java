package com.monri.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

public class MonriUtil {
    private MonriUtil() {
    }

    public static String library(Context context) {
        return MetaUtility.getLibrary(context);
    }

}