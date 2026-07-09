package com.monri.android;

import android.app.Activity;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import com.monri.android.activity.ConfirmPaymentActivity;
import com.monri.android.googlepay.GooglePayButtonOptions;
import com.monri.android.logger.MonriLogger;
import com.monri.android.logger.MonriLoggerFactory;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentResult;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
final class MonriPaymentController implements PaymentController {

    private static final MonriLogger logger = MonriLoggerFactory.get("MonriPaymentController");

    private static final String NO_CALLBACK_ATTACHED_WARNING = "PaymentResult received but no confirmPayment callback is attached. " +
            "Use Monri#setPaymentResultConsumer to receive results after activity recreation.";

    private final int PAYMENT_REQUEST_CODE = 10000;
    private final int AUTHENTICATE_PAYMENT_REQUEST_CODE = 10001;

    private final MonriApiOptions monriApiOptions;
    private final ActivityResultLauncher<ConfirmPaymentActivity.Request> registeredForActivityResult;
    ActionResultConsumer<PaymentResult> delegatedCallback;

    MonriPaymentController(MonriApiOptions monriApiOptions) {
        this.monriApiOptions = monriApiOptions;
        registeredForActivityResult = null;
    }

    MonriPaymentController(MonriApiOptions monriApiOptions, ActivityResultLauncher<ConfirmPaymentActivity.Request> registeredForActivityResult) {
        this.monriApiOptions = monriApiOptions;
        this.registeredForActivityResult = registeredForActivityResult;
    }

    /**
     * @deprecated use {@link #confirmPayment(ConfirmPaymentParams, ActionResultConsumer)}
     */
    @Override
    public void confirmPayment(Activity activity, ConfirmPaymentParams params) {
        activity.startActivityForResult(ConfirmPaymentActivity.createIntent(activity, params, monriApiOptions), PAYMENT_REQUEST_CODE);
    }

    @Override
    public void confirmPayment(ConfirmPaymentParams params, ActionResultConsumer<PaymentResult> resultCallback) {
        if (registeredForActivityResult == null) {
            throw new NullPointerException("In Monri constructor you didn't provide activityResultCaller, registeredForActivityResult in null.");
        }
        this.delegatedCallback = resultCallback;
        registeredForActivityResult.launch(new ConfirmPaymentActivity.Request(params, monriApiOptions));
    }

    @Override
    public void confirmPayment(final ConfirmPaymentParams params, final ActionResultConsumer<PaymentResult> resultCallback,
                               final GooglePayButtonOptions googlePayButtonOptions) {
        if (registeredForActivityResult == null) {
            throw new NullPointerException("In Monri constructor you didn't provide activityResultCaller, registeredForActivityResult in null.");
        }
        this.delegatedCallback = resultCallback;
        registeredForActivityResult.launch(new ConfirmPaymentActivity.Request(params, monriApiOptions, googlePayButtonOptions));
    }

    @Override
    public boolean shouldHandlePaymentResult(int requestCode, Intent data) {
        return (PAYMENT_REQUEST_CODE == requestCode || requestCode == AUTHENTICATE_PAYMENT_REQUEST_CODE) && data != null;
    }

    @Override
    public void handlePaymentResult(int requestCode, Intent data, ResultCallback<PaymentResult> callback) {

        final PaymentResult paymentResult = data.getParcelableExtra(PaymentResult.BUNDLE_NAME);

        if (paymentResult == null) {
            callback.onError(new IllegalStateException("PaymentResult == null, contact support@monri.com for more details"));
        } else {
            switch (paymentResult.getStatus()) {
                case "approved":
                case "declined":
                    callback.onSuccess(paymentResult);
                    break;
                default:
                    callback.onError(new IllegalStateException(String.format("PaymentResult with unsupported status '%s' should not be invoked in this phase. Contact support@monri.com for more details", paymentResult.getStatus())));
            }
        }

    }

    @Override
    public void acceptResult(final PaymentResult result, final Throwable throwable) {
        final ActionResultConsumer<PaymentResult> callback = delegatedCallback;
        delegatedCallback = null;

        if (callback == null) {
            logger.warn(NO_CALLBACK_ATTACHED_WARNING);
        } else {
            callback.accept(result, throwable);
        }
    }

    @Override
    public boolean hasResultConsumer() {
        return delegatedCallback != null;
    }
}
