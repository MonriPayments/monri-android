package com.monri.android.activity;

import com.google.android.gms.wallet.button.PayButton;
import com.monri.android.model.PaymentResult;

public interface GooglePayHandlerCallbacks {

    void onNotReadyToPayWithGooglePay();

    void onGooglePayButtonReady(PayButton payButton);

    void onGetPaymentMethodDataFromUserFailed(int statusCode);

    void onConfirmPaymentResult(PaymentResult result, Throwable cause);

    void onError(GooglePayHandlerException googlePayHandlerException);
}
