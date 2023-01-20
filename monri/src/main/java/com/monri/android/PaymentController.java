package com.monri.android;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultCaller;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.PaymentResult;

import java.util.function.Consumer;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public interface PaymentController {
    /**
     * @deprecated use {@link #confirmPayment(ActivityResultCaller, ConfirmPaymentParams, ActionResultConsumer)}
     */
    void confirmPayment(Activity activity, ConfirmPaymentParams params);
    void confirmPayment(ActivityResultCaller activity, ConfirmPaymentParams params, ActionResultConsumer<PaymentResult> resultCallback);

    @Deprecated
    boolean shouldHandlePaymentResult(int requestCode, Intent data);

    @Deprecated
    void handlePaymentResult(int requestCode, Intent data, ResultCallback<PaymentResult> callback);

    void acceptResult(PaymentResult result, Throwable throwable);
}
