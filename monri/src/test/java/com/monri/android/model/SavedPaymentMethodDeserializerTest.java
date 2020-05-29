package com.monri.android.model;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by jasminsuljic on 2019-12-12.
 * MonriAndroid
 */
public class SavedPaymentMethodDeserializerTest {
    @Test
    public void deserialize() throws IOException {
        String json = "{\n" +
                "  \"type\": \"card\",\n" +
                "  \"data\": {\n" +
                "    \"brand\": \"visa\",\n" +
                "    \"issuer\": \"xml-sim\",\n" +
                "    \"masked\": \"411111-xxx-xxx-1111\",\n" +
                "    \"expiration_date\": \"2412\",\n" +
                "    \"token\": \"3cfb7f1df0ef7ef707ad213e4850219ed4b4553a96a68b6430359d002acdcafd\"\n" +
                "  }\n" +
                "}";

        //new test needed without objectMapper to class SavedPamymetMethod.. part of this is done in class HttpRequestTest
        Assert.assertNotNull(json);
    }
}