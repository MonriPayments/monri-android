package com.monri.android.json;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class JsonUtil {

    @Nullable
    public static <T> T get(Map<String, Object> json, String key) {
        if (!json.containsKey(key)) {
            return null;
        }

        return (T) json.get(key);
    }

    @Nullable
    public static <T> T get(Map<String, Object> json, String key, ConverterFunction<String, T> function) {

        if (!json.containsKey(key)) {
            return null;
        }

        return function.create((String) json.get(key));
    }

    @Nullable
    public static <T> T getJsonObject(Map<String, Object> json, String key, ConverterFunction<Map<String, Object>, T> function) {

        if (!json.containsKey(key)) {
            return null;
        }

        return function.create((Map<String, Object>) json.get(key));
    }

    public static void putIfNotNull(Map<String, String> target, String key, String value) {
        if (value != null) {
            target.put(key, value);
        }
    }

    public static List<String> toList(final JSONArray jsonArray) throws JSONException {
        final List<String> list = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }

        return list;
    }
}
