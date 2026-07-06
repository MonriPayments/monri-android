package com.monri.android;

import android.app.Activity;
import android.content.Intent;
import com.monri.android.googlepay.GooglePayButtonOptions;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.PaymentResult;
/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public interface PaymentController {
    /**
     * @deprecated use {@link #confirmPayment(ConfirmPaymentParams, ActionResultConsumer)}
     */
    @Deprecated
    void confirmPayment(Activity activity, ConfirmPaymentParams params);
    void confirmPayment(ConfirmPaymentParams params, ActionResultConsumer<PaymentResult> resultCallback);
    void confirmPayment(ConfirmPaymentParams params, ActionResultConsumer<PaymentResult> resultCallback, GooglePayButtonOptions googlePayButtonOptions);

    @Deprecated
    boolean shouldHandlePaymentResult(int requestCode, Intent data);

    @Deprecated
    void handlePaymentResult(int requestCode, Intent data, ResultCallback<PaymentResult> callback);

    void acceptResult(PaymentResult result, Throwable throwable);

    default boolean hasResultConsumer() {
        return false;
    }
}
