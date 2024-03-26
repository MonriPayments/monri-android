package com.monri.android.activity.fake;

import com.monri.android.CustomerApi;
import com.monri.android.MonriApi;
import com.monri.android.ResultCallback;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;

/**
 * Fake MonriApi that can return expected values.
 * */
public final class FakeMonriApi implements MonriApi {

    private final PaymentStatusHandler paymentStatusHandler;

    private int statusCheckInvokedCount;

    public FakeMonriApi(final PaymentStatusHandler paymentStatusHandler) {
        this.paymentStatusHandler = paymentStatusHandler;
    }

    @Override
    public void confirmPayment(ConfirmPaymentParams params, ResultCallback<ConfirmPaymentResponse> callback) {
        /* no-op */
    }

    @Override
    public void paymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback) {
        statusCheckInvokedCount++;

        if (paymentStatusHandler != null) {
            paymentStatusHandler.onCheckPaymentStatus(params, callback);
        }
    }

    @Override
    public CustomerApi customers() {
        return null;
    }

    public int getStatusCheckInvokedCount() {
        return statusCheckInvokedCount;
    }

    public interface PaymentStatusHandler {
        void onCheckPaymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback);
    }
}
