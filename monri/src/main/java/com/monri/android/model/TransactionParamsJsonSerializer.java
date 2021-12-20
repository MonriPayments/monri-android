package com.monri.android.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
class TransactionParamsJsonSerializer extends JsonSerializer<TransactionParams> {

    private static final List<String> metaKeys = Arrays.asList(
            "integration_type",
            "library",
            "library_version"
    );

    public TransactionParamsJsonSerializer() {
    }

    @Override
    public void serialize(TransactionParams value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        if (value == null) {
            gen.writeNull();
        } else {
            try {
                gen.writeObject(transformParams(value.getData()));
            } catch (JSONException ignored) {

            }
        }
    }

    private Map<String, Object> transformParams(Map<String, String> data) throws JSONException {
        Map<String, Object> returnValue = new HashMap<>(data);
        JSONObject meta = new JSONObject();

        for (String metaKey : metaKeys) {
// integration_type
            // meta.integration_type
            String key = String.format("meta.%s", metaKey);
            if (data.containsKey(key)) {
                meta.put(metaKey, data.get(key));
                data.remove(key);
            }
        }

        if (meta.length() > 0) {
            returnValue.put("meta", Objects.requireNonNull(MonriJsonUtils.jsonObjectToMap(meta)));
        }

        return returnValue;
    }
}
