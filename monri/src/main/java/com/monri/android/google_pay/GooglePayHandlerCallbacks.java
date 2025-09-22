package com.monri.android.google_pay;

import com.google.android.gms.wallet.button.PayButton;
import com.monri.android.model.PaymentResult;

public interface GooglePayHandlerCallbacks {

    void onNotReadyToPayWithGooglePay();

    void onGooglePayButtonReady(final PayButton payButton);

    void onGetPaymentMethodDataFromUserFailed(final int statusCode);

    void onConfirmPaymentResult(final PaymentResult result, final Throwable cause);

    void onError(final GooglePayHandlerException googlePayHandlerException);
}
