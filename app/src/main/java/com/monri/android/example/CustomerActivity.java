package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.monri.android.Monri;
import com.monri.android.ResultCallback;
import com.monri.android.model.CustomerAllResponse;
import com.monri.android.model.CustomerCreateRequest;
import com.monri.android.model.CustomerDeleteRequest;
import com.monri.android.model.CustomerDeleteResponse;
import com.monri.android.model.CustomerPaymentMethodRequest;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.CustomerRequestBody;
import com.monri.android.model.CustomerResponse;
import com.monri.android.model.CustomerRetrieveMerchantIdRequest;
import com.monri.android.model.CustomerRetrieveRequest;
import com.monri.android.model.CustomerUpdateRequest;
import com.monri.android.model.MonriApiOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CustomerActivity extends AppCompatActivity implements ViewDelegate {
    OrderRepository orderRepository;
    Monri monri;
    TextView customerApiResult;
    CustomerResponse customerResponse = null;

    public static Intent createIntent(
            Context context
    ) {
        return new Intent(context, CustomerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        enableButtons();

        customerApiResult = findViewById(R.id.customer_api_result);

        orderRepository = new OrderRepository(this, this);
        monri = new Monri(this.getApplicationContext(), MonriApiOptions.create(orderRepository.authenticityToken(), true));
        String accessToken = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzY29wZXMiOlsiY3VzdG9tZXJzIiwicGF5bWVudC1tZXRob2RzIl0sImV4cCI6MTY3MjE3OTM2MCwiaXNzIjoiaHR0cHM6Ly9tb25yaS5jb20iLCJzdWIiOiI3ZGIxMWVhNWQ0YTFhZjMyNDIxYjU2NGM3OWI5NDZkMWVhZDNkYWYwIn0.KMbpTjb4pRhtr7Dgw3K1w-1hsxj6GkPs-L6s3KkVMNJguq4dTGGeuA2rU3etgyeQ88fzD_5NUeQKICgrzyaJpDhWhS9DFLnhu5VPE-vuF3ywdi1G-pWYxxVKje8TEhHFTTkvUjq65Oy69D3x7b8bx1JlynqJjYw4-Lanz-ChWMdjbbufUBBiwOv5PQOtmysVQhuZ5T_VjUttT5OJXApJN4H6uB47pFdmprlJcjKYIuuAiLbzVb5y4mrpVdCNxdGF7B1ltc9e1TaxKUXMpjQ1B0nhE5RUKUJSvkDQqIZ8DTN-iYWRR9gtVWNDRnWBwSL4_CSBTD56LgQAjM1TkJlr2g";
        String timestamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        final CustomerRequestBody customerRequestBody = new CustomerRequestBody()
                .setMerchantCustomerId(timestamp)
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

        findViewById(R.id.btn_create_customer).setOnClickListener(v -> monri.getMonriApi().createCustomer(
                new CustomerCreateRequest(
                        customerRequestBody,
                        accessToken
                )
              , new ResultCallback<CustomerResponse>() {
                    @Override
                    public void onSuccess(final CustomerResponse result) {
                        customerResponse = result;
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
        ));


        findViewById(R.id.btn_update_customer).setOnClickListener(v -> monri.getMonriApi().updateCustomer(
                new CustomerUpdateRequest(
                        customerRequestBody.setMetadata(new HashMap<>() {{
                            put("update customer", new Date().toString());
                        }}),
                        customerResponse.getUuid(),
                        accessToken

                ),
                new ResultCallback<CustomerResponse>() {
                    @Override
                    public void onSuccess(final CustomerResponse result) {
                        customerApiResult.setText(String.format("%s", result.toString()));
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        customerApiResult.setText(String.format("%s", throwable.getMessage()));
                    }
                }
        ));

        findViewById(R.id.btn_delete_customer).setOnClickListener(v -> monri.getMonriApi().deleteCustomer(
                new CustomerDeleteRequest(
                        accessToken,
                        customerResponse.getUuid()
                ),
                new ResultCallback<CustomerDeleteResponse>() {
                    @Override
                    public void onSuccess(final CustomerDeleteResponse result) {
                        customerApiResult.setText(String.format("%s", result.toString()));
                        if("approved".equals(result.getStatus())){
                            customerResponse = null;
                            enableButtons();
                        }
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        customerApiResult.setText(String.format("%s", throwable.getMessage()));
                    }
                }
        ));

        findViewById(R.id.btn_retrieve_customer).setOnClickListener(v -> monri.getMonriApi().retrieveCustomer(
                new CustomerRetrieveRequest(
                        accessToken,
                        customerResponse.getUuid()
                ),
                new ResultCallback<CustomerResponse>() {
                    @Override
                    public void onSuccess(final CustomerResponse result) {
                        customerApiResult.setText(String.format("Retrieve customer: %s", result.toString()));
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        customerApiResult.setText(String.format("%s", throwable.getMessage()));
                    }
                }
        ));

        findViewById(R.id.btn_retrieve_customer_via_merchant_id).setOnClickListener(v -> monri.getMonriApi().retrieveCustomerViaMerchantCustomerId(
                new CustomerRetrieveMerchantIdRequest(
                        accessToken,
                        customerResponse.getMerchantCustomerId()
                ),
                new ResultCallback<CustomerResponse>() {
                    @Override
                    public void onSuccess(final CustomerResponse result) {
                        customerApiResult.setText(String.format("Retrieve customer via customer_merchant_id: %s", result.toString()));
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        customerApiResult.setText(String.format("%s", throwable.getMessage()));
                    }
                }
        ));

        findViewById(R.id.btn_get_all_customers).setOnClickListener(v -> monri.getMonriApi().getAllCustomers(
                accessToken,
                new ResultCallback<CustomerAllResponse>() {
                    @Override
                    public void onSuccess(final CustomerAllResponse result) {
                        StringBuilder name = new StringBuilder();
                        for(CustomerResponse customerResponse : result.getCustomerResponseList()){
                            name.append(customerResponse.getName()).append('\n');
                        }
                        customerApiResult.setText(String.format("%s", name.toString()));
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        customerApiResult.setText(String.format("%s", throwable.getMessage()));
                    }
                }
        ));

        findViewById(R.id.btn_retrieve_saved_cards_from_customer).setOnClickListener(v -> monri.getMonriApi().retrieveCustomerPaymentMethods(
                new CustomerPaymentMethodRequest(
                        customerResponse.getUuid(),
                        20,
                        0,
                        accessToken
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
        ));
    }


    @Override
    public void statusMessage(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void enableButtons(){
        findViewById(R.id.btn_update_customer).setEnabled(customerResponse != null);
        findViewById(R.id.btn_delete_customer).setEnabled(customerResponse != null);
        findViewById(R.id.btn_retrieve_customer).setEnabled(customerResponse != null);
        findViewById(R.id.btn_retrieve_customer_via_merchant_id).setEnabled(customerResponse != null);
        findViewById(R.id.btn_retrieve_saved_cards_from_customer).setEnabled(customerResponse != null);
    }
}