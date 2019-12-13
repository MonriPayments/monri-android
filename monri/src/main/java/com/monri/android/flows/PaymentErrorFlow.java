package com.monri.android.flows;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public interface PaymentErrorFlow {
    void handleResult(Throwable throwable);
}
