package com.monri.android;

import android.app.Activity;
import android.content.Intent;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.PaymentResult;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public interface PaymentController {
    void confirmPayment(Activity activity, ConfirmPaymentParams params);

    void handleActionRequired(Activity activity, String paymentId);

    boolean shouldHandlePaymentResult(int requestCode, Intent data);

    void handlePaymentResult(int requestCode, Intent data, ResultCallback<PaymentResult> callback);
}
