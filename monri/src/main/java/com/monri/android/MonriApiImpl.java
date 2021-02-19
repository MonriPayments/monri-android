package com.monri.android;

import com.monri.android.http.MonriHttpApi;
import com.monri.android.http.MonriHttpAsyncTask;
import com.monri.android.http.MonriHttpCallback;
import com.monri.android.http.MonriHttpException;
import com.monri.android.http.MonriHttpRequest;
import com.monri.android.http.MonriHttpResult;
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

    MonriApiImpl(final MonriHttpApi monriHttpApi) {
        this.monriHttpApi = monriHttpApi;
    }

    @Override
    public void confirmPayment(ConfirmPaymentParams params, ResultCallback<ConfirmPaymentResponse> callback) {
        try {
            MonriHttpAsyncTask monriHttpAsyncTask = new MonriHttpAsyncTask(new MonriHttpCallback() {
                @Override
                public void onSuccess(final MonriHttpResult result) {
                    callback.onSuccess((ConfirmPaymentResponse) result.getResult());

                }

                @Override
                public void onError(final MonriHttpException error) {
                    callback.onError(error.getCause());

                }
            }, monriHttpApi);

            monriHttpAsyncTask.execute(new MonriHttpRequest<>(MonriHttpRequest.HttpCallType.CONFIRM_PAYMENT, params));

        } catch (Exception e) {
            callback.onError(e);
        }

    }

    @Override
    public void paymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback) {
        try {
            MonriHttpAsyncTask monriHttpAsyncTask = new MonriHttpAsyncTask(new MonriHttpCallback() {
                @Override
                public void onSuccess(final MonriHttpResult result) {
                    callback.onSuccess((PaymentStatusResponse) result.getResult());

                }

                @Override
                public void onError(final MonriHttpException error) {
                    callback.onError(error.getCause());

                }
            }, monriHttpApi);

            monriHttpAsyncTask.execute(new MonriHttpRequest<>(MonriHttpRequest.HttpCallType.PAYMENT_STATUS, params));

        } catch (Exception e) {
            callback.onError(e);
        }
    }

}
