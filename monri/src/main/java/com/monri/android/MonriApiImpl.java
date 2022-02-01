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
                () -> {
                    MonriHttpResult<ConfirmPaymentResponse> result = monriHttpApi.confirmPayment(params);
                    if (result.getCause() != null) {
                        throw result.getCause();
                    } else {
                        return result.getResult();
                    }
                },
                callback);
    }

    @Override
    public void paymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback) {
        taskRunner.executeAsync(
                () -> {
                    MonriHttpResult<PaymentStatusResponse> result = monriHttpApi.paymentStatus(params.getClientSecret());
                    if (result.getCause() != null) {
                        throw result.getCause();
                    } else {
                        return result.getResult();
                    }
                },
                callback
        );
    }

}
