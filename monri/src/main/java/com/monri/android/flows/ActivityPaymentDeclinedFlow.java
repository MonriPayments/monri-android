package com.monri.android.flows;

import com.monri.android.activity.UiDelegate;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentResult;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public class ActivityPaymentDeclinedFlow implements PaymentDeclinedFlow {

    private final UiDelegate uiDelegate;

    public ActivityPaymentDeclinedFlow(final UiDelegate uiDelegate) {
        this.uiDelegate = uiDelegate;
    }

    @Override
    public void handleResult(ConfirmPaymentResponse response) {
        uiDelegate.hideLoading();
        uiDelegate.makeWebViewGone();

        uiDelegate.handlePaymentResult(new PaymentResult(response.getStatus().getStatus()));
    }
}
