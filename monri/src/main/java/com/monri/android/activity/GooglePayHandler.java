package com.monri.android.activity;

import android.app.Activity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.button.ButtonOptions;
import com.google.android.gms.wallet.button.PayButton;
import com.google.android.gms.wallet.contract.TaskResultContracts;
import com.monri.android.Monri;
import com.monri.android.ResultCallback;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.GooglePayPayment;
import org.json.JSONException;
import org.json.JSONObject;
import static com.monri.android.activity.GooglePayHandlerException.Error.ERROR_GETTING_PAYMENT_METHOD_FROM_USER;
import static com.monri.android.activity.GooglePayHandlerException.Error.GET_ALLOWED_PAYMENT_METHODS_ERROR;
import static com.monri.android.activity.GooglePayHandlerException.Error.IS_READY_TO_PAY_PAYMENTS_CLIENT_ERROR;
import static com.monri.android.activity.GooglePayHandlerException.Error.NOT_READY_TO_PAY_WITH_GOOGLE_PAY_ERROR;
import static com.monri.android.activity.GooglePayHandlerException.Error.PARSE_PAYMENT_METHOD_DATA_JSON_ERROR;
import static com.monri.android.activity.GooglePayHandlerException.Error.PREPARE_IS_READY_TO_PAY_REQ_ERROR;
import static com.monri.android.activity.GooglePayHandlerException.Error.PREPARE_PAYMENT_DATA_REQUEST_ERROR;
import static com.monri.android.activity.GooglePayHandlerException.Error.START_PAYMENT_SESSION_ERROR;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;

public class GooglePayHandler {

    private final PaymentsClient googlePaymentsClient;
    private ConfirmPaymentParams confirmPaymentParams;
    private final Activity activity;
    private final Monri monri;
    private final MonriGooglePaymentRequestHelper monriGooglePaymentRequestHelper;
    private final GooglePayHandlerCallbacks googlePayHandlerCallbacks;
    private ActivityResultLauncher<Task<PaymentData>> paymentMethodLauncher;
    private final ButtonOptions.Builder buttonOptionsBuilder;

    public GooglePayHandler(
            final ActivityResultCaller paymentMethodActivityResultCaller, final Activity activity, final int walletEnvironment, final Monri monri,
            final ButtonOptions.Builder buttonOptionsBuilder, final GooglePayHandlerCallbacks googlePayHandlerCallbacks
    ) {
        this.monri = monri;
        this.googlePayHandlerCallbacks = googlePayHandlerCallbacks;
        this.activity = activity;
        this.buttonOptionsBuilder = buttonOptionsBuilder;

        initPaymentMethodDataLauncher(paymentMethodActivityResultCaller);

        final Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder().setEnvironment(walletEnvironment).build();
        googlePaymentsClient = Wallet.getPaymentsClient(activity, walletOptions);
        monriGooglePaymentRequestHelper = new MonriGooglePaymentRequestHelper();
    }

    private void initPaymentMethodDataLauncher(final ActivityResultCaller activityResultCaller) {
        this.paymentMethodLauncher = activityResultCaller.registerForActivityResult(new TaskResultContracts.GetPaymentDataResult(), result -> {
            int statusCode = result.getStatus().getStatusCode();

            if (statusCode != CommonStatusCodes.SUCCESS) {
                googlePayHandlerCallbacks.onGooglePayError(new GooglePayHandlerException(ERROR_GETTING_PAYMENT_METHOD_FROM_USER, statusCode));
            } else {
                preparePaymentMethodDataForConfirmPayment(result.getResult());
            }
        });
    }

    public void startGooglePayPayment(final ConfirmPaymentParams confirmPaymentParams) {
        this.confirmPaymentParams = confirmPaymentParams;

        monri.getMonriApi().startGooglePayPayment(confirmPaymentParams.getPaymentId(), new StartGooglePayPaymentResultCallback());
    }

    private void isReadyToPay() {
        try {
            final IsReadyToPayRequest isReadyToPayRequest = monriGooglePaymentRequestHelper.getIsReadyToPayRequest();
            googlePaymentsClient.isReadyToPay(isReadyToPayRequest)
                                .addOnSuccessListener(this::processIsReadyToPayRequest)
                                .addOnFailureListener(e -> googlePayHandlerCallbacks.onGooglePayError(new GooglePayHandlerException(IS_READY_TO_PAY_PAYMENTS_CLIENT_ERROR)));
        } catch (JSONException e) {
            googlePayHandlerCallbacks.onGooglePayError(new GooglePayHandlerException(PREPARE_IS_READY_TO_PAY_REQ_ERROR));
        }
    }

    private void processIsReadyToPayRequest(final Boolean isReadyToPay) {
        if (isReadyToPay) {
            prepareGooglePayButton();
        } else {
            googlePayHandlerCallbacks.onGooglePayError(new GooglePayHandlerException(NOT_READY_TO_PAY_WITH_GOOGLE_PAY_ERROR));
        }
    }

    private void prepareGooglePayButton() {
        try {
            final String allowedPaymentMethods = monriGooglePaymentRequestHelper.getAllowedPaymentMethods().toString();
            final ButtonOptions buttonOptions = buttonOptionsBuilder.setAllowedPaymentMethods(allowedPaymentMethods).build();

            final PayButton payButton = new PayButton(activity);
            payButton.initialize(buttonOptions);
            payButton.setOnClickListener((v) -> requestPayment());

            googlePayHandlerCallbacks.onGooglePayButtonReady(payButton);
        } catch (JSONException e) {
            googlePayHandlerCallbacks.onGooglePayError(new GooglePayHandlerException(GET_ALLOWED_PAYMENT_METHODS_ERROR));
        }
    }

    private void requestPayment() {
        try {
            final PaymentDataRequest paymentDataRequest = monriGooglePaymentRequestHelper.getPaymentDataRequest();

            googlePaymentsClient.loadPaymentData(paymentDataRequest)
                                .addOnCompleteListener(paymentMethodLauncher::launch);
        } catch (JSONException e) {
            googlePayHandlerCallbacks.onGooglePayError(new GooglePayHandlerException(PREPARE_PAYMENT_DATA_REQUEST_ERROR));
        }
    }

    private void preparePaymentMethodDataForConfirmPayment(final PaymentData paymentData) {
        try {
            final JSONObject googlePaymentMethodData = monriGooglePaymentRequestHelper.getPaymentMethodDataFromPaymentData(paymentData);

            final GooglePayPayment googlePayPayment = new GooglePayPayment(GooglePayPayment.Provider.GOOGLE_PAY, googlePaymentMethodData);
            final ConfirmPaymentParams confirmPaymentParams = ConfirmPaymentParams.create(this.confirmPaymentParams.getPaymentId(), googlePayPayment.toPaymentMethodParams(), this.confirmPaymentParams.getTransaction());

            googlePayHandlerCallbacks.onConfirmGooglePayPaymentDataReady(confirmPaymentParams);
        } catch (Exception e) {
            googlePayHandlerCallbacks.onGooglePayError(new GooglePayHandlerException(PARSE_PAYMENT_METHOD_DATA_JSON_ERROR));
        }
    }

    private class StartGooglePayPaymentResultCallback implements ResultCallback<JSONObject> {

        @Override
        public void onSuccess(final JSONObject result) {
            monriGooglePaymentRequestHelper.setMonriGooglePaySessionParameters(result);
            isReadyToPay();
        }

        @Override
        public void onError(final Throwable throwable) {
            googlePayHandlerCallbacks.onGooglePayError(new GooglePayHandlerException(START_PAYMENT_SESSION_ERROR));
        }
    }
}
