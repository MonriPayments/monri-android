package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.disposables.Disposable;

public class ExamplesActivity extends AppCompatActivity implements ViewDelegate {

    public static Intent createIntent(Context context) {
        return new Intent(context, ExamplesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examples);

        final OrderRepository orderRepository = new OrderRepository(this, this);

        this.<Button>findViewById(R.id.btn_payment_with_new_card).setOnClickListener(v -> {
            v.setEnabled(false);
            final Disposable subscribe = orderRepository.prepareTransaction()
                    .doFinally(() -> v.setEnabled(true))
                    .subscribe(prepareTransactionResponse -> {
                        startActivity(MainActivity.createIntent(ExamplesActivity.this, prepareTransactionResponse));
                    }, ExamplesActivity.this::handleError);
        });

        this.<Button>findViewById(R.id.btn_payment_with_saved_card).setOnClickListener(v -> {
            v.setEnabled(false);
            final Disposable subscribe = orderRepository
                    .prepareTransaction()
                    .doFinally(() -> v.setEnabled(true))
                    .subscribe(prepareTransactionResponse -> {
                        startActivity(PaymentMethodsActivity.createIntent(ExamplesActivity.this, prepareTransactionResponse));
                    }, ExamplesActivity.this::handleError);

        });

        this.<Button>findViewById(R.id.btn_payment_session_create).setOnClickListener(v -> {
            this.startActivity(PaymentActivity.createIntent(this));
        });

        findViewById(R.id.btn_customer_activity).setOnClickListener(v -> {
            startActivity(CustomerActivity.createIntent(this));
        });
    }

    void handleError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void statusMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
