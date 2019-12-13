package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Supplier;

import com.monri.android.Monri;
import com.monri.android.ResultCallback;
import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.PaymentMethodParams;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.TransactionParams;

import java.util.Arrays;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class PaymentActivity extends AppCompatActivity implements ResultCallback<PaymentResult> {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    OrderRepository orderRepository;
    Monri monri;

    TextView txtViewResult;

    public static Intent createIntent(Context context) {
        return new Intent(context, PaymentActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        orderRepository = new OrderRepository(this);
        monri = new Monri(this.getApplicationContext(), orderRepository.authenticityToken());

        txtViewResult = findViewById(R.id.txt_result_payment_example);

        findViewById(R.id.btn_payment_example)
                .setOnClickListener(v -> {
                    final Disposable subscribe = orderRepository
                            .createPayment()
                            .subscribe(handlePaymentSessionResponse(this::nonThreeDsCard, false), this::handlePaymentSessionFailure);
                    compositeDisposable.add(subscribe);
                });

        findViewById(R.id.btn_payment_example_3ds1)
                .setOnClickListener(v -> {
                    final Disposable subscribe = orderRepository
                            .createPayment()
                            .subscribe(handlePaymentSessionResponse(this::threeDsCard, false), this::handlePaymentSessionFailure);
                    compositeDisposable.add(subscribe);
                });

        findViewById(R.id.btn_add_payment_method_example)
                .setOnClickListener(v -> {
                    final Disposable subscribe = orderRepository
                            .createPayment(true)
                            .subscribe(handlePaymentSessionResponse(this::nonThreeDsCard, true), this::handlePaymentSessionFailure);
                    compositeDisposable.add(subscribe);
                });

        findViewById(R.id.btn_add_payment_method_example_3ds1)
                .setOnClickListener(v -> {
                    final Disposable subscribe = orderRepository
                            .createPayment(true)
                            .subscribe(handlePaymentSessionResponse(this::threeDsCard, true), this::handlePaymentSessionFailure);
                    compositeDisposable.add(subscribe);
                });
    }

    private void handlePaymentSessionFailure(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    Consumer<NewPaymentResponse> handlePaymentSessionResponse(Supplier<PaymentMethodParams> paymentMethodParamsSupplier, boolean addPaymentMethod) {
        return newPaymentResponse -> {
            if (!"approved".equals(newPaymentResponse.getStatus())) {
                Toast.makeText(this, "Payment session create failed", Toast.LENGTH_LONG).show();
            } else {

                final CustomerParams customerParams = new CustomerParams()
                        .setAddress("Adresa")
                        .setFullName("Tester Testerovic")
                        .setCity("Sarajevo")
                        .setZip("71000")
                        .setPhone("+38761000111")
                        .setCountry("Bosnia And Herzegovina")
                        .setEmail("tester+android_sdk@monri.com");

                monri.confirmPayment(this, ConfirmPaymentParams.create(
                        newPaymentResponse.getClientSecret(),
                        paymentMethodParamsSupplier.get(),
                        TransactionParams.create()
                                .set("order_info", "Android SDK payment session")
                                .set(customerParams)
                ));

            }
        };
    }

    PaymentMethodParams nonThreeDsCard() {
        return new Card("4111 1111 1111 1111", 12, 2024, "123").toPaymentMethodParams();
    }

    PaymentMethodParams threeDsCard() {
        return new Card("4341 7920 0000 0044", 12, 2024, "123").toPaymentMethodParams();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        final boolean monriPaymentResult = monri.onPaymentResult(requestCode, data, this);
        if (!monriPaymentResult) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSuccess(PaymentResult result) {
        Toast.makeText(this, String.format("Transaction processed with result %s", result.getStatus()), Toast.LENGTH_LONG).show();
        txtViewResult.setText(result.toString());
    }

    @Override
    public void onError(Throwable throwable) {
        txtViewResult.setText(String.format("%s\n\n%s", throwable.getCause(), Arrays.toString(throwable.getStackTrace())));
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }
}
