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
    void confirmPayment(Activity activity, ConfirmPaymentParams params);
    void confirmPayment(ActivityResultCaller activity, ConfirmPaymentParams params, ActionResultConsumer<PaymentResult> resultCallback);

    boolean shouldHandlePaymentResult(int requestCode, Intent data);

    void handlePaymentResult(int requestCode, Intent data, ResultCallback<PaymentResult> callback);
    void acceptResult(ActionResult<PaymentResult> result);
}
