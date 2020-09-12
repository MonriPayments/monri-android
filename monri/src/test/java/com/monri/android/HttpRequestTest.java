package com.monri.android;

import com.monri.android.http.MonriHttpApiImpl;
import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.SavedCardPaymentMethod;
import com.monri.android.model.TransactionParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class HttpRequestTest {

    private String requestConfirmPaymentJSONString = "{\"payment_method\":{\"type\":\"card\",\"data\":{\"cvv\":\"123\",\"pan\":\"4111111111111111\",\"expiration_date\":\"2112\",\"tokenize_pan\":\"false\"}},\"transaction\":{\"ch_phone\":\"+38761000111\",\"ch_address\":\"Adresa\",\"ch_zip\":\"71000\",\"ch_full_name\":\"Tester Testerovic\",\"ch_country\":\"BA\",\"ch_email\":\"tester+android_sdk@monri.com\",\"order_info\":\"Android SDK payment session\",\"ch_city\":\"Sarajevo\"}}";
    private String responseConfirmPaymentJSONStringWithoutPmAndErrors = "{\"status\":\"approved\",\"client_secret\":\"3e1801b7fdbfc1689c9c1ccdf4da62b99a110b58\",\"payment_result\":{\"order_number\":\"hlUC30Gyo98ISbkWWHxeu9nMrzn5UCYT9GazZPp7\",\"amount\":100,\"currency\":\"HRK\",\"outgoing_amount\":100,\"outgoing_currency\":\"HRK\",\"transaction_type\":\"authorize\",\"created_at\":\"2020-05-22T12:45:30.675+02:00\",\"response_code\":\"0000\",\"response_message\":\"approved\",\"pan_token\":\"null\",\"status\":\"approved\"}}";
    private String responseConfirmPaymentJSONStringWithoutErrors = "{\"status\":\"approved\",\"client_secret\":\"3e1801b7fybfc1689c9c1ccda4da62b99a110b58\",\"payment_result\":{\"order_number\":\"hlUC30Gyo98ISbkWWHxeu9uMrzn5UCYT7GazZPp7\",\"amount\":100,\"currency\":\"HRK\",\"outgoing_amount\":100,\"outgoing_currency\":\"HRK\",\"payment_method\":{\"type\":\"card\",\"data\":{\"brand\":\"visa\",\"issuer\":\"ucb\",\"masked\":\"411111******1111\",\"expiration_date\":\"2112\",\"token\":\"5c1801b7fybfc1689c9c1ccda4da62b99a110bi7\"}},\"transaction_type\":\"authorize\",\"created_at\":\"2020-05-22T12:45:30.675+02:00\",\"response_code\":\"0000\",\"response_message\":\"approved\",\"pan_token\":\"null\",\"status\":\"approved\"}}";
    private String responseConfirmPaymentJSONString = "{\"status\":\"approved\",\"client_secret\":\"3e1801b7fybfc1689c9c1ccda4da62b99a110b58\",\"payment_result\":{\"order_number\":\"hlUC30Gyo98ISbkWWHxeu9uMrzn5UCYT7GazZPp7\",\"amount\":100,\"currency\":\"HRK\",\"outgoing_amount\":100,\"outgoing_currency\":\"HRK\",\"payment_method\":{\"type\":\"card\",\"data\":{\"brand\":\"visa\",\"issuer\":\"ucb\",\"masked\":\"411111******1111\",\"expiration_date\":\"2112\",\"token\":\"5c1801b7fybfc1689c9c1ccda4da62b99a110bi7\"}},\"transaction_type\":\"authorize\",\"created_at\":\"2020-05-22T12:45:30.675+02:00\",\"response_code\":\"0000\",\"response_message\":\"approved\",\"pan_token\":\"null\",\"errors\":[\"error1\",\"error2\",\"error3\"],\"status\":\"approved\"}}";

    private ConfirmPaymentParams getConfirmPaymentParams() {
        final CustomerParams customerParams = new CustomerParams()
                .setAddress("Adresa")
                .setFullName("Tester Testerovic")
                .setCity("Sarajevo")
                .setZip("71000")
                .setPhone("+38761000111")
                .setCountry("BA")
                .setEmail("tester+android_sdk@monri.com");

        return ConfirmPaymentParams.create(
                "b4a3091dc077eada68bca86fbb5e2f524d5a889d",
                new Card("4111 1111 1111 1111", 12, 2024, "123").toPaymentMethodParams(),
                TransactionParams.create()
                        .set("order_info", "Android SDK payment session")
                        .set(customerParams)
        );
    }


    @Test
    public void fromJSONToConfirmPaymentResponseWithoutPmAndErrors() throws JSONException {
        final ConfirmPaymentResponse confirmPaymentResponse = MonriHttpApiImpl.ConfirmPaymentResponseJSONToClass(new JSONObject(responseConfirmPaymentJSONStringWithoutPmAndErrors));
        assertNotNull(confirmPaymentResponse.getPaymentResult());
        assertNotNull(confirmPaymentResponse.getStatus());
        Assert.assertNull(confirmPaymentResponse.getActionRequired());

        final PaymentResult paymentResult = confirmPaymentResponse.getPaymentResult();
        assertNotNull(paymentResult.getCreatedAt());
        assertNotNull(paymentResult.getOrderNumber());
        assertNotNull(paymentResult.getAmount());
        assertNotNull(paymentResult.getCurrency());
        assertNotNull(paymentResult.getStatus());

        Assert.assertNull(paymentResult.getErrors());

        assertNotNull(paymentResult.getCreatedAt());
        assertNotNull(paymentResult.getPanToken());
        assertNotNull(paymentResult.getTransactionType());
        Assert.assertNull(paymentResult.getPaymentMethod());

    }

    @Test
    public void fromJSONToConfirmPaymentResponseWithoutErrors() throws JSONException {
        final ConfirmPaymentResponse confirmPaymentResponse = MonriHttpApiImpl.ConfirmPaymentResponseJSONToClass(new JSONObject(responseConfirmPaymentJSONStringWithoutErrors));
        assertNotNull(confirmPaymentResponse.getPaymentResult());
        assertNotNull(confirmPaymentResponse.getStatus());

        Assert.assertNull(confirmPaymentResponse.getActionRequired());

        final PaymentResult paymentResult = confirmPaymentResponse.getPaymentResult();
        assertNotNull(paymentResult.getCreatedAt());
        assertNotNull(paymentResult.getOrderNumber());
        assertNotNull(paymentResult.getAmount());
        assertNotNull(paymentResult.getCurrency());
        assertNotNull(paymentResult.getStatus());

        Assert.assertNull(paymentResult.getErrors());

        assertNotNull(paymentResult.getCreatedAt());
        assertNotNull(paymentResult.getPanToken());
        assertNotNull(paymentResult.getTransactionType());

        final SavedCardPaymentMethod paymentMethod = (SavedCardPaymentMethod) paymentResult.getPaymentMethod();
        assertNotNull(paymentMethod);
        final SavedCardPaymentMethod.Data data = paymentMethod.getData();
        assertNotNull(data);
        final String brand = data.getBrand();
        assertNotNull(brand);
        Assert.assertEquals("visa", brand);
    }

    @Test
    public void fromJSONToConfirmPaymentResponse() throws JSONException {
        final ConfirmPaymentResponse confirmPaymentResponse = MonriHttpApiImpl.ConfirmPaymentResponseJSONToClass(new JSONObject(responseConfirmPaymentJSONString));
        assertNotNull(confirmPaymentResponse.getPaymentResult());
        assertNotNull(confirmPaymentResponse.getStatus());

        Assert.assertNull(confirmPaymentResponse.getActionRequired());

        final PaymentResult paymentResult = confirmPaymentResponse.getPaymentResult();
        assertNotNull(paymentResult.getCreatedAt());
        assertNotNull(paymentResult.getOrderNumber());
        assertNotNull(paymentResult.getAmount());
        assertNotNull(paymentResult.getCurrency());
        assertNotNull(paymentResult.getStatus());

        assertNotNull(paymentResult.getErrors());

        Assert.assertEquals("error1", paymentResult.getErrors().get(0));

        assertNotNull(paymentResult.getCreatedAt());
        assertNotNull(paymentResult.getPanToken());
        assertNotNull(paymentResult.getTransactionType());

        final SavedCardPaymentMethod paymentMethod = (SavedCardPaymentMethod) paymentResult.getPaymentMethod();
        assertNotNull(paymentMethod);
        final SavedCardPaymentMethod.Data data = paymentMethod.getData();
        assertNotNull(data);
        final String brand = data.getBrand();
        assertNotNull(brand);
        Assert.assertEquals("visa", brand);
    }

    @Test
    public void fromConfirmPaymentParamsToJSON() throws JSONException {

        final ConfirmPaymentParams confirmPaymentParams = getConfirmPaymentParams();

        final JSONObject jsonObject = MonriHttpApiImpl.confirmPaymentParamsToJSON(confirmPaymentParams);
        Assert.assertTrue(jsonObject.has("payment_method"));
        Assert.assertFalse(jsonObject.has("_payment_method"));

    }

}
