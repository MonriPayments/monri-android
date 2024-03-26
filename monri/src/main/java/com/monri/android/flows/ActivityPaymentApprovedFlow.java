package com.monri.android.flows;

import com.monri.android.activity.UiDelegate;
import com.monri.android.model.ConfirmPaymentResponse;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public class ActivityPaymentApprovedFlow implements PaymentApprovedFlow {

    final UiDelegate uiDelegate;

    public ActivityPaymentApprovedFlow(final UiDelegate uiDelegate) {
        this.uiDelegate = uiDelegate;
    }

    @Override
    public void handleResult(ConfirmPaymentResponse response) {
        uiDelegate.hideLoading();
        uiDelegate.makeWebViewGone();

        uiDelegate.handlePaymentResult(response.getPaymentResult());
    }
}
