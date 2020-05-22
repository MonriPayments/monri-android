package com.monri.android.http;

import android.os.AsyncTask;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;

import java.lang.ref.WeakReference;

public class MonriHttpAsyncTask extends AsyncTask<MonriHttpRequest, Void, MonriAsyncTaskResult> {

    private final WeakReference<MonriHttpCallback> callback;
    private final MonriHttpApi monriHttpApi;


    public MonriHttpAsyncTask(MonriHttpCallback callback, MonriHttpApi monriHttpApi) {
        this.callback = new WeakReference<>(callback);
        this.monriHttpApi = monriHttpApi;
    }

    @Override
    protected MonriAsyncTaskResult doInBackground(MonriHttpRequest... requests) {

        try {

            final MonriHttpRequest request = requests[0];

            switch (request.getHttpCallType()){
                case PAYMENT_STATUS:

                    PaymentStatusParams statusParams = (PaymentStatusParams) request.getRequestData();

                    final MonriHttpResult<PaymentStatusResponse> statusResult
                            = monriHttpApi.paymentStatus(statusParams.getClientSecret());
                    if (statusResult.getCause() != null) {
                        return MonriAsyncTaskResult.failed(statusResult.getCause());
                    }

                    final PaymentStatusResponse paymentStatusResponse = statusResult.getResult();
                    return MonriAsyncTaskResult.success(MonriHttpResult.success(paymentStatusResponse));

                case CONFIRM_PAYMENT:
                    ConfirmPaymentParams paymentParams = (ConfirmPaymentParams) request.getRequestData();

                    final MonriHttpResult<ConfirmPaymentResponse> confirmPaymentResult
                            = monriHttpApi.confirmPayment(paymentParams.getPaymentId(), paymentParams);
                    if (confirmPaymentResult.getCause() != null) {
                        return MonriAsyncTaskResult.failed(confirmPaymentResult.getCause());
                    }

                    final ConfirmPaymentResponse paymentStatusResponse1 = confirmPaymentResult.getResult();
                    return MonriAsyncTaskResult.success(MonriHttpResult.success(paymentStatusResponse1));

                default: throw new IllegalArgumentException("Call type is not supported");
            }


        } catch (Exception e) {
            return MonriAsyncTaskResult.failed(e, MonriHttpExceptionCode.UNKNOWN_EXCEPTION);
        }
    }

    @Override
    protected void onPostExecute(MonriAsyncTaskResult result) {
        if (result.getResult() != null) {
            callback.get().onSuccess((MonriHttpResult) result.getResult());
        } else {
            callback.get().onError(result.getException());
        }
    }


}
