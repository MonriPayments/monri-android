package com.monri.android;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.monri.android.activity.ConfirmPaymentActivity;
import com.monri.android.http.MonriHttpApi;
import com.monri.android.http.MonriHttpMethod;
import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.TransactionParams;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@LargeTest//https://testing.googleblog.com/2010/12/test-sizes.html
public class MonriHttpTest {

    private CountDownLatch signal;//for thread blocking until other threads complete the given task.
    private Context appContext;

    private ConfirmPaymentParams getConfirmPaymentParams(final String paymentId) {
        final CustomerParams customerParams = new CustomerParams()
                .setAddress("Adresa")
                .setFullName("Tester Testerovic")
                .setCity("Sarajevo")
                .setZip("71000")
                .setPhone("+38761000111")
                .setCountry("BA")
                .setEmail("tester+android_sdk@monri.com");

        ConfirmPaymentParams confirmPaymentParams = ConfirmPaymentParams.create(
                paymentId,
                new Card("4111 1111 1111 1111", 12, 2024, "123").toPaymentMethodParams(),
                TransactionParams.create()
                        .set("order_info", "Android SDK payment session")
                        .set(customerParams)
        );

        return confirmPaymentParams;
    }

    private ConfirmPaymentParams getConfirmPaymentParams() {
        return getConfirmPaymentParams("b4a3091dc077eada68bca86fbb5e2f524d5a889d");
    }

    private HttpURLConnection createHttpURLConnection(final String endpoint,
                                                      final MonriHttpMethod monriHttpMethod,
                                                      final Map<String, String> headers) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(monriHttpMethod.getValue());

        switch (monriHttpMethod) {
            case GET:
                break;
            case POST:
                urlConnection.setDoInput(true);//Allow Inputs
                urlConnection.setDoOutput(true);//Allow Outputs
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setUseCaches(false);//Don't use a cached Copy
                break;
            default:
        }

        for (String key : headers.keySet()) {
            urlConnection.setRequestProperty(key, headers.get(key));
        }

        return urlConnection;

    }

    private String getAuthenticityToken(){
        return "6a13d79bde8da9320e88923cb3472fb638619ccb";
    }

    private String getAuth() {
        return String.format("WP3-v2-Client %s", getAuthenticityToken());
    }

    private Map<String, String> getHttpHeaders() {
        return new HashMap<String, String>() {{
            put("Authorization", getAuth());
            put("Content-Type", "application/json; charset=UTF-8");
            put("Accept", "application/json");
        }};
    }

    @Test
    public void createPaymentTest() throws InterruptedException {

        signal = new CountDownLatch(3);

        AsyncTask<Void, Void, Void> createPaymentAsync = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... voids) {

                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("add_payment_method", false);

                    String baseUrl = "https://mobile.webteh.hr/";

                    final HttpURLConnection urlConnection =
                            createHttpURLConnection(
                                    baseUrl + "example/create-payment-session",
                                    MonriHttpMethod.POST,
                                    new HashMap<>()
                            );

                    OutputStreamWriter wr = null;

                    try {
                        wr = new OutputStreamWriter(urlConnection.getOutputStream());
                        wr.write(jsonObject.toString());
                        wr.flush();

                    } finally {
                        if (wr != null) {
                            wr.close();
                        }
                    }

                    //now read response
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader r = new BufferedReader(new InputStreamReader(in));
                        StringBuilder jsonStringResponse = new StringBuilder();
                        for (String line; (line = r.readLine()) != null; ) {
                            jsonStringResponse.append(line).append('\n');
                        }

                        JSONObject jsonResponse = new JSONObject(jsonStringResponse.toString());

                        Assert.assertNotNull(jsonResponse);

                        String clientSecret = "";

                        if (jsonResponse.has("client_secret")) {
                            clientSecret = jsonResponse.getString("client_secret");
                        }

                        confirmPaymentTest(clientSecret);

                    } finally {
                        urlConnection.disconnect();
                    }

                } catch (Exception e) {
                    System.out.println("Error while creating payment");
                }

                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {
                super.onPostExecute(aVoid);
                signal.countDown();
            }
        };

        createPaymentAsync.execute();

        Assert.assertTrue(signal.await(20, TimeUnit.SECONDS));

    }

    public void confirmPaymentTest(final String clientSecretId)  {

        AsyncTask<Void, Void, Void> confirmPaymentAsync = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... voids) {

                String baseUrl = MonriConfig.TEST_ENV_HOST;

                try {

                    final JSONObject confirmPaymentParamsJSON = MonriHttpApi.confirmPaymentParamsToJSON(getConfirmPaymentParams());

                    final HttpURLConnection httpURLConnection = createHttpURLConnection(
                            baseUrl + "/v2/payment/" + clientSecretId + "/confirm",
                            MonriHttpMethod.POST,
                            getHttpHeaders()
                    );

                    OutputStreamWriter wr = null;

                    try {
                        wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                        wr.write(confirmPaymentParamsJSON.toString());
                        wr.flush();

                    } finally {
                        if (wr != null) {
                            wr.close();
                        }
                    }

                    //now read response
                    try {
                        InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                        BufferedReader r = new BufferedReader(new InputStreamReader(in));
                        StringBuilder jsonStringResponse = new StringBuilder();
                        for (String line; (line = r.readLine()) != null; ) {
                            jsonStringResponse.append(line).append('\n');
                        }

                        JSONObject jsonResponse = new JSONObject(jsonStringResponse.toString());

                        Assert.assertNotNull(jsonResponse);

                        paymentStatusTest(clientSecretId);

                    } finally {
                        httpURLConnection.disconnect();
                    }
                }catch (Exception e){
                    System.out.println("Confirm payment POST error");
                }

                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {
                super.onPostExecute(aVoid);
                signal.countDown();
            }
        };

        confirmPaymentAsync.execute();

    }


    public void paymentStatusTest(final String clientSecretId) {

        AsyncTask<Void, Void, Void> paymentStatusAsync = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... voids) {

                try {
                    String baseUrl = MonriConfig.TEST_ENV_HOST;

                    final HttpURLConnection urlConnection = createHttpURLConnection(
                            baseUrl + "/v2/payment/" + clientSecretId + "/status",
                            MonriHttpMethod.GET,
                            getHttpHeaders()
                    );

                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader r = new BufferedReader(new InputStreamReader(in));
                        StringBuilder jsonStringResponse = new StringBuilder();
                        for (String line; (line = r.readLine()) != null; ) {
                            jsonStringResponse.append(line).append('\n');
                        }

                        final JSONObject jsonResponse = new JSONObject(jsonStringResponse.toString());

                        Assert.assertNotNull(jsonResponse);


                    } finally {
                        urlConnection.disconnect();
                    }

                } catch (Exception e) {
                    System.out.println("Payment status GET error");
                }

                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {
                super.onPostExecute(aVoid);
                signal.countDown();
            }
        };

        paymentStatusAsync.execute();

    }

    @Test
    public void createPaymentSessionAndTestConfirmPaymentActivity() throws InterruptedException, ExecutionException {//run with debug and then normal run to avoid null pointer exception on callback in MonriHttpAsyncTask
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String clientSecretTMP = "";

        AsyncTask<Void, Void, String> createPaymentAsync = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(final Void... voids) {

                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("add_payment_method", false);

                    String baseUrl = "https://mobile.webteh.hr/";

                    final HttpURLConnection urlConnection =
                            createHttpURLConnection(
                                    baseUrl + "example/create-payment-session",
                                    MonriHttpMethod.POST,
                                    new HashMap<>()
                            );

                    OutputStreamWriter wr = null;

                    try {
                        wr = new OutputStreamWriter(urlConnection.getOutputStream());
                        wr.write(jsonObject.toString());
                        wr.flush();

                    } finally {
                        if (wr != null) {
                            wr.close();
                        }
                    }

                    //now read response
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader r = new BufferedReader(new InputStreamReader(in));
                        StringBuilder jsonStringResponse = new StringBuilder();
                        for (String line; (line = r.readLine()) != null; ) {
                            jsonStringResponse.append(line).append('\n');
                        }

                        JSONObject jsonResponse = new JSONObject(jsonStringResponse.toString());

                        Assert.assertNotNull(jsonResponse);

                        String clientSecret = "";

                        if (jsonResponse.has("client_secret")) {
                            clientSecret = jsonResponse.getString("client_secret");
                        }

                       return clientSecret;

                    } finally {
                        urlConnection.disconnect();
                    }

                } catch (Exception e) {
                    System.out.println("Error while creating payment");
                }

                return null;
            }

        };

        clientSecretTMP = createPaymentAsync.execute().get();

        //TODO first you have to create payment.. this code above
       testConfirmPaymentActivity(getConfirmPaymentParams(clientSecretTMP));

    }

    private void testConfirmPaymentActivity(final ConfirmPaymentParams confirmPaymentParams) throws InterruptedException, ExecutionException {

        final AtomicReference<ActivityScenario<Activity>> testConfirmPaymentsActivity = new AtomicReference<ActivityScenario<Activity>>();

        AsyncTask<Void,Void,Void> launchActivity = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... voids) {
                testConfirmPaymentsActivity.set(ActivityScenario.launch(
                        ConfirmPaymentActivity.createIntent(appContext, confirmPaymentParams, MonriApiOptions.create(getAuthenticityToken(), true))
                ));

                return null;
            }

        };

        launchActivity.execute().get();

        Assert.assertEquals(Activity.RESULT_OK,testConfirmPaymentsActivity.get().getResult().getResultCode());

        final Bundle bundle = testConfirmPaymentsActivity.get().getResult().getResultData().getExtras();
        bundle.setClassLoader(PaymentResult.class.getClassLoader());

        final PaymentResult paymentResult = bundle.getParcelable(PaymentResult.BUNDLE_NAME);
        Assert.assertNotNull(paymentResult);
        Assert.assertNotNull(paymentResult.getStatus());
        Assert.assertNotNull(paymentResult.getCurrency());
        Assert.assertNotNull(paymentResult.getAmount());
        Assert.assertNotNull(paymentResult.getOrderNumber());
        Assert.assertNotNull(paymentResult.getCreatedAt());

        testConfirmPaymentsActivity.get().close();

    }


}
