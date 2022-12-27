package com.monri.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;

import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.CustomerCreateRequest;
import com.monri.android.model.CustomerDeleteRequest;
import com.monri.android.model.CustomerDeleteResponse;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.CustomerRequestBody;
import com.monri.android.model.CustomerResponse;
import com.monri.android.model.CustomerRetrieveMerchantIdRequest;
import com.monri.android.model.CustomerRetrieveRequest;
import com.monri.android.model.CustomerUpdateRequest;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentStatus;
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

public class MonriCustomerTest {
    private String getAuthenticityToken() {
        return "6a13d79bde8da9320e88923cb3472fb638619ccb";
    }

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

    String accessToken = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzY29wZXMiOlsiY3VzdG9tZXJzIiwicGF5bWVudC1tZXRob2RzIl0sImV4cCI6MTY3MjE1NTYyNCwiaXNzIjoiaHR0cHM6Ly9tb25yaS5jb20iLCJzdWIiOiI3ZGIxMWVhNWQ0YTFhZjMyNDIxYjU2NGM3OWI5NDZkMWVhZDNkYWYwIn0.euKiUs8T8lQCfWmLV-ZzUkuhS-4lCUzy4e2W8EfY2uBtMZiFAw6XDp--K9TFZeEnPf8wJrL76YD9KDWgGjHdVYyoDAjJlTRusWWCBsAs8feW5LdnV3oFA9yh1O3vRBOB1M0qV2vu2Pgy2nmQgUXf0p1CM5grq4EnLN2EwwI4Nacg3jxaIHz6PuAIsyZdSuHPfBuJIe7uzzbslBYUjRw_ZSTRRHZ1kV2jqMkjpYX3puOY8obSC5-qgbHSmzHkOZT2tWeCrdWRc5UqVZ2FOfKWyYszLypY8Ra7qt-XVsjhnn8ClOgPkGo9CCY1RygEQxDnAu6LtPxNbNmTAhfL9y1NqQ";
    String merchantCustomerId = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

    private String createAccessToken() {
        return accessToken;
    }

    private Monri createMonriInstance() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        MonriApiOptions monriApiOptions = MonriApiOptions.create(getAuthenticityToken(), true);
        return new Monri(appContext, monriApiOptions);

    }

    @Test
    public void createAccessTokenAndCreateCustomer() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(1);

        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        try {
                            createCustomer(accessToken, new ResultCallback<CustomerResponse>() {
                                @Override
                                public void onSuccess(final CustomerResponse result) {
                                    Assert.assertNotNull(result.getUuid());
                                    Assert.assertEquals("Adnan", result.getName());
                                    signal.countDown();
                                }

                                @Override
                                public void onError(final Throwable throwable) {
                                    Assert.fail("client secret failed");
                                    signal.countDown();
                                }
                            });
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

    private void createCustomer(final String accessToken, final ResultCallback<CustomerResponse> callback) throws InterruptedException, ExecutionException {
        Monri monri = createMonriInstance();

        final CustomerRequestBody customerRequestBody = new CustomerRequestBody()
                .setMerchantCustomerId(merchantCustomerId)
                .setDescription("description")
                .setEmail("adnan.omerovic@monri.com")
                .setName("Adnan")
                .setPhone("00387000111")
                .setMetadata(new HashMap<>() {{
                    put("a", "b");
                }})
                .setZipCode("71000")
                .setCity("Sarajevo")
                .setAddress("Džemala Bijedića 2")
                .setCountry("BA");

        CustomerCreateRequest customerCreateRequest = new CustomerCreateRequest(
                customerRequestBody,
                accessToken
        );

        monri.getMonriApi().createCustomer(customerCreateRequest, callback);
    }

    @Test
    public void createAccessTokenCreateCustomerUpdateCustomer() throws InterruptedException {
        Monri monri = createMonriInstance();
        CountDownLatch signal = new CountDownLatch(1);
        CustomerRequestBody customerRequestBody = new CustomerRequestBody();
        final String newName = "Jasmin Suljic";
        customerRequestBody.setName(newName);

        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        try {
                            createCustomer(accessToken, new ResultCallback<CustomerResponse>() {
                                @Override
                                public void onSuccess(final CustomerResponse createdCustomer) {
                                    monri.getMonriApi().updateCustomer(
                                            new CustomerUpdateRequest(
                                                    customerRequestBody,
                                                    createdCustomer.getUuid(),
                                                    accessToken
                                            ),
                                            new ResultCallback<CustomerResponse>() {
                                                @Override
                                                public void onSuccess(final CustomerResponse updatedCustomer) {
                                                    Assert.assertEquals(newName, updatedCustomer.getName());
                                                    Assert.assertEquals(createdCustomer.getCity(), updatedCustomer.getCity());
                                                    signal.countDown();
                                                }

                                                @Override
                                                public void onError(final Throwable throwable) {
                                                    Assert.fail("update customer failed");
                                                    signal.countDown();
                                                }
                                            }
                                    );
                                }

                                @Override
                                public void onError(final Throwable throwable) {
                                    Assert.fail("create customer failed: " + throwable.getMessage());
                                    signal.countDown();
                                }
                            });
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            Assert.fail("create customer failed");
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
    public void createAccessTokenCreateCustomerRetrieveCustomer() throws InterruptedException {
        Monri monri = createMonriInstance();
        CountDownLatch signal = new CountDownLatch(1);

        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        try {
                            createCustomer(accessToken, new ResultCallback<CustomerResponse>() {
                                @Override
                                public void onSuccess(final CustomerResponse createdCustomer) {
                                    monri.getMonriApi().retrieveCustomer(
                                            new CustomerRetrieveRequest(
                                                    accessToken,
                                                    createdCustomer.getUuid()
                                            ),
                                            new ResultCallback<CustomerResponse>() {
                                                @Override
                                                public void onSuccess(final CustomerResponse customer) {
                                                    Assert.assertEquals(createdCustomer.getName(), customer.getName());
                                                    Assert.assertEquals(createdCustomer.getCity(), customer.getCity());
                                                    signal.countDown();
                                                }

                                                @Override
                                                public void onError(final Throwable throwable) {
                                                    Assert.fail("retrieve customer failed");
                                                    signal.countDown();
                                                }
                                            }
                                    );
                                }

                                @Override
                                public void onError(final Throwable throwable) {
                                    Assert.fail("create customer failed: " + throwable.getMessage());
                                    signal.countDown();
                                }
                            });
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            Assert.fail("create customer failed");
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
    public void createAccessTokenCreateCustomerRetrieveCustomerViaMerchantId() throws InterruptedException {
        Monri monri = createMonriInstance();
        CountDownLatch signal = new CountDownLatch(1);

        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        try {
                            createCustomer(accessToken, new ResultCallback<CustomerResponse>() {
                                @Override
                                public void onSuccess(final CustomerResponse createdCustomer) {
                                    monri.getMonriApi().retrieveCustomerViaMerchantCustomerId(
                                            new CustomerRetrieveMerchantIdRequest(
                                                    accessToken,
                                                    merchantCustomerId
                                            ),
                                            new ResultCallback<CustomerResponse>() {
                                                @Override
                                                public void onSuccess(final CustomerResponse customer) {
                                                    Assert.assertEquals(createdCustomer.getName(), customer.getName());
                                                    Assert.assertEquals(createdCustomer.getCity(), customer.getCity());
                                                    signal.countDown();
                                                }

                                                @Override
                                                public void onError(final Throwable throwable) {
                                                    Assert.fail("retrieve customer via merchant id failed");
                                                    signal.countDown();
                                                }
                                            }
                                    );
                                }

                                @Override
                                public void onError(final Throwable throwable) {
                                    Assert.fail("create customer failed");
                                    signal.countDown();
                                }
                            });
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            Assert.fail("create customer failed");
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
    public void createAccessTokenCreateCustomerDeleteCustomer() throws InterruptedException {
        Monri monri = createMonriInstance();
        CountDownLatch signal = new CountDownLatch(1);

        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        try {
                            createCustomer(accessToken, new ResultCallback<CustomerResponse>() {
                                @Override
                                public void onSuccess(final CustomerResponse createdCustomer) {
                                    monri.getMonriApi().deleteCustomer(
                                            new CustomerDeleteRequest(
                                                    accessToken,
                                                    createdCustomer.getUuid()
                                            ),
                                            new ResultCallback<CustomerDeleteResponse>() {
                                                @Override
                                                public void onSuccess(final CustomerDeleteResponse result) {
                                                    Assert.assertTrue(result.isDeleted());
                                                    Assert.assertEquals(createdCustomer.getUuid(), result.getUuid());
                                                    Assert.assertEquals("approved", result.getStatus());
                                                    signal.countDown();
                                                }

                                                @Override
                                                public void onError(final Throwable throwable) {
                                                    Assert.fail("delete customer failed");
                                                    signal.countDown();
                                                }
                                            }
                                    );
                                }

                                @Override
                                public void onError(final Throwable throwable) {
                                    Assert.fail("create customer failed: " + throwable.getMessage());
                                    signal.countDown();
                                }
                            });
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            Assert.fail("create customer failed");
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

    //todo I have to create transaction in order to test customer payment methods,
    //todo create a transaction and check if the customer is created....
    private Card getNon3DSCard() {
        return new Card("4111 1111 1111 1111", 12, 2034, "123");
    }

    private ConfirmPaymentParams getConfirmPaymentParams(final String clientSecret, final Card card, final boolean shouldSaveCard) {
        final CustomerParams customerParams = new CustomerParams()
                .setCustomerId(merchantCustomerId)
                .setAddress("Adresa")
                .setFullName("Adnan Omerovic")
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

    @Test
    public void createPaymentSessionConfirmPaymentCheckIsCustomerCreated() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(1);
        Monri monri = createMonriInstance();

        taskRunnerExecuteWithCallback(
                this::createPaymentSession,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String clientSecretId) {
                        final ConfirmPaymentParams confirmPaymentParams = getConfirmPaymentParams(
                                clientSecretId,
                                getNon3DSCard(),
                                true
                        );
                        monri.getMonriApi().confirmPayment(confirmPaymentParams, new ResultCallback<ConfirmPaymentResponse>() {
                            @Override
                            public void onSuccess(final ConfirmPaymentResponse confirmPaymentResponse) {
                                Assert.assertEquals(PaymentStatus.APPROVED, confirmPaymentResponse.getStatus());
                                monri.getMonriApi().retrieveCustomerViaMerchantCustomerId(
                                        new CustomerRetrieveMerchantIdRequest(accessToken, merchantCustomerId),
                                        new ResultCallback<CustomerResponse>() {
                                            @Override
                                            public void onSuccess(final CustomerResponse customerResponse) {
                                                Assert.assertEquals("Adnan Omerovic", customerResponse.getName());
                                                signal.countDown();
                                            }

                                            @Override
                                            public void onError(final Throwable throwable) {
                                                Assert.fail("Retrieve customer via merchant id failed");
                                                signal.countDown();
                                            }
                                        }
                                );
                            }

                            @Override
                            public void onError(final Throwable throwable) {
                                Assert.fail("confirmPaymentFailed");
                                signal.countDown();
                            }
                        });

                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Assert.fail("client secret failed");
                    }
                }
        );

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));
    }
}
