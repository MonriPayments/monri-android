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
import com.monri.android.model.CustomerPaymentMethodRequest;
import com.monri.android.model.CustomerPaymentMethodResponse;
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
import java.util.UUID;
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

    String accessToken = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzY29wZXMiOlsiY3VzdG9tZXJzIiwicGF5bWVudC1tZXRob2RzIl0sImV4cCI6MTY3MzAyMDI4MiwiaXNzIjoiaHR0cHM6Ly9tb25yaS5jb20iLCJzdWIiOiI2YTEzZDc5YmRlOGRhOTMyMGU4ODkyM2NiMzQ3MmZiNjM4NjE5Y2NiIn0.p9tiD8JZMwV2KRQJpKLj_I4m72-Sq5l1PMtDlOobEh0nnyQT3pM8vSIMBL-f4yBNPZb4Of7i_3VmaOXh9c94j51XHOoB0izWgwgCxgnZXjSqT95xacnpqe1MUDWtVXHxkVhUrECe1-zqeoNOixNTzqQHi20B2sEFw2H72VdagcL7y8zZWs843-n5OnHMWBQj1hbnPI3ueCkunormI47no69KWSQ08na3Zy-W9ialr7nN6MEIE-jhi_6PxAicwMbWZdtaISnh4ePN7d_Lu9eXlsCsDq7i_JYr5MZ-VL6HHIJ6SJurdH4JbEj0B1wC4re3xq5KdfuegBVoA50XXirMqw";

    private String createAccessToken() {
        return accessToken;
    }

    private Monri createMonriInstance() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        MonriApiOptions monriApiOptions = MonriApiOptions.create(getAuthenticityToken(), true);
        return new Monri(appContext, monriApiOptions);
    }

    private Card getNon3DSCard() {
        return new Card("4111 1111 1111 1111", 12, 2034, "123");
    }

    private ConfirmPaymentParams getConfirmPaymentParams(final String customerUuid, final String clientSecret, final Card card, final boolean shouldSaveCard) {
        final CustomerParams customerParams = new CustomerParams()
                .setCustomerUuid(customerUuid)
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

    private void createCustomer(final String accessToken, final ResultCallback<CustomerResponse> callback) throws InterruptedException, ExecutionException {
        createCustomer(accessToken, callback, null);
    }

    private void createCustomer(final String accessToken, final ResultCallback<CustomerResponse> callback, final String merchantCustomerId) throws InterruptedException, ExecutionException {
        Monri monri = createMonriInstance();
//        String tmpMerchantCustomerId = UUID.randomUUID().toString();

        final CustomerRequestBody customerRequestBody = new CustomerRequestBody()
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

        if (merchantCustomerId != null) {
            customerRequestBody.setMerchantCustomerId(merchantCustomerId);
        }

        CustomerCreateRequest customerCreateRequest = new CustomerCreateRequest(
                customerRequestBody,
                accessToken
        );

        monri.getMonriApi().createCustomer(customerCreateRequest, callback);
    }

    private void createCustomer(Consumer<CustomerResponse> customerConsumer) {
        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        try {
                            createCustomer(accessToken, new ResultCallback<CustomerResponse>() {
                                @Override
                                public void onSuccess(final CustomerResponse result) {
                                    customerConsumer.accept(result);
                                }

                                @Override
                                public void onError(final Throwable throwable) {
                                    Assert.fail("create customer has not been created: " + throwable.getMessage());
                                }
                            });
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            Assert.fail("createCustomer failed:" + e.getMessage());
                        }

                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Assert.fail("createCustomer failed: " + throwable.getMessage());
                    }
                }
        );
    }

    @Test
    public void testCreateCustomer() throws InterruptedException {
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

    @Test
    public void testUpdateCustomer() throws InterruptedException {
        Monri monri = createMonriInstance();
        CountDownLatch signal = new CountDownLatch(2);
        CustomerRequestBody customerRequestBody = new CustomerRequestBody();
        final String newName = "Adnan Omerovic";
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
                                    signal.countDown();
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
                            signal.countDown();
                        }
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Assert.fail("client secret failed");
                        signal.countDown();
                    }
                }
        );

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));
    }

    @Test
    public void testRetrieveCustomer() throws InterruptedException {
        Monri monri = createMonriInstance();
        CountDownLatch signal = new CountDownLatch(2);

        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        try {
                            createCustomer(accessToken, new ResultCallback<CustomerResponse>() {
                                @Override
                                public void onSuccess(final CustomerResponse createdCustomer) {
                                    signal.countDown();
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
                            signal.countDown();
                        }
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Assert.fail("client secret failed");
                        signal.countDown();
                    }
                }
        );

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));
    }

    @Test
    public void testRetrieveCustomerViaMerchantId() throws InterruptedException {
        Monri monri = createMonriInstance();
        CountDownLatch signal = new CountDownLatch(2);
        String merchantCustomerId = UUID.randomUUID().toString();

        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        try {
                            createCustomer(
                                    accessToken,
                                    new ResultCallback<CustomerResponse>() {
                                        @Override
                                        public void onSuccess(final CustomerResponse createdCustomer) {
                                            signal.countDown();
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
                                            Assert.fail("create customer failed: " + throwable.getMessage());
                                            signal.countDown();
                                        }
                                    },
                                    merchantCustomerId
                            );
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            Assert.fail("create customer failed: " + e.getMessage());
                            signal.countDown();
                        }
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Assert.fail("client secret failed");
                        signal.countDown();
                    }
                }
        );

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));
    }

    @Test
    public void testDeleteCustomer() throws InterruptedException {
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
                                    signal.countDown();
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
                            signal.countDown();
                        }
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Assert.fail("client secret failed");
                        signal.countDown();
                    }
                }
        );

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));

    }

    @Test
    public void testCustomerPayment() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(3);
        Monri monri = createMonriInstance();

        createCustomer((CustomerResponse customerResponse) -> {
            taskRunnerExecuteWithCallback(
                    this::createPaymentSession,
                    new ResultCallback<String>() {
                        @Override
                        public void onSuccess(final String clientSecretId) {
                            signal.countDown();
                            final ConfirmPaymentParams confirmPaymentParams = getConfirmPaymentParams(
                                    customerResponse.getUuid(),
                                    clientSecretId,
                                    getNon3DSCard(),
                                    true
                            );
                            monri.getMonriApi().confirmPayment(confirmPaymentParams, new ResultCallback<ConfirmPaymentResponse>() {
                                @Override
                                public void onSuccess(final ConfirmPaymentResponse confirmPaymentResponse) {
                                    Assert.assertEquals(PaymentStatus.APPROVED, confirmPaymentResponse.getStatus());
                                    signal.countDown();
                                    monri.getMonriApi().retrieveCustomerPaymentMethods(
                                            new CustomerPaymentMethodRequest(
                                                    customerResponse.getUuid(),
                                                    20,
                                                    0,
                                                    accessToken
                                            ),
                                            new ResultCallback<CustomerPaymentMethodResponse>() {
                                                @Override
                                                public void onSuccess(final CustomerPaymentMethodResponse result) {
                                                    Assert.assertEquals("approved", result.getStatus());
                                                    Assert.assertNotNull(result.getCustomerPaymentMethod());
                                                    Assert.assertEquals(customerResponse.getUuid(), result.getCustomerPaymentMethod().get(0).getCustomerUuid());
                                                    signal.countDown();
                                                }

                                                @Override
                                                public void onError(final Throwable throwable) {
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
                            signal.countDown();
                        }
                    }
            );
        });

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));
    }
}
