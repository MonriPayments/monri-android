package com.monri.android;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;

import androidx.activity.result.ActivityResultCaller;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import com.monri.android.activity.ConfirmPaymentActivity;
import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.CreateCustomerParams;
import com.monri.android.model.Customer;
import com.monri.android.model.CustomerData;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.CustomerPaymentMethod;
import com.monri.android.model.CustomerPaymentMethodParams;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.DeleteCustomerParams;
import com.monri.android.model.DeleteCustomerResponse;
import com.monri.android.model.GetCustomerParams;
import com.monri.android.model.MerchantCustomers;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentStatus;
import com.monri.android.model.RetrieveCustomerViaMerchantCustomerUuidParams;
import com.monri.android.model.TransactionParams;
import com.monri.android.model.UpdateCustomerParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
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

    private String createPaymentSession() {

        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("add_payment_method", false);
            final String url = "https://dashboard.monri.com/api/examples/ruby/examples/create-payment-session";
            final MonriHttpResult<JSONObject> jsonObjectMonriHttpResult = MonriHttpUtil.httpsPOST(
                    url,
                    jsonObject,
                    new HashMap<>()
            );

            JSONObject jsonResponse = jsonObjectMonriHttpResult.getResult();
            Assert.assertNotNull(jsonResponse);

            String clientSecret = "";

            if (jsonResponse.has("client_secret")) {
                clientSecret = jsonResponse.getString("client_secret");
            }

            return clientSecret;
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("createPaymentSession failed");
            return "";
        }
    }

    private void createPaymentSession(Consumer<String> callback) {
        taskRunnerExecuteWithCallback(
                this::createPaymentSession,
                new ResultCallback<>() {
                    @Override
                    public void onSuccess(final String clientSecretId) {
                        callback.accept(clientSecretId);
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Assert.fail(throwable.getMessage());
                    }
                });
    }

    private String accessToken = null;

    private String createAccessToken() {
        if (accessToken != null) {
            return accessToken;
        }

        final MonriHttpResult<JSONObject> jsonObjectMonriHttpResult = MonriHttpUtil.httpsGET(
                "https://dashboard.monri.com/api/examples/ruby/examples/access_token",
                new HashMap<>()
        );

        JSONObject jsonResponse = jsonObjectMonriHttpResult.getResult();
        Assert.assertNotNull(jsonResponse);

        String accessToken = "";

        if (jsonResponse.has("access_token")) {
            try {
                accessToken = "Bearer " + jsonResponse.getString("access_token");
            } catch (JSONException e) {
                e.printStackTrace();
                Assert.fail("create access token failed");
            }
        }

        this.accessToken = accessToken;

        return accessToken;
    }

    private void createAccessToken(Consumer<String> callback) {
        taskRunnerExecuteWithCallback(
                this::createAccessToken,
                new ResultCallback<String>() {
                    @Override
                    public void onSuccess(final String accessToken) {
                        callback.accept(accessToken);
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Assert.fail(throwable.getMessage());
                    }
                }
        );
    }

    private Monri cachedMonri;
//    private ActivityScenario<ConfirmPaymentActivity> scenario;

    private void createMonriInstance(Consumer<Monri> callback) {
        createPaymentSession(clientSecretId -> {
            createMonriInstance(callback, clientSecretId);
        });
    }

    private void createMonriInstance(Consumer<Monri> callback, String clientSecretId) {

        try {
            if (cachedMonri != null) {
                callback.accept(cachedMonri);
                return;
            }

            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            MonriApiOptions monriApiOptions = MonriApiOptions.create(getAuthenticityToken(), true);

            final ConfirmPaymentParams confirmPaymentParams = getConfirmPaymentParams(
                    null,
                    clientSecretId,
                    getNon3DSCard(),
                    false
            );


            final ConfirmPaymentActivity.Request request = new ConfirmPaymentActivity.Request(confirmPaymentParams, monriApiOptions);

            taskRunnerExecute(() -> {
                final Intent intent = ConfirmPaymentActivity.createIntent(appContext, request);

//                if (scenario != null) {
//                    scenario.close();
//                }

                ActivityScenario<ConfirmPaymentActivity> scenario = ActivityScenario.launch(intent);
                scenario.moveToState(Lifecycle.State.CREATED);
                scenario.onActivity((ConfirmPaymentActivity activity) -> {
                    cachedMonri = new Monri(((ActivityResultCaller) activity), monriApiOptions);
                    callback.accept(cachedMonri);
                });


            });


        } catch (Exception e) {
            Log.d("createMonriInstance failed, exception: ", e.getMessage());
        }

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

    private void createCustomer(final String accessToken, final ResultCallback<Customer> callback) {
        createCustomer(accessToken, callback, null);
    }

    private void createCustomer(final String accessToken, final ResultCallback<Customer> callback, final String merchantCustomerId) {
        createMonriInstance(monri -> {
            final CustomerData customerData = getCustomerData();

            if (merchantCustomerId != null) {
                customerData.setMerchantCustomerUuid(merchantCustomerId);
            }

            CreateCustomerParams createCustomerParams = new CreateCustomerParams(
                    customerData,
                    accessToken
            );

            monri.getMonriApi().customers().create(createCustomerParams, callback);
        });

    }

    private void createCustomer(Consumer<Customer> customerConsumer) {
        createAccessToken(accessToken -> {
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
        });

    }

    private void createCustomers(String accessToken, short size, Consumer<Pair<List<Customer>, String>> callback) {
        List<Customer> customerList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
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
        }

        callback.accept(Pair.create(customerList, null));
    }

    @Test
    public void testCreateCustomer() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(1);

        createAccessToken(accessToken -> {
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
        });

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));
    }

    @Test
    public void testUpdateCustomer() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(1);
        CustomerData customerData = new CustomerData();
        final String newName = "Adnan Omerovic";
        customerData.setName(newName);

        createMonriInstance(monri -> {
            createAccessToken(accessToken -> {
                createCustomer(accessToken, new ResultCallback<Customer>() {
                    @Override
                    public void onSuccess(final Customer createdCustomer) {
                        monri.getMonriApi().customers().update(
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
                    }
                });
            });
        });

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));
    }

    @Test
    public void testRetrieveCustomer() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(1);

        createMonriInstance(monri -> {
            createAccessToken(accessToken -> {
                createCustomer(accessToken, new ResultCallback<Customer>() {
                    @Override
                    public void onSuccess(final Customer createdCustomer) {
                        monri.getMonriApi().customers().get(
                                new GetCustomerParams(
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
                    }
                });
            });
        });

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));
    }

    @Test
    public void testRetrieveAllCustomers() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(1);

        final AtomicReference<List<Customer>> createdCustomersAtomicReference = new AtomicReference<>();
        final AtomicReference<List<Customer>> allCustomersAtomicReference = new AtomicReference<>();

        createMonriInstance(monri -> {
            createAccessToken(accessToken -> {
                createCustomers(accessToken, (short) 3, (Pair<List<Customer>, String> listStringPair) -> {
                    final List<Customer> customerResponse = listStringPair.first;

                    if (customerResponse == null) {
                        Assert.fail(listStringPair.second);
                    }

                    createdCustomersAtomicReference.set(customerResponse);

                    monri.getMonriApi().customers().all(accessToken, new ResultCallback<MerchantCustomers>() {
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
            });
        });

        Assert.assertTrue(signal.await(20, TimeUnit.SECONDS));

        final List<Customer> createdCustomers = createdCustomersAtomicReference.get();
        final List<Customer> allCustomers = createdCustomersAtomicReference.get();

        for (int i = 0; i < createdCustomers.size(); i++) {
            Assert.assertEquals(createdCustomers.get(i).getDescription(), allCustomers.get(i).getDescription());
        }
    }

    @Test
    public void testRetrieveCustomerViaMerchantId() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(1);
        String merchantCustomerId = UUID.randomUUID().toString();

        createMonriInstance(monri -> {
            createAccessToken(accessToken -> {
                createCustomer(
                        accessToken,
                        new ResultCallback<Customer>() {
                            @Override
                            public void onSuccess(final Customer createdCustomer) {
                                monri.getMonriApi().customers().getViaMerchantCustomerUuid(
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
                            }
                        },
                        merchantCustomerId
                );
            });
        });

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));
    }

    @Test
    public void testDeleteCustomer() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(1);

        createMonriInstance(monri -> {
            createAccessToken(accessToken -> {
                createCustomer(accessToken, new ResultCallback<Customer>() {
                    @Override
                    public void onSuccess(final Customer createdCustomer) {
                        signal.countDown();
                        monri.getMonriApi().customers().delete(
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
            });
        });

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));

    }

    @Test
    public void testCustomerPayment() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(1);

        createMonriInstance(monri -> {
            createCustomer((Customer customer) -> {
                createPaymentSession(clientSecretId -> {
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
                            monri.getMonriApi().customers().paymentMethods(
                                    new CustomerPaymentMethodParams(
                                            customer.getUuid(),
                                            20,
                                            0,
                                            createAccessToken()
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
                        }
                    });
                });

            });
        });

        Assert.assertTrue(signal.await(30, TimeUnit.SECONDS));
    }
}
