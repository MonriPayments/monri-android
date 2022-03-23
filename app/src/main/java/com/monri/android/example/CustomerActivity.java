package com.monri.android.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.monri.android.Monri;
import com.monri.android.ResultCallback;
import com.monri.android.model.CustomerRequest;
import com.monri.android.model.CustomerResponse;
import com.monri.android.model.MonriApiOptions;

import java.util.HashMap;

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

        findViewById(R.id.btn_create_customer).setOnClickListener(v -> monri.getMonriApi().createCustomer(
                new CustomerRequest(
                        "merchantCustomerId-1",
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

                ), new ResultCallback<CustomerResponse>() {
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
    }


    @Override
    public void statusMessage(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}