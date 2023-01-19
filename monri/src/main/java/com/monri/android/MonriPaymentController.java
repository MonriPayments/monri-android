package com.monri.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.monri.android.activity.ConfirmPaymentActivity;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentResult;

import java.util.function.Consumer;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
final class MonriPaymentController implements PaymentController {

    private final int PAYMENT_REQUEST_CODE = 10000;
    private final int AUTHENTICATE_PAYMENT_REQUEST_CODE = 10001;

    private final MonriApiOptions monriApiOptions;

    MonriPaymentController(MonriApiOptions monriApiOptions) {
        this.monriApiOptions = monriApiOptions;
    }

    @Override
    public void confirmPayment(Activity activity, ConfirmPaymentParams params) {
        activity.startActivityForResult(ConfirmPaymentActivity.createIntent(activity, params, monriApiOptions), PAYMENT_REQUEST_CODE);
    }

    @Override
    public void confirmPayment(ActivityResultCaller activity, ConfirmPaymentParams params, ActionResultConsumer<PaymentResult> resultCallback) {
        activity.<ConfirmPaymentActivity.Request, ConfirmPaymentActivity.Response>registerForActivityResult(new ActivityResultContract<>() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, ConfirmPaymentActivity.Request input) {
                return ConfirmPaymentActivity.createIntent(context, input);
            }

            @Override
            public ConfirmPaymentActivity.Response parseResult(int resultCode, @Nullable Intent intent) {
                return ConfirmPaymentActivity.parseResponse(resultCode, intent);
            }
        }, result -> {
            resultCallback.accept(new ActionResult<>(result.getPaymentResult(), null));
        }).launch(new ConfirmPaymentActivity.Request(params, monriApiOptions));
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

}
