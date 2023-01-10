package com.monri.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;

import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.CustomerPaymentMethod;
import com.monri.android.model.MerchantCustomers;
import com.monri.android.model.CreateCustomerParams;
import com.monri.android.model.DeleteCustomerParams;
import com.monri.android.model.DeleteCustomerResponse;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.CustomerPaymentMethodParams;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.CustomerData;
import com.monri.android.model.Customer;
import com.monri.android.model.RetrieveCustomerViaMerchantCustomerUuidParams;
import com.monri.android.model.RetrieveCustomerParams;
import com.monri.android.model.UpdateCustomerParams;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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

    String accessToken = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzY29wZXMiOlsiY3VzdG9tZXJzIiwicGF5bWVudC1tZXRob2RzIl0sImV4cCI6MTY3MzM0NzY5MCwiaXNzIjoiaHR0cHM6Ly9tb25yaS5jb20iLCJzdWIiOiI2YTEzZDc5YmRlOGRhOTMyMGU4ODkyM2NiMzQ3MmZiNjM4NjE5Y2NiIn0.Vi-u-pELd9Py7Yzf3QK5PhfkDiHUgkJZa8i8vM2Kvn3y_qR2mORR_SS6vmFVp6QxtPcAIi8fsaQu4sOe06jlKPUlKHSYYMaMyTgaPj1K8Qwf-11HS5kzhdbif6nHYNRvY5wdrKZWnx72Wqtkvv463LPXqJBYANRulvFOvkFelYWrCxWBDaFMB3q2j92RDrg_UnEcZRFjsRo6LIaYkBDZxQPxRiO3tbVvL54mA_KunMyONTKp-suCusBQ5Q2Mtnqg-X9uI3IYWQhHc3npz6AVKHQ1JGRGY7_vgBMr-TT-LWgNGkKpvLrJRZKT8jUTx7KdB-2Xv97njjj2NVJF94FiCw";

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

    private CustomerData getCustomerData() {
        return new CustomerData()
                .setDescription("description: " + System.currentTimeMillis())
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
    }

    private void createCustomer(final String accessToken, final ResultCallback<Customer> callback) throws InterruptedException, ExecutionException {
        createCustomer(accessToken, callback, null);
    }

    private void createCustomer(final String accessToken, final ResultCallback<Customer> callback, final String merchantCustomerId) throws InterruptedException, ExecutionException {
        Monri monri = createMonriInstance();

        final CustomerData customerData = getCustomerData();

        if (merchantCustomerId != null) {
            customerData.setMerchantCustomerUuid(merchantCustomerId);
        }

        CreateCustomerParams createCustomerParams = new CreateCustomerParams(
                customerData,
                accessToken
        );

        monri.getMonriApi().createCustomer(createCustomerParams, callback);
    }

    private void createCustomer(Consumer<Customer> customerConsumer) {
        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        try {
                            createCustomer(accessToken, new ResultCallback<Customer>() {
                                @Override
                                public void onSuccess(final Customer result) {
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

    private void createCustomers(String accessToken, short size, Consumer<Pair<List<Customer>, String>> callback) {
        List<Customer> customerList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                createCustomer(accessToken, new ResultCallback<Customer>() {
                    @Override
                    public void onSuccess(final Customer createdCustomer) {
                        customerList.add(createdCustomer);
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        callback.accept(Pair.create(null, throwable.getMessage()));
                    }
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                callback.accept(Pair.create(null, e.getMessage()));
            }
        }

        callback.accept(Pair.create(customerList, null));
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
                            createCustomer(accessToken, new ResultCallback<Customer>() {
                                @Override
                                public void onSuccess(final Customer customer) {
                                    CustomerData customerData = getCustomerData();

                                    Assert.assertNotNull(customer.getUuid());
                                    Assert.assertEquals("approved", customer.getStatus());
                                    Assert.assertEquals("null", customer.getMerchantCustomerId());
                                    Assert.assertNotNull(customer.getDescription());
                                    Assert.assertEquals(customerData.getEmail(), customer.getEmail());
                                    Assert.assertEquals(customerData.getName(), customer.getName());
                                    Assert.assertEquals(customerData.getPhone(), customer.getPhone());
                                    Assert.assertEquals(customerData.getMetadata(), customer.getMetadata());
                                    Assert.assertEquals(customerData.getZipCode(), customer.getZipCode());
                                    Assert.assertEquals(customerData.getCity(), customer.getCity());
                                    Assert.assertEquals(customerData.getAddress(), customer.getAddress());
                                    Assert.assertEquals(customerData.getCountry(), customer.getCountry());
                                    Assert.assertFalse(customer.getDeleted());
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
    public void testUpdateCustomer() throws InterruptedException {
        Monri monri = createMonriInstance();
        CountDownLatch signal = new CountDownLatch(2);
        CustomerData customerData = new CustomerData();
        final String newName = "Adnan Omerovic";
        customerData.setName(newName);

        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        try {
                            createCustomer(accessToken, new ResultCallback<Customer>() {
                                @Override
                                public void onSuccess(final Customer createdCustomer) {
                                    signal.countDown();
                                    monri.getMonriApi().updateCustomer(
                                            new UpdateCustomerParams(
                                                    customerData,
                                                    createdCustomer.getUuid(),
                                                    accessToken
                                            ),
                                            new ResultCallback<Customer>() {
                                                @Override
                                                public void onSuccess(final Customer updatedCustomer) {
                                                    Assert.assertEquals(newName, updatedCustomer.getName());
                                                    Assert.assertNotNull(updatedCustomer.getUuid());
                                                    Assert.assertEquals("approved", updatedCustomer.getStatus());
                                                    Assert.assertEquals("null", updatedCustomer.getMerchantCustomerId());
                                                    Assert.assertNotNull(updatedCustomer.getDescription());
                                                    Assert.assertEquals(createdCustomer.getEmail(), updatedCustomer.getEmail());
                                                    Assert.assertEquals(createdCustomer.getPhone(), updatedCustomer.getPhone());
                                                    Assert.assertEquals(createdCustomer.getMetadata(), updatedCustomer.getMetadata());
                                                    Assert.assertEquals(createdCustomer.getZipCode(), updatedCustomer.getZipCode());
                                                    Assert.assertEquals(createdCustomer.getCity(), updatedCustomer.getCity());
                                                    Assert.assertEquals(createdCustomer.getAddress(), updatedCustomer.getAddress());
                                                    Assert.assertEquals(createdCustomer.getCountry(), updatedCustomer.getCountry());
                                                    Assert.assertFalse(updatedCustomer.getDeleted());
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
                            createCustomer(accessToken, new ResultCallback<Customer>() {
                                @Override
                                public void onSuccess(final Customer createdCustomer) {
                                    signal.countDown();
                                    monri.getMonriApi().retrieveCustomer(
                                            new RetrieveCustomerParams(
                                                    accessToken,
                                                    createdCustomer.getUuid()
                                            ),
                                            new ResultCallback<Customer>() {
                                                @Override
                                                public void onSuccess(final Customer customer) {
                                                    Assert.assertNotNull(customer.getUuid());
                                                    Assert.assertEquals("approved", customer.getStatus());
                                                    Assert.assertEquals("null", customer.getMerchantCustomerId());
                                                    Assert.assertNotNull(customer.getDescription());
                                                    Assert.assertEquals(createdCustomer.getEmail(), customer.getEmail());
                                                    Assert.assertEquals(createdCustomer.getName(), customer.getName());
                                                    Assert.assertEquals(createdCustomer.getPhone(), customer.getPhone());
                                                    Assert.assertEquals(createdCustomer.getMetadata(), customer.getMetadata());
                                                    Assert.assertEquals(createdCustomer.getZipCode(), customer.getZipCode());
                                                    Assert.assertEquals(createdCustomer.getCity(), customer.getCity());
                                                    Assert.assertEquals(createdCustomer.getAddress(), customer.getAddress());
                                                    Assert.assertEquals(createdCustomer.getCountry(), customer.getCountry());
                                                    Assert.assertFalse(customer.getDeleted());
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
    public void testRetrieveAllCustomers() throws InterruptedException {
        Monri monri = createMonriInstance();
        CountDownLatch signal = new CountDownLatch(1);

        final AtomicReference<List<Customer>> createdCustomersAtomicReference = new AtomicReference<>();
        final AtomicReference<List<Customer>> allCustomersAtomicReference = new AtomicReference<>();

        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        createCustomers(accessToken, (short) 3, (Pair<List<Customer>, String> listStringPair) -> {
                            final List<Customer> customerResponse = listStringPair.first;
                            if (customerResponse == null) {
                                Assert.fail(listStringPair.second);
                            }

                            createdCustomersAtomicReference.set(customerResponse);

                            monri.getMonriApi().retrieveAllCustomers(accessToken, new ResultCallback<MerchantCustomers>() {
                                @Override
                                public void onSuccess(final MerchantCustomers result) {
                                    allCustomersAtomicReference.set(result.getCustomerResponseList());
                                    signal.countDown();
                                }

                                @Override
                                public void onError(final Throwable throwable) {
                                    signal.countDown();
                                }
                            });
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Assert.fail("client secret failed");
                        signal.countDown();
                    }
                }
        );

        Assert.assertTrue(signal.await(10, TimeUnit.SECONDS));

        final List<Customer> createdCustomers = createdCustomersAtomicReference.get();
        final List<Customer> allCustomers = createdCustomersAtomicReference.get();

        for (int i = 0; i < createdCustomers.size(); i++) {
            Assert.assertEquals(createdCustomers.get(i).getDescription(), allCustomers.get(i).getDescription());
        }
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
                                    new ResultCallback<Customer>() {
                                        @Override
                                        public void onSuccess(final Customer createdCustomer) {
                                            signal.countDown();
                                            monri.getMonriApi().retrieveCustomerViaMerchantCustomerUuid(
                                                    new RetrieveCustomerViaMerchantCustomerUuidParams(
                                                            accessToken,
                                                            merchantCustomerId
                                                    ),
                                                    new ResultCallback<Customer>() {
                                                        @Override
                                                        public void onSuccess(final Customer customer) {
                                                            Assert.assertNotNull(customer.getUuid());
                                                            Assert.assertEquals("approved", customer.getStatus());
                                                            Assert.assertEquals(merchantCustomerId, customer.getMerchantCustomerId());
                                                            Assert.assertNotNull(customer.getDescription());
                                                            Assert.assertEquals(createdCustomer.getEmail(), customer.getEmail());
                                                            Assert.assertEquals(createdCustomer.getName(), customer.getName());
                                                            Assert.assertEquals(createdCustomer.getPhone(), customer.getPhone());
                                                            Assert.assertEquals(createdCustomer.getMetadata(), customer.getMetadata());
                                                            Assert.assertEquals(createdCustomer.getZipCode(), customer.getZipCode());
                                                            Assert.assertEquals(createdCustomer.getCity(), customer.getCity());
                                                            Assert.assertEquals(createdCustomer.getAddress(), customer.getAddress());
                                                            Assert.assertEquals(createdCustomer.getCountry(), customer.getCountry());
                                                            Assert.assertFalse(customer.getDeleted());
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
                            createCustomer(accessToken, new ResultCallback<Customer>() {
                                @Override
                                public void onSuccess(final Customer createdCustomer) {
                                    signal.countDown();
                                    monri.getMonriApi().deleteCustomer(
                                            new DeleteCustomerParams(
                                                    accessToken,
                                                    createdCustomer.getUuid()
                                            ),
                                            new ResultCallback<DeleteCustomerResponse>() {
                                                @Override
                                                public void onSuccess(final DeleteCustomerResponse result) {
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

        createCustomer((Customer customer) -> {
            taskRunnerExecuteWithCallback(
                    this::createPaymentSession,
                    new ResultCallback<String>() {
                        @Override
                        public void onSuccess(final String clientSecretId) {
                            signal.countDown();
                            final ConfirmPaymentParams confirmPaymentParams = getConfirmPaymentParams(
                                    customer.getUuid(),
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
                                            new CustomerPaymentMethodParams(
                                                    customer.getUuid(),
                                                    20,
                                                    0,
                                                    accessToken
                                            ),
                                            new ResultCallback<CustomerPaymentMethodResponse>() {
                                                @Override
                                                public void onSuccess(final CustomerPaymentMethodResponse result) {
                                                    Assert.assertEquals("approved", result.getStatus());
                                                    Assert.assertNotNull(result.getCustomerPaymentMethod());

                                                    CustomerPaymentMethod firstCustomerPaymentMethod = result.getCustomerPaymentMethod().get(0);
                                                    Assert.assertEquals(customer.getUuid(), firstCustomerPaymentMethod.getCustomerUuid());
                                                    Assert.assertEquals("411111******1111", firstCustomerPaymentMethod.getMaskedPan());
                                                    Assert.assertNotNull(firstCustomerPaymentMethod.getToken());
                                                    Assert.assertFalse(firstCustomerPaymentMethod.isExpired());
                                                    Assert.assertEquals("2034-12-31", firstCustomerPaymentMethod.getExpirationDate());
                                                    Assert.assertEquals(confirmPaymentParams.getTransaction().get("customer_uuid"), firstCustomerPaymentMethod.getCustomerUuid());

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
