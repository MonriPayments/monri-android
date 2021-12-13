package com.monri.android.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class TransactionParamsJsonSerializer extends JsonSerializer<TransactionParams> {

    public TransactionParamsJsonSerializer() {
    }

    @Override
    public void serialize(TransactionParams value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeObject(transformParams(value.getData()));
        }
    }

    private Map<String, Object> transformParams(Map<String, String> transactionParamsData) {
        Map<String, Object> map = new HashMap<>(transactionParamsData);
        String metaAsString = transactionParamsData.get("meta");

        try {
            JSONObject meta = new JSONObject(metaAsString);
            map.put("meta", Objects.requireNonNull(MonriJsonUtils.jsonObjectToMap(meta)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map;
    }
}
