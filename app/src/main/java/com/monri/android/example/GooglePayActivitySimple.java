package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCaller;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.wallet.button.ButtonConstants;
import com.monri.android.Monri;
import com.monri.android.googlepay.GooglePayButtonOptions;
import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.GooglePayPayment;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.TransactionParams;
import com.monri.android.view.CardMultilineWidget;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GooglePayActivitySimple extends AppCompatActivity implements ViewDelegate {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private OrderRepository orderRepository;
    private Monri monri;
    private ProgressBar progressBar;
    private TextView resultTextView;
    private Button regularPayButton;
    private Button proceedWithGooglePayButton;
    private String paymentId;
    private CardMultilineWidget cardMultilineWidget;

    final CustomerParams testCustomerParams = new CustomerParams()
            .setAddress("Adresa")
            .setFullName("Tester Testerovic")
            .setCity("Sarajevo")
            .setZip("71000")
            .setPhone("+38761000111")
            .setCountry("BA")
            .setEmail("tester+android_sdk@monri.com");

    public static Intent createIntent(final Context context) {
        return new Intent(context, GooglePayActivitySimple.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_google_pay_simple);

        resultTextView = findViewById(R.id.google_pay_result_textview);
        progressBar = findViewById(R.id.progress_bar);
        regularPayButton = findViewById(R.id.payment_picker_pay_google_pay_activity);
        cardMultilineWidget = findViewById(R.id.payment_picker_card_google_pay_activity);
        proceedWithGooglePayButton = findViewById(R.id.proceed_with_google_pay);

        regularPayButton.setOnClickListener(v -> regularConfirmPayment());
        proceedWithGooglePayButton.setOnClickListener(v -> proceedWithGooglePay());

        initializeMonriSdkObjects();
    }

    private void initializeMonriSdkObjects() {
        orderRepository = new OrderRepository(this, this);
        monri = new Monri(((ActivityResultCaller) this), MonriApiOptions.create(orderRepository.authenticityToken(), true));

        createPaymentSession();
    }

    private void createPaymentSession() {
        final Disposable createPaymentResponseObservable = orderRepository.createPayment(false)
                                                                          .subscribeOn(Schedulers.io())
                                                                          .observeOn(AndroidSchedulers.mainThread())
                                                                          .subscribe(this::processCreatePaymentSessionResponse,
                                                                                     this::processCreatePaymentSessionError);
        compositeDisposable.add(createPaymentResponseObservable);
    }

    private void processCreatePaymentSessionResponse(final NewPaymentResponse response) {
        if (Status.APPROVED != response.getStatus()) {
            progressBar.setVisibility(View.GONE);
            resultTextView.setText(getString(R.string.google_pay_activity_create_payment_session_rejected, response.getStatus()));
        } else {
            paymentId = response.getClientSecret();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void processCreatePaymentSessionError(final Throwable error) {
        progressBar.setVisibility(View.GONE);
        resultTextView.setText(getString(R.string.google_pay_activity_create_payment_session_error, error));
    }

    private void regularConfirmPayment() {
        final Card card = cardMultilineWidget.getCard();
        if (card == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.card_is_not_valid), Toast.LENGTH_SHORT).show();
        } else {
            monri.confirmPayment(
                    ConfirmPaymentParams.create(
                    paymentId,
                    card.toPaymentMethodParams(),
                    TransactionParams.create()
                                     .set(testCustomerParams)
                    ),
                    this::onConfirmPaymentResult
            );
        }
    }

    private void proceedWithGooglePay() {
        final GooglePayPayment payment = new GooglePayPayment(GooglePayPayment.Provider.GOOGLE_PAY);

        final ConfirmPaymentParams confirmPaymentParams;
        confirmPaymentParams = ConfirmPaymentParams.create(
                paymentId,
                payment.toPaymentMethodParams(),
                TransactionParams.create().set(testCustomerParams)
        );

        final GooglePayButtonOptions googlePayButtonOptions = new GooglePayButtonOptions(
                ButtonConstants.ButtonType.ORDER,
                ButtonConstants.ButtonTheme.LIGHT,
                5
        );

        monri.confirmPayment(confirmPaymentParams, this::onConfirmPaymentResult, googlePayButtonOptions);
    }

    public void onConfirmPaymentResult(final PaymentResult result, final Throwable cause) {
        final String paymentResultText;

        if (cause != null) {
            paymentResultText = getString(R.string.google_pay_activity_confirm_payment_error, cause);
        } else {
            paymentResultText = getString(R.string.google_pay_activity_confirm_payment_result, result.getStatus(), result.getErrors());
        }

        resultTextView.setText(paymentResultText);
    }

    @Override
    public void statusMessage(String message) {

    }
}
