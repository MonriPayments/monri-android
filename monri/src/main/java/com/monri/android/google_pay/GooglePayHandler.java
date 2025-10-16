package com.monri.android.google_pay;

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
import com.monri.android.model.TransactionParams;
import org.json.JSONException;
import org.json.JSONObject;
import static com.monri.android.google_pay.GooglePayHandlerException.Error.GET_ALLOWED_PAYMENT_METHODS_ERROR;
import static com.monri.android.google_pay.GooglePayHandlerException.Error.IS_READY_TO_PAY_PAYMENTS_CLIENT_ERROR;
import static com.monri.android.google_pay.GooglePayHandlerException.Error.PARSE_PAYMENT_METHOD_DATA_JSON_ERROR;
import static com.monri.android.google_pay.GooglePayHandlerException.Error.PREPARE_IS_READY_TO_PAY_REQ_ERROR;
import static com.monri.android.google_pay.GooglePayHandlerException.Error.PREPARE_PAYMENT_DATA_REQUEST_ERROR;
import static com.monri.android.google_pay.GooglePayHandlerException.Error.START_PAYMENT_SESSION_ERROR;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;


public class GooglePayHandler {

    private PaymentsClient googlePaymentsClient;
    private TransactionParams transactionParams;
    private String paymentId;
    private Activity activity;
    private Monri monri;
    private MonriGooglePaymentRequestHelper monriGooglePaymentRequestHelper;
    private GooglePayHandlerCallbacks googlePayHandlerCallbacks;
    private final ActivityResultCaller activityResultCaller;
    private ActivityResultLauncher<Task<PaymentData>> paymentMethodLauncher;
    private final ButtonOptions.Builder buttonOptionsBuilder;

    public GooglePayHandler(
            final ActivityResultCaller paymentMethodActivityResultCaller, final Activity activity, final int walletEnvironment, final Monri monri,
            final GooglePayHandlerCallbacks googlePayHandlerCallbacks, final ButtonOptions.Builder buttonOptionsBuilder
    ) {
        this.monri = monri;
        this.googlePayHandlerCallbacks = googlePayHandlerCallbacks;
        this.activity = activity;
        this.activityResultCaller = paymentMethodActivityResultCaller;
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
                googlePayHandlerCallbacks.onGetPaymentMethodDataFromUserFailed(statusCode);
            } else {
                preparePaymentMethodDataForConfirmPayment(result.getResult());
            }
        });
    }

    public void startGooglePayPayment(final TransactionParams transactionParams, final String paymentId) {
        this.transactionParams = transactionParams;
        this.paymentId = paymentId;

        monri.getMonriApi().startGooglePayPayment(paymentId, new StartGooglePayPaymentResultCallback());
    }

    private void isReadyToPay() {
        try {
            final IsReadyToPayRequest isReadyToPayRequest = monriGooglePaymentRequestHelper.getIsReadyToPayRequest();
            googlePaymentsClient.isReadyToPay(isReadyToPayRequest)
                                .addOnSuccessListener(this::processIsReadyToPayRequest)
                                .addOnFailureListener(e -> googlePayHandlerCallbacks.onError(new GooglePayHandlerException(IS_READY_TO_PAY_PAYMENTS_CLIENT_ERROR)));
        } catch (JSONException e) {
            googlePayHandlerCallbacks.onError(new GooglePayHandlerException(PREPARE_IS_READY_TO_PAY_REQ_ERROR));
        }
    }

    private void processIsReadyToPayRequest(final Boolean isReadyToPay) {
        if (isReadyToPay) {
            prepareGooglePayButton();
        } else {
            googlePayHandlerCallbacks.onNotReadyToPayWithGooglePay();
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
            googlePayHandlerCallbacks.onError(new GooglePayHandlerException(GET_ALLOWED_PAYMENT_METHODS_ERROR));
        }
    }

    private void requestPayment() {
        try {
            final PaymentDataRequest paymentDataRequest = monriGooglePaymentRequestHelper.getPaymentDataRequest();

            googlePaymentsClient.loadPaymentData(paymentDataRequest)
                                .addOnCompleteListener(paymentMethodLauncher::launch);
        } catch (JSONException e) {
            googlePayHandlerCallbacks.onError(new GooglePayHandlerException(PREPARE_PAYMENT_DATA_REQUEST_ERROR));
        }
    }

    private void preparePaymentMethodDataForConfirmPayment(final PaymentData paymentData) {
        try {
            final JSONObject googlePaymentMethodData = monriGooglePaymentRequestHelper.getPaymentMethodDataFromPaymentData(paymentData);

            confirmGooglePayPayment(googlePaymentMethodData);
        } catch (Exception e) {
            googlePayHandlerCallbacks.onError(new GooglePayHandlerException(PARSE_PAYMENT_METHOD_DATA_JSON_ERROR));
        }
    }

    private void confirmGooglePayPayment(final JSONObject googlePaymentMethodData) {
        final GooglePayPayment googlePayPayment = new GooglePayPayment(GooglePayPayment.Provider.GOOGLE_PAY, googlePaymentMethodData);
        final ConfirmPaymentParams confirmPaymentParams = ConfirmPaymentParams.create(paymentId, googlePayPayment.toPaymentMethodParams(), transactionParams);

        monri.confirmPayment(confirmPaymentParams, googlePayHandlerCallbacks::onConfirmPaymentResult);
    }

    private class StartGooglePayPaymentResultCallback implements ResultCallback<JSONObject> {
        @Override
        public void onSuccess(final JSONObject result) {
            monriGooglePaymentRequestHelper.setMonriGooglePaySessionParameters(result);
            isReadyToPay();
        }

        @Override
        public void onError(final Throwable throwable) {
            googlePayHandlerCallbacks.onError(new GooglePayHandlerException(START_PAYMENT_SESSION_ERROR));
        }
    }
}
