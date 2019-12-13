package com.monri.android.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class TransactionParamsJsonSerializer extends JsonSerializer<TransactionParams> {

    public TransactionParamsJsonSerializer() {
    }

    @Override
    public void serialize(TransactionParams value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeObject(value.getData());
        }
    }
}
