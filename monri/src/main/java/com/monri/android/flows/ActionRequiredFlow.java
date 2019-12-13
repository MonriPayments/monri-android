package com.monri.android.flows;

import com.monri.android.model.ConfirmPaymentResponse;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public interface ActionRequiredFlow {
    void handleResult(ConfirmPaymentResponse confirmPaymentResponse);
}
