package com.monri.android.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.monri.android.Monri;
import com.monri.android.ResultCallback;
import com.monri.android.model.Customer;
import com.monri.android.model.CustomerRequest;
import com.monri.android.model.CustomerResponse;
import com.monri.android.model.MonriApiOptions;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CustomerActivity extends AppCompatActivity implements ViewDelegate {
    OrderRepository orderRepository;
    Monri monri;
    TextView customerApiResult;

    public static Intent createIntent(
            Context context
    ) {
        return new Intent(context, CustomerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        customerApiResult = findViewById(R.id.customer_api_result);

        orderRepository = new OrderRepository(this, this);
        monri = new Monri(this.getApplicationContext(), MonriApiOptions.create(orderRepository.authenticityToken(), true));


        final Customer customer = new Customer(
                String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())),
                "description",
                "adnan.omerovic@monri.com",
                "Adnan",
                "00387000111",
                new HashMap<>() {{
                    put("a", "b");
                }},
                "71000",
                "Sarajevo",
                "Džemala Bijedića 2",
                "BA"

        );

        findViewById(R.id.btn_create_customer).setOnClickListener(v -> monri.getMonriApi().createCustomer(
                new CustomerRequest(
                        customer,
                        "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzY29wZXMiOlsiY3VzdG9tZXJzIiwicGF5bWVudC1tZXRob2RzIl0sImV4cCI6MTY3MTU2NTE5MSwiaXNzIjoiaHR0cHM6Ly9tb25yaS5jb20iLCJzdWIiOiI3ZGIxMWVhNWQ0YTFhZjMyNDIxYjU2NGM3OWI5NDZkMWVhZDNkYWYwIn0.mqSXRU3-bezZmCUZgocSZZ0WceTtSK1R1DFObZCkH4OWzWfXvXN2HVr0z7-iLaYGGhyzSAed9QVkNUHLWRDqCp9MqJhFU6K2wQ5zB8050D5uYoZIxgBR0YJlU6DG_9gYoQVSGLbHO05E98RVL4Gndb2j4IPbGQ-0ejcYDXXb5BA0amQc2DQhxPyMaC_MZl7ZtJprZMgmYXyMyiqGX2i44rAzk4VnXOQfW0g1r6oMc_Hjrse4hHvZ8V1j7x9QrJofL0TL5lT1lu6F2lEZfeZlymUs_HjG-8t_VOmEVPvTrExjCLZyNmNqbrMBh4rNfBjqMPv1MCOllVM16kMWRC_uxg"
                )
              , new ResultCallback<CustomerResponse>() {
                    @Override
                    public void onSuccess(final CustomerResponse result) {
                        customerApiResult.setText(String.format("%s %s", result.getUuid(), result.getCity()));
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        customerApiResult.setText(throwable.getMessage());
                    }
                }
        ));

        findViewById(R.id.btn_get_all_customers).setOnClickListener(v -> monri.getMonriApi().getAllCustomers(
                "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzY29wZXMiOlsiY3VzdG9tZXJzIiwicGF5bWVudC1tZXRob2RzIl0sImV4cCI6MTY3MTU2NTE5MSwiaXNzIjoiaHR0cHM6Ly9tb25yaS5jb20iLCJzdWIiOiI3ZGIxMWVhNWQ0YTFhZjMyNDIxYjU2NGM3OWI5NDZkMWVhZDNkYWYwIn0.mqSXRU3-bezZmCUZgocSZZ0WceTtSK1R1DFObZCkH4OWzWfXvXN2HVr0z7-iLaYGGhyzSAed9QVkNUHLWRDqCp9MqJhFU6K2wQ5zB8050D5uYoZIxgBR0YJlU6DG_9gYoQVSGLbHO05E98RVL4Gndb2j4IPbGQ-0ejcYDXXb5BA0amQc2DQhxPyMaC_MZl7ZtJprZMgmYXyMyiqGX2i44rAzk4VnXOQfW0g1r6oMc_Hjrse4hHvZ8V1j7x9QrJofL0TL5lT1lu6F2lEZfeZlymUs_HjG-8t_VOmEVPvTrExjCLZyNmNqbrMBh4rNfBjqMPv1MCOllVM16kMWRC_uxg",
                new ResultCallback<Object>() {
                    @Override
                    public void onSuccess(final Object result) {
                        customerApiResult.setText(String.format("%s", result.toString()));
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
}