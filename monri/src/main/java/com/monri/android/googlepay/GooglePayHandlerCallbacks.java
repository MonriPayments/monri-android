package com.monri.android.googlepay;

import com.google.android.gms.wallet.button.PayButton;
import com.monri.android.model.ConfirmPaymentParams;

public interface GooglePayHandlerCallbacks {

    void onGooglePayButtonReady(PayButton payButton);

    void onConfirmGooglePayPaymentDataReady(ConfirmPaymentParams googlePayConfirmPaymentParams);

    void onGooglePayError(GooglePayHandlerException googlePayHandlerException);
}
