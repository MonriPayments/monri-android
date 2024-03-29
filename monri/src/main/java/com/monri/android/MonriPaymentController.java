package com.monri.android;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import com.monri.android.activity.ConfirmPaymentActivity;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentResult;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
final class MonriPaymentController implements PaymentController {

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
        if(registeredForActivityResult == null){
            throw new NullPointerException("In Monri constructor you didn't provide activityResultCaller, registeredForActivityResult in null.");
        }
        this.delegatedCallback = resultCallback;
        registeredForActivityResult.launch(new ConfirmPaymentActivity.Request(params, monriApiOptions));
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
    public void acceptResult(PaymentResult result, Throwable throwable) {
        if (delegatedCallback == null) {
             throw new NullPointerException("delegatedCallback is null");
        } else {
            delegatedCallback.accept(result, throwable);
        }
    }
}
