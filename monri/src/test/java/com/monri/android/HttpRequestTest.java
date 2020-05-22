package com.monri.android;

import com.monri.android.http.MonriHttpApi;
import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.PaymentMethodParams;
import com.monri.android.model.TransactionParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class HttpRequestTest{
    //need     testImplementation 'androidx.test:core:1.2.0'
   // private Context context = ApplicationProvider.getApplicationContext();

    private String requestConfirmPaymentJSONString = "{\"payment_method\":{\"type\":\"card\",\"data\":{\"cvv\":\"123\",\"pan\":\"4111111111111111\",\"expiration_date\":\"2112\",\"tokenize_pan\":\"false\"}},\"transaction\":{\"ch_phone\":\"+38761000111\",\"ch_address\":\"Adresa\",\"ch_zip\":\"71000\",\"ch_full_name\":\"Tester Testerovic\",\"ch_country\":\"BA\",\"ch_email\":\"tester+android_sdk@monri.com\",\"order_info\":\"Android SDK payment session\",\"ch_city\":\"Sarajevo\"}}";
    private String responseConfirmPaymentJSONString = "{\"status\":\"approved\",\"client_secret\":\"3e1801b7fdbfc1689c9c1ccdf4da62b99a110b58\",\"payment_result\":{\"order_number\":\"hlUC30Gyo98ISbkWWHxeu9nMrzn5UCYT9GazZPp7\",\"amount\":100,\"currency\":\"HRK\",\"outgoing_amount\":100,\"outgoing_currency\":\"HRK\",\"transaction_type\":\"authorize\",\"created_at\":\"2020-05-22T12:45:30.675+02:00\",\"response_code\":\"0000\",\"response_message\":\"approved\",\"pan_token\":\"null\",\"status\":\"approved\"}}";

    @Test
    public void httpRequestTest(){
        String baseUrl = MonriConfig.TEST_ENV_HOST;

        final String auth = String.format("WP3-v2-Client %s", "6a13d79bde8da9320e88923cb3472fb638619ccb");

    }

    @Test
    public void fromJSONToConfirmPaymentResponse(){
        try {
            final ConfirmPaymentResponse confirmPaymentResponse = MonriHttpApi.ConfirmPaymentResponseJSONToClass(new JSONObject(responseConfirmPaymentJSONString));
            Assert.assertNull(confirmPaymentResponse.getId());
            Assert.assertNotNull(confirmPaymentResponse.getPaymentResult());
            Assert.assertNotNull(confirmPaymentResponse.getStatus());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void fromConfirmPaymentParamsToJSON(){
        final CustomerParams customerParams = new CustomerParams()
                .setAddress("Adresa")
                .setFullName("Tester Testerovic")
                .setCity("Sarajevo")
                .setZip("71000")
                .setPhone("+38761000111")
                .setCountry("BA")
                .setEmail("tester+android_sdk@monri.com");

        ConfirmPaymentParams confirmPaymentParams = ConfirmPaymentParams.create(
                "b4a3091dc077eada68bca86fbb5e2f524d5a889d",
                new Card("4111 1111 1111 1111", 12, 2024, "123").toPaymentMethodParams(),
                TransactionParams.create()
                        .set("order_info", "Android SDK payment session")
                        .set(customerParams)
                );

        try {
            final JSONObject jsonObject = MonriHttpApi.confirmPaymentParamsToJSON(confirmPaymentParams);
            Assert.assertTrue(jsonObject.has("payment_method"));
            Assert.assertFalse(jsonObject.has("_payment_method"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
