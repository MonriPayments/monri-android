package com.monri.android;

import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.platform.app.InstrumentationRegistry;

import com.monri.android.activity.ConfirmPaymentActivity;
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
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MonriTest {

    @FunctionalInterface
    private interface Consumer<T> {
        void accept(T t);
    }

    private void taskRunnerExecute(@NonNull Runnable backgroundWork) {
        taskRunnerExecute(backgroundWork, null);
    }

    private void taskRunnerExecute(@NonNull Runnable backgroundWork, @Nullable Runnable uiWork) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            backgroundWork.run();

            if (uiWork != null) {
                handler.post(uiWork);
            }
        });
    }

    private <Result> void taskRunnerExecuteWithCallback(Callable<Result> callable, ResultCallback<Result> callback) {
        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(callable, callback);
    }

    private HttpURLConnection createHttpURLConnection(
            final String endpoint,
            final MonriHttpMethod monriHttpMethod,
            final Map<String, String> headers
    ) throws IOException {
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

    private String getAuthenticityToken() {
        return "6a13d79bde8da9320e88923cb3472fb638619ccb";
    }

    private Card get3DSCard() {
        return new Card("4341 7920 0000 0044", 12, 2034, "123");
    }

    private Card getNon3DSCard() {
        return new Card("4111 1111 1111 1111", 12, 2034, "123");
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

    private ConfirmPaymentParams getConfirmPaymentParams(final String clientSecret, final Card card, final boolean shouldSaveCard) {
        final CustomerParams customerParams = new CustomerParams()
                .setAddress("Adresa")
                .setFullName("Tester Testerovic")
                .setCity("Sarajevo")
                .setZip("71000")
                .setPhone("+38761000111")
                .setCountry("BA")
                .setEmail("monri-android-sdk-test@monri.com");

        card.setTokenizePan(shouldSaveCard);//save card for future payment

        ConfirmPaymentParams confirmPaymentParams = ConfirmPaymentParams.create(
                clientSecret,
                card.toPaymentMethodParams(),
                TransactionParams.create()
                        .set("order_info", "Android SDK payment session")
                        .set(customerParams)
        );

        return confirmPaymentParams;
    }

    private String createPaymentSession() {
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

            try (OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream())) {
                wr.write(jsonObject.toString());
                wr.flush();
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
            System.out.println("Error while creating payment session");
            return "Error while creating payment session";
        }
    }

    @Test
    public void createPaymentSessionAndTestConfirmPaymentActivityWith3DSAccept() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(2);

        taskRunnerExecuteWithCallback(
                this::createPaymentSession,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String clientSecretId) {
                        signal.countDown();
                        try {
                            testConfirmPaymentActivityWith3DSCardAccept(clientSecretId, signal);
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            Assert.fail("testConfirmPaymentActivity failed");
                        }

                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Assert.fail("client secret failed");
                    }
                }
        );

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));

    }

    @Test
    public void createPaymentSessionAndTestConfirmPaymentActivityWith3DSCancel() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(2);

        taskRunnerExecuteWithCallback(
                this::createPaymentSession,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String clientSecretId) {
                        signal.countDown();
                        try {
                            testConfirmPaymentActivityWith3DSCardCancel(clientSecretId, signal);
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            Assert.fail("testConfirmPaymentActivity failed");
                        }

                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Assert.fail("client secret failed");
                    }
                }
        );

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));

    }


    private void invokeConfirmPaymentActivity(final ConfirmPaymentParams confirmPaymentParams, boolean accept3DS, Consumer<Instrumentation.ActivityResult> activityResult) {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent confirmPaymentIntent = ConfirmPaymentActivity.createIntent(appContext, confirmPaymentParams, MonriApiOptions.create(getAuthenticityToken(), true));

        try (ActivityScenario<ConfirmPaymentActivity> scenario = ActivityScenario.launch(confirmPaymentIntent)) {
            onWebView()
                    .forceJavascriptEnabled()
                    .withElement(findElement(Locator.CSS_SELECTOR, accept3DS ? "input[value=\"Authenticate\"]" : "input[value=\"Cancel\"]"))
                    .perform(webClick());// Similar to perform(click())


            final Instrumentation.ActivityResult result = scenario.getResult();

            activityResult.accept(result);
        }
    }


    private void testConfirmPaymentActivityWith3DSCardAccept(final String clientSecretId, final CountDownLatch signal) throws InterruptedException, ExecutionException {
        ConfirmPaymentParams confirmPaymentParams = getConfirmPaymentParams(clientSecretId, get3DSCard(), true);
        taskRunnerExecute(
                () -> invokeConfirmPaymentActivity(confirmPaymentParams, true, (activityResult -> {
                            Assert.assertEquals(Activity.RESULT_OK, activityResult.getResultCode());

                            final Bundle bundle = activityResult.getResultData().getExtras();
                            bundle.setClassLoader(PaymentResult.class.getClassLoader());

                            final PaymentResult paymentResult = bundle.getParcelable(PaymentResult.BUNDLE_NAME);
                            Assert.assertNotNull(paymentResult);
                            Assert.assertNotNull(paymentResult.getPanToken());
                            Assert.assertEquals("approved", paymentResult.getStatus());
                            Assert.assertNotNull(paymentResult.getStatus());
                            Assert.assertNotNull(paymentResult.getCurrency());
                            Assert.assertNotNull(paymentResult.getAmount());
                            Assert.assertNotNull(paymentResult.getOrderNumber());
                            Assert.assertNotNull(paymentResult.getCreatedAt());

                            signal.countDown();
                        })
                )
        );

    }


    private void testConfirmPaymentActivityWith3DSCardCancel(final String clientSecretId, final CountDownLatch signal) throws InterruptedException, ExecutionException {
        ConfirmPaymentParams confirmPaymentParams = getConfirmPaymentParams(clientSecretId, get3DSCard(), true);
        taskRunnerExecute(
                () -> invokeConfirmPaymentActivity(confirmPaymentParams, false, (activityResult -> {
                            Assert.assertEquals(Activity.RESULT_OK, activityResult.getResultCode());

                            final Bundle bundle = activityResult.getResultData().getExtras();
                            bundle.setClassLoader(PaymentResult.class.getClassLoader());

                            final PaymentResult paymentResult = bundle.getParcelable(PaymentResult.BUNDLE_NAME);
                            Assert.assertNotNull(paymentResult);
                            Assert.assertEquals("null",paymentResult.getPanToken());
                            Assert.assertEquals("declined", paymentResult.getStatus());
                            Assert.assertNotNull(paymentResult.getStatus());
                            Assert.assertNotNull(paymentResult.getCurrency());
                            Assert.assertNotNull(paymentResult.getAmount());
                            Assert.assertNotNull(paymentResult.getOrderNumber());
                            Assert.assertNotNull(paymentResult.getCreatedAt());

                            signal.countDown();
                        })
                )
        );

    }

}
