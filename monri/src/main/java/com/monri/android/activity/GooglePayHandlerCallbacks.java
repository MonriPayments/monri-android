package com.monri.android.activity;

import com.google.android.gms.wallet.button.PayButton;
import com.monri.android.model.ConfirmPaymentParams;

public interface GooglePayHandlerCallbacks {

    void onGooglePayButtonReady(PayButton payButton);

    void onConfirmGooglePayPaymentDataReady(ConfirmPaymentParams confirmPaymentParams);

    void onGooglePayError(GooglePayHandlerException googlePayHandlerException);
}
