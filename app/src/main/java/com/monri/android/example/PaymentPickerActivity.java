package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentMethodParams;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.SavedCard;
import com.monri.android.model.TransactionParams;
import com.monri.android.view.CardMultilineWidget;

import java.util.Arrays;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class PaymentPickerActivity extends AppCompatActivity implements ResultCallback<PaymentResult>, ViewDelegate {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    OrderRepository orderRepository;
    Monri monri;

    TextView txtViewResult;

    EditText etCvv;

    TextView tvCardType;
    TextView tvMaskedPan;
    Button btnPay;

    View paymentPickerSavedCardView;

    View paymentPickerNewCardView;

    public static Intent createIntent(Context context,
                                      String panToken,
                                      String maskedPan,
                                      String cardType) {
        final Intent intent = new Intent(context, PaymentPickerActivity.class);
        intent.putExtra("NEW_CARD_PAYMENT", false);
        intent.putExtra("PAN_TOKEN", panToken);
        intent.putExtra("MASKED_PAN", maskedPan);
        intent.putExtra("CARD_TYPE", cardType);
        return intent;
    }

    public static Intent createIntent(Context context, boolean threeDsCard, boolean addPaymentMethodScenario, boolean saveCardForFuturePayments) {
        Intent intent = new Intent(context, PaymentPickerActivity.class);
        intent.putExtra("NEW_CARD_PAYMENT", true);
        intent.putExtra("THREE_DS_CARD", threeDsCard);
        intent.putExtra("ADD_PAYMENT_METHOD_SCENARIO", addPaymentMethodScenario);
        intent.putExtra("saveCardForFuturePayments", saveCardForFuturePayments);
        return intent;
    }


    Button continueWithPayment;
    CardMultilineWidget cardMultilineWidget;

    boolean threeDsCard;
    boolean addPaymentMethodScenario;
    boolean saveCardForFuturePayments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_picker);

        orderRepository = new OrderRepository(this, this);
        monri = new Monri(this.getApplicationContext(), MonriApiOptions.create(orderRepository.authenticityToken(), true));

        Intent intent = getIntent();
        boolean newCardPayment = intent.getBooleanExtra("NEW_CARD_PAYMENT", true);

        paymentPickerNewCardView = findViewById(R.id.payment_picker_new_card);
        paymentPickerSavedCardView = findViewById(R.id.payment_picker_saved_card);

        if (newCardPayment) {
            newCardPaymentViewSetup(intent);
        } else {
            savedCardPaymentViewSetup(intent);
        }
    }

    private void savedCardPaymentViewSetup(Intent intent) {
        paymentPickerSavedCardView.setVisibility(View.VISIBLE);
        txtViewResult = findViewById(R.id.txt_result_payment_example_1);
        final String panToken = intent.getStringExtra("PAN_TOKEN");
        final String maskedPan = intent.getStringExtra("MASKED_PAN");
        final String cardType = intent.getStringExtra("CARD_TYPE");
        tvCardType = findViewById(R.id.tv_card_type);
        tvMaskedPan = findViewById(R.id.tv_masked_pan);
        etCvv = findViewById(R.id.et_cvv);
        btnPay = findViewById(R.id.btn_payment);

        tvCardType.setText(cardType);
        tvMaskedPan.setText(maskedPan);

        btnPay.setOnClickListener(v -> {
            SavedCard savedCard = new SavedCard(panToken, etCvv.getText().toString());
            final Disposable subscribe = orderRepository
                    .createPayment(false)
                    .subscribe(handlePaymentSessionResponse(savedCard::toPaymentMethodParams));
            compositeDisposable.add(subscribe);
        });
    }

    private void newCardPaymentViewSetup(Intent intent) {
        paymentPickerNewCardView.setVisibility(View.VISIBLE);
        threeDsCard = intent.getBooleanExtra("THREE_DS_CARD", false);
        addPaymentMethodScenario = intent.getBooleanExtra("ADD_PAYMENT_METHOD_SCENARIO", false);
        saveCardForFuturePayments = intent.getBooleanExtra("saveCardForFuturePayments", false);

        continueWithPayment = findViewById(R.id.continue_with_payment);
        txtViewResult = findViewById(R.id.txt_result_payment_example);

        if (threeDsCard) {
            continueWithPayment.setText(R.string.continue_with_3ds_enrolled_card);
        } else {
            continueWithPayment.setText(R.string.continue_with_non_3ds_enrolled_card);
        }

        continueWithPayment.setOnClickListener(v -> {

            Supplier<PaymentMethodParams> paymentMethodParamsSupplier;

            if (threeDsCard) {
                paymentMethodParamsSupplier = this::threeDsCard;
            } else {
                paymentMethodParamsSupplier = this::nonThreeDsCard;
            }

            final Disposable subscribe = orderRepository.createPayment(addPaymentMethodScenario)
                    .subscribe(handlePaymentSessionResponse(paymentMethodParamsSupplier));

            compositeDisposable.add(subscribe);
        });

        cardMultilineWidget = findViewById(R.id.payment_picker_card);

        findViewById(R.id.payment_picker_pay).setOnClickListener(v -> {
            final Card card = cardMultilineWidget.getCard();
            if (card == null) {
                Toast.makeText(getApplicationContext(), "Card is not valid", Toast.LENGTH_SHORT).show();
            } else {
                final Disposable subscribe = orderRepository.createPayment(addPaymentMethodScenario)
                        .subscribe(handlePaymentSessionResponse(() -> card.setTokenizePan(saveCardForFuturePayments).toPaymentMethodParams()));

                compositeDisposable.add(subscribe);
            }
        });
    }

    Consumer<NewPaymentResponse> handlePaymentSessionResponse(Supplier<PaymentMethodParams> paymentMethodParamsSupplier) {
        return newPaymentResponse -> {
            if (!"approved".equals(newPaymentResponse.getStatus())) {
                Toast.makeText(this, "Payment session create failed", Toast.LENGTH_LONG).show();
            } else {

                final CustomerParams customerParams = new CustomerParams()
                        .setCustomerUuid("38400000-8cf0-11bd-b23e-10b96e4ef00d")
                        .setAddress("Adresa")
                        .setFullName("Tester Testerovic")
                        .setCity("Sarajevo")
                        .setZip("71000")
                        .setPhone("+38761000111")
                        .setCountry("BA")
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
        ResultCallback<PaymentResult> callback = this;
        final boolean monriPaymentResult = monri.onPaymentResult(requestCode, data, callback);
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

    @Override
    public void statusMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
