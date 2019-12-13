package com.monri.android.flows;

import android.app.Activity;
import android.content.Intent;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.PaymentStatus;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public class PaymentErrorFlowImpl implements PaymentErrorFlow {

    private final Activity activity;
    private final ConfirmPaymentParams paymentParams;

    public PaymentErrorFlowImpl(Activity activity, ConfirmPaymentParams paymentParams) {
        this.activity = activity;
        this.paymentParams = paymentParams;
    }

    @Override
    public void handleResult(Throwable throwable) {
        Intent intent = new Intent();
        PaymentResult paymentResult = new PaymentResult(PaymentStatus.DECLINED.getStatus());
        intent.putExtra(PaymentResult.BUNDLE_NAME, paymentResult);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }
}
