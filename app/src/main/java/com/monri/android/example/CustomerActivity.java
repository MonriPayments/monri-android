package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.monri.android.Monri;
import com.monri.android.ResultCallback;
import com.monri.android.model.MerchantCustomers;
import com.monri.android.model.CreateCustomerParams;
import com.monri.android.model.DeleteCustomerParams;
import com.monri.android.model.DeleteCustomerResponse;
import com.monri.android.model.CustomerPaymentMethodParams;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.CustomerData;
import com.monri.android.model.Customer;
import com.monri.android.model.RetrieveCustomerViaMerchantCustomerUuidParams;
import com.monri.android.model.RetrieveCustomerParams;
import com.monri.android.model.UpdateCustomerParams;
import com.monri.android.model.MonriApiOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CustomerActivity extends AppCompatActivity implements ViewDelegate {
    OrderRepository orderRepository;
    Monri monri;
    TextView customerApiResult;
    Customer customer = null;

    public static Intent createIntent(
            Context context
    ) {
        return new Intent(context, CustomerActivity.class);
    }

    private CustomerData getCustomerRequestBody(){
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
        monri = new Monri(this.getApplicationContext(), MonriApiOptions.create(orderRepository.authenticityToken(), true));
        String accessToken = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzY29wZXMiOlsiY3VzdG9tZXJzIiwicGF5bWVudC1tZXRob2RzIl0sImV4cCI6MTY3MzM0NTc1OSwiaXNzIjoiaHR0cHM6Ly9tb25yaS5jb20iLCJzdWIiOiI2YTEzZDc5YmRlOGRhOTMyMGU4ODkyM2NiMzQ3MmZiNjM4NjE5Y2NiIn0.KV3gBvvty_kssWo89QWYauPkUHoPbwbSPY8QV2YHGA8lYB8hz0xl-mhieah9QbLc2XCqlTZ5jwD36nAWup3BUkf7atYsVQqYL-3eblKz08o6JP09BW5NMDN_nmacbjYP7CBbTeycJLCcWi8jvt97fyXkXv5XSc1HuLsbsQckUojZIZ_xGyKowia5ItIyMtj51qApczYhBVceOb3m7Yu2ZZbxm35CJMTTVi0BmX61hhDSnXwhpbVQ63djWUoPHKc3xN-PWXkg1M7pbFIU95mzNAPN796V0TghE8Lf9dXe7NnnwP8nVZ8dj4EyUm0mDxZZlfgeKt11uu0wLdDrYz3XRA";

        findViewById(R.id.btn_create_customer).setOnClickListener(v -> {

                    monri.getMonriApi().createCustomer(
                            new CreateCustomerParams(
                                    getCustomerRequestBody(),
                                    accessToken
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
                }
        );


        findViewById(R.id.btn_update_customer).setOnClickListener(v -> {
            final CustomerData customerData = getCustomerRequestBody();
            monri.getMonriApi().updateCustomer(
                    new UpdateCustomerParams(
                            customerData.setMetadata(new HashMap<>() {{
                                put("update customer", new Date().toString());
                            }}),
                            customer.getUuid(),
                            accessToken

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

        findViewById(R.id.btn_delete_customer).setOnClickListener(v -> monri.getMonriApi().deleteCustomer(
                new DeleteCustomerParams(
                        accessToken,
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
        ));

        findViewById(R.id.btn_retrieve_customer).setOnClickListener(v -> monri.getMonriApi().retrieveCustomer(
                new RetrieveCustomerParams(
                        accessToken,
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
        ));

        findViewById(R.id.btn_retrieve_customer_via_merchant_id).setOnClickListener(v -> monri.getMonriApi().retrieveCustomerViaMerchantCustomerUuid(
                new RetrieveCustomerViaMerchantCustomerUuidParams(
                        accessToken,
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
        ));

        findViewById(R.id.btn_get_all_customers).setOnClickListener(v -> monri.getMonriApi().retrieveAllCustomers(
                accessToken,
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
        ));

        findViewById(R.id.btn_retrieve_saved_cards_from_customer).setOnClickListener(v -> monri.getMonriApi().retrieveCustomerPaymentMethods(
                new CustomerPaymentMethodParams(
                        customer.getUuid(),
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

    private void enableButtons() {
        findViewById(R.id.btn_update_customer).setEnabled(customer != null);
        findViewById(R.id.btn_delete_customer).setEnabled(customer != null);
        findViewById(R.id.btn_retrieve_customer).setEnabled(customer != null);
        findViewById(R.id.btn_retrieve_customer_via_merchant_id).setEnabled(customer != null);
        findViewById(R.id.btn_retrieve_saved_cards_from_customer).setEnabled(customer != null);
    }
}