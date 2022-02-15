package com.monri.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

class MetaUtility {
    private static final String PREFERENCE_META_LIBRARY_KEY = "com.monri.meta.library";
    private static final String LIBRARY = "Android-SDK";

    public static final String INTEGRATION_TYPE_KEY = "integration_type";
    public static final String LIBRARY_KEY = "library";
    public static final String LIBRARY_VERSION_KEY = "library_version";

    public static final List<String> META_KEYS = Arrays.asList(
            INTEGRATION_TYPE_KEY,
            LIBRARY_KEY,
            LIBRARY_VERSION_KEY
    );

    private MetaUtility() {
    }

    @NonNull
    static String getLibrary(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //noinspection ConstantConditions
        return sharedPreferences.getString(PREFERENCE_META_LIBRARY_KEY, LIBRARY);
    }

}