package com.monri.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

class MetaUtility {
    private static final String PREFERENCE_META_KEY = "monri_cross_platform_meta_key";
    private static final String INTEGRATION_TYPE = "android-sdk";
    private static final String LIBRARY = "Android-SDK";

    private MetaUtility() {
    }

    @Nullable
    public static String readMetaData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(PREFERENCE_META_KEY, null);
    }

    public static void updateMetaData(Context context) {
        String metaInfo = readMetaData(context);

        if (metaInfo == null) {
            writeMetaData(context, LIBRARY);
        } else {
            try {
                JSONObject meta = new JSONObject(metaInfo);
                String library = meta.has("library") ? meta.getString("library") : LIBRARY;
                writeMetaData(context, library);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private static void writeMetaData(Context context, String library) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("integration_type", INTEGRATION_TYPE);
            jsonObject.put("library_version", BuildConfig.MONRI_SDK_VERSION);
            jsonObject.put("library", library);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sharedPreferences.edit().putString(PREFERENCE_META_KEY, jsonObject.toString()).apply();
    }

}