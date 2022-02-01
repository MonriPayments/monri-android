package com.monri.android;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
class MonriApiImpl implements MonriApi {

    private final MonriHttpApi monriHttpApi;
    private final TaskRunner taskRunner;

    MonriApiImpl(final MonriHttpApi monriHttpApi) {
        this.monriHttpApi = monriHttpApi;
        this.taskRunner = new TaskRunner();
    }

    @Override
    public void confirmPayment(ConfirmPaymentParams params, ResultCallback<ConfirmPaymentResponse> callback) {
        taskRunner.executeAsync(
                () -> monriHttpApi.confirmPayment(params),
                result -> callback.onSuccess(result.getResult()),
                result -> callback.onError(result.getCause())
        );
    }

    @Override
    public void paymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback) {
        taskRunner.executeAsync(
                () -> monriHttpApi.paymentStatus(params.getClientSecret()),
                result -> callback.onSuccess(result.getResult()),
                result -> callback.onError(result.getCause())
        );
    }

}
