package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.appcompat.app.AppCompatActivity;

import com.monri.android.Monri;
import com.monri.android.ResultCallback;
import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.CreateCustomerParams;
import com.monri.android.model.Customer;
import com.monri.android.model.CustomerData;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.CustomerPaymentMethodParams;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.DeleteCustomerParams;
import com.monri.android.model.DeleteCustomerResponse;
import com.monri.android.model.GetCustomerParams;
import com.monri.android.model.MerchantCustomers;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.RetrieveCustomerViaMerchantCustomerUuidParams;
import com.monri.android.model.TransactionParams;
import com.monri.android.model.UpdateCustomerParams;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class CustomerActivity extends AppCompatActivity implements ViewDelegate {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    OrderRepository orderRepository;
    Monri monri;
    TextView customerApiResult;
    Customer customer = null;

    public static Intent createIntent(
            Context context
    ) {
        return new Intent(context, CustomerActivity.class);
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

    private CustomerData getCustomerRequestBody() {
        String timestamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        return new CustomerData()
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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        enableButtons();

        customerApiResult = findViewById(R.id.customer_api_result);

        orderRepository = new OrderRepository(this, this);
        monri = new Monri(((ActivityResultCaller) this), MonriApiOptions.create(orderRepository.authenticityToken(), true));

        findViewById(R.id.btn_create_customer).setOnClickListener(v -> {

                    final Disposable subscribe = orderRepository.createAccessToken()
                            .subscribe(accessTokenResponse -> {
                                monri.getMonriApi().customers().create(
                                        new CreateCustomerParams(
                                                getCustomerRequestBody(),
                                                "Bearer " + accessTokenResponse.accessToken
                                        )
                                        , new ResultCallback<Customer>() {
                                            @Override
                                            public void onSuccess(final Customer result) {
                                                customer = result;
                                                customerApiResult.setText(String.format("%s %s %s %s",
                                                        result.getUuid(),
                                                        result.getCity(),
                                                        result.getMerchantCustomerId(),
                                                        result.getUpdatedAt()
                                                ));
                                                enableButtons();
                                            }

                                            @Override
                                            public void onError(final Throwable throwable) {
                                                customerApiResult.setText(throwable.getMessage());
                                            }
                                        }
                                );
                            });

                    compositeDisposable.add(subscribe);
                }
        );


        findViewById(R.id.btn_update_customer).setOnClickListener(v -> {
            final Disposable subscribe = orderRepository.createAccessToken()
                    .subscribe(accessTokenResponse -> {
                        final CustomerData customerData = getCustomerRequestBody();
                        monri.getMonriApi().customers().update(
                                new UpdateCustomerParams(
                                        customerData.setMetadata(new HashMap<>() {{
                                            put("update customer", new Date().toString());
                                        }}),
                                        customer.getUuid(),
                                        "Bearer " + accessTokenResponse.accessToken

                                ),
                                new ResultCallback<Customer>() {
                                    @Override
                                    public void onSuccess(final Customer result) {
                                        customerApiResult.setText(String.format("%s", result.toString()));
                                    }

                                    @Override
                                    public void onError(final Throwable throwable) {
                                        customerApiResult.setText(String.format("%s", throwable.getMessage()));
                                    }
                                }
                        );
                    });

            compositeDisposable.add(subscribe);
        });

        findViewById(R.id.btn_delete_customer).setOnClickListener(v -> {
            final Disposable subscribe = orderRepository.createAccessToken()
                    .subscribe(accessTokenResponse -> {
                        monri.getMonriApi().customers().delete(
                                new DeleteCustomerParams(
                                        "Bearer " + accessTokenResponse.accessToken,
                                        customer.getUuid()
                                ),
                                new ResultCallback<DeleteCustomerResponse>() {
                                    @Override
                                    public void onSuccess(final DeleteCustomerResponse result) {
                                        customerApiResult.setText(String.format("%s", result.toString()));
                                        if ("approved".equals(result.getStatus())) {
                                            customer = null;
                                            enableButtons();
                                        }
                                    }

                                    @Override
                                    public void onError(final Throwable throwable) {
                                        customerApiResult.setText(String.format("%s", throwable.getMessage()));
                                    }
                                }
                        );
                    });

            compositeDisposable.add(subscribe);
        });

        findViewById(R.id.btn_retrieve_customer).setOnClickListener(v -> {
            final Disposable subscribe = orderRepository.createAccessToken()
                    .subscribe(accessTokenResponse -> {
                        monri.getMonriApi().customers().get(
                                new GetCustomerParams(
                                        "Bearer " + accessTokenResponse.accessToken,
                                        customer.getUuid()
                                ),
                                new ResultCallback<Customer>() {
                                    @Override
                                    public void onSuccess(final Customer result) {
                                        customerApiResult.setText(String.format("Retrieve customer: %s", result.toString()));
                                    }

                                    @Override
                                    public void onError(final Throwable throwable) {
                                        customerApiResult.setText(String.format("%s", throwable.getMessage()));
                                    }
                                }
                        );
                    });

            compositeDisposable.add(subscribe);
        });

        findViewById(R.id.btn_retrieve_customer_via_merchant_id).setOnClickListener(v -> {
            final Disposable subscribe = orderRepository.createAccessToken()
                    .subscribe(accessTokenResponse -> {
                        monri.getMonriApi().customers().getViaMerchantCustomerUuid(
                                new RetrieveCustomerViaMerchantCustomerUuidParams(
                                        "Bearer " + accessTokenResponse.accessToken,
                                        customer.getMerchantCustomerId()
                                ),
                                new ResultCallback<Customer>() {
                                    @Override
                                    public void onSuccess(final Customer result) {
                                        customerApiResult.setText(String.format("Retrieve customer via customer_merchant_id: %s", result.toString()));
                                    }

                                    @Override
                                    public void onError(final Throwable throwable) {
                                        customerApiResult.setText(String.format("%s", throwable.getMessage()));
                                    }
                                }
                        );
                    });

            compositeDisposable.add(subscribe);
        });

        findViewById(R.id.btn_get_all_customers).setOnClickListener(v -> {
            final Disposable subscribe = orderRepository.createAccessToken()
                    .subscribe(accessTokenResponse -> {
                        monri.getMonriApi().customers().all(
                                "Bearer " + accessTokenResponse.accessToken,
                                new ResultCallback<MerchantCustomers>() {
                                    @Override
                                    public void onSuccess(final MerchantCustomers result) {
                                        StringBuilder name = new StringBuilder();
                                        for (Customer customer : result.getCustomerResponseList()) {
                                            name.append(customer.getName()).append('\n');
                                        }
                                        customerApiResult.setText(String.format("%s", name));
                                    }

                                    @Override
                                    public void onError(final Throwable throwable) {
                                        customerApiResult.setText(String.format("%s", throwable.getMessage()));
                                    }
                                }
                        );
                    });

            compositeDisposable.add(subscribe);
        });

        findViewById(R.id.btn_retrieve_saved_cards_from_customer).setOnClickListener(v -> {
            final Disposable subscribe = orderRepository.createAccessToken()
                    .subscribe(accessTokenResponse -> {
                        monri.getMonriApi().customers().paymentMethods(
                                new CustomerPaymentMethodParams(
                                        customer.getUuid(),
                                        20,
                                        0,
                                        "Bearer " + accessTokenResponse.accessToken
                                ),
                                new ResultCallback<CustomerPaymentMethodResponse>() {
                                    @Override
                                    public void onSuccess(final CustomerPaymentMethodResponse result) {
                                        customerApiResult.setText(String.format("Payment methods: %s", result.toString()));
                                    }

                                    @Override
                                    public void onError(final Throwable throwable) {
                                        customerApiResult.setText(String.format("%s", throwable.getMessage()));
                                    }
                                }
                        );
                    });

            compositeDisposable.add(subscribe);
        });


        findViewById(R.id.btn_confirm_payment_customer_uuid).setOnClickListener(v -> {
            final Disposable subscribe = orderRepository.createAccessToken()
                    .subscribe(accessTokenResponse -> {
                        monri.getMonriApi().customers().create(
                                new CreateCustomerParams(
                                        getCustomerRequestBody(),
                                        "Bearer " + accessTokenResponse.accessToken
                                )
                                , new ResultCallback<Customer>() {
                                    @Override
                                    public void onSuccess(final Customer result) {
                                        customer = result;
                                        customerApiResult.setText(String.format("%s %s %s %s",
                                                result.getUuid(),
                                                result.getCity(),
                                                result.getMerchantCustomerId(),
                                                result.getUpdatedAt()
                                        ));
                                        confirmPayment(customer.getUuid());
                                    }

                                    @Override
                                    public void onError(final Throwable throwable) {
                                        customerApiResult.setText(throwable.getMessage());
                                    }
                                }
                        );
                    });

            compositeDisposable.add(subscribe);
        });
    }


    private void confirmPayment(String customerUuid) {
        final Disposable subscribe = orderRepository.createPayment(true)
                .subscribe(response -> {
                    final ConfirmPaymentParams confirmPaymentParams = getConfirmPaymentParams(
                            customerUuid,
                            response.clientSecret,
                            getNon3DSCard(),
                            true
                    );
                    monri.confirmPayment(confirmPaymentParams, (PaymentResult result, Throwable cause) -> {
                        if (cause == null) {
                            customerApiResult.setText(String.format("Payment: %s \n", result.getStatus()));
                            if (result.getErrors() != null) {
                                for (String error : result.getErrors()) {
                                    customerApiResult.append(String.format("Error: %s \n", error));
                                }
                            }
                        } else {
                            customerApiResult.setText(String.format("%s", cause.getMessage()));
                        }
                    });
                });

        compositeDisposable.add(subscribe);
    }


    @Override
    public void statusMessage(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void enableButtons() {
        findViewById(R.id.btn_update_customer).setEnabled(customer != null);
        findViewById(R.id.btn_delete_customer).setEnabled(customer != null);
        findViewById(R.id.btn_retrieve_customer).setEnabled(customer != null);
        findViewById(R.id.btn_retrieve_customer_via_merchant_id).setEnabled(customer != null);
        findViewById(R.id.btn_retrieve_saved_cards_from_customer).setEnabled(customer != null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}