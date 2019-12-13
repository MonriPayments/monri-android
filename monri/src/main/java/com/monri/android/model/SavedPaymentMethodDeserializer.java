package com.monri.android.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

/**
 * Created by jasminsuljic on 2019-12-12.
 * MonriAndroid
 */
class SavedPaymentMethodDeserializer extends JsonDeserializer<SavedPaymentMethod> {

    public SavedPaymentMethodDeserializer() {
    }

    @Override
    public SavedPaymentMethod deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        final ObjectNode treeNode = p.getCodec().readTree(p);

        if (treeNode == null) {
            return null;
        }

        final TextNode typeNode = (TextNode) treeNode.get("type");

        if (!"card".equals(typeNode.asText())) {
            return null;
        }

        if (!treeNode.has("data")) {
            return null;
        }

        final ObjectNode dataNode = (ObjectNode) treeNode.get("data");

        final SavedCardPaymentMethod.Data data = new SavedCardPaymentMethod.Data(
                dataNode.get("brand").textValue(),
                dataNode.get("issuer").textValue(),
                dataNode.get("masked").textValue(),
                dataNode.get("expiration_date").textValue(),
                dataNode.get("token").textValue()
        );

        return new SavedCardPaymentMethod("card", data);
    }
}
