package com.monri.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

class MetaUtility {
    private static final String PREFERENCE_META_LIBRARY_KEY = "com.monri.meta.library";
    private static final String LIBRARY = "Android-SDK";

    private MetaUtility() {
    }

    @NonNull
    static String getLibrary(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //noinspection ConstantConditions
        return sharedPreferences.getString(PREFERENCE_META_LIBRARY_KEY, LIBRARY);
    }

}