package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCaller;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.button.ButtonConstants;
import com.google.android.gms.wallet.button.ButtonOptions;
import com.google.android.gms.wallet.button.PayButton;
import com.monri.android.Monri;
import com.monri.android.google_pay.GooglePayHandler;
import com.monri.android.google_pay.GooglePayHandlerCallbacks;
import com.monri.android.google_pay.GooglePayHandlerException;
import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.TransactionParams;
import com.monri.android.view.CardMultilineWidget;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GooglePayActivitySimple extends AppCompatActivity implements ViewDelegate, GooglePayHandlerCallbacks {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private OrderRepository orderRepository;
    private Monri monri;
    private ProgressBar progressBar;
    private TextView resultTextView;
    private GooglePayHandler googlePayHandler;
    private Button regularPayButton;
    private LinearLayout googlePayButtonContainer;
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
        googlePayButtonContainer = findViewById(R.id.google_pay_button_container);

        regularPayButton.setOnClickListener(v -> regularConfirmPayment());

        initializeMonriSdkObjects();
    }

    private void initializeMonriSdkObjects() {
        orderRepository = new OrderRepository(this, this);
        monri = new Monri(((ActivityResultCaller) this), MonriApiOptions.create(orderRepository.authenticityToken(), true));

        final ButtonOptions.Builder buttonOptionsBuilder = ButtonOptions.newBuilder()
                                                                        .setButtonTheme(ButtonConstants.ButtonTheme.LIGHT)
                                                                        .setButtonType(ButtonConstants.ButtonType.DONATE)
                                                                        .setCornerRadius(5);
        googlePayHandler = new GooglePayHandler(
                this,
                this,
                WalletConstants.ENVIRONMENT_TEST,
                monri,
                this,
                buttonOptionsBuilder
        );

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
        if (!OrderRepository.CREATE_PAYMENT_SESSION_APPROVED.equals(response.getStatus())) {
            progressBar.setVisibility(View.GONE);
            resultTextView.setText(getString(R.string.google_pay_activity_create_payment_session_rejected, response.getStatus()));
        } else {
            paymentId = response.getClientSecret();
            startGooglePayPayment(response.getClientSecret());
        }
    }

    private void startGooglePayPayment(final String clientSecret) {
        googlePayHandler.startGooglePayPayment(TransactionParams.create().set(testCustomerParams), clientSecret);
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


    @Override
    public void onError(final GooglePayHandlerException googlePayHandlerException) {
        resultTextView.setText(getString(R.string.google_pay_activity_google_pay_handler_exception, googlePayHandlerException.getErrorCode()));
    }

    @Override
    public void onNotReadyToPayWithGooglePay() {
        resultTextView.setText(getString(R.string.google_pay_activity_on_not_ready_to_pay_with_google_pay_called));
    }

    @Override
    public void onGooglePayButtonReady(final PayButton payButton) {
        progressBar.setVisibility(View.GONE);

        googlePayButtonContainer.addView(payButton);
    }

    @Override
    public void onGetPaymentMethodDataFromUserFailed(final int statusCode) {
        resultTextView.setText(getString(R.string.google_pay_activity_failed_to_get_payment_method_data_from_user, statusCode));
    }

    @Override
    public void onConfirmPaymentResult(PaymentResult result, Throwable cause) {
        final String paymentResultText;

        if (cause != null) {
            paymentResultText = getString(R.string.google_pay_activity_confirm_payment_error, cause);
        } else {
            paymentResultText = getString(R.string.google_pay_activity_confirm_payment_result, result.getStatus());
        }

        resultTextView.setText(paymentResultText);
    }

    @Override
    public void statusMessage(String message) {

    }
}
