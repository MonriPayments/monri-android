package com.monri.android.http;

import android.os.AsyncTask;

import com.monri.android.model.PaymentStatusResponse;

import java.lang.ref.WeakReference;

public class MonriHttpAsyncTask extends AsyncTask<String, Void, MonriAsyncTaskResult> {

    private final WeakReference<MonriHttpCallback> callback;
    //private final EncryptRequest request;
    private final MonriHttpApi monriHttpApi;


    public MonriHttpAsyncTask(MonriHttpCallback callback, MonriHttpApi monriHttpApi) {
        this.callback = new WeakReference<>(callback);
//            this.request = request;
        this.monriHttpApi = monriHttpApi;
    }

    @Override
    protected MonriAsyncTaskResult doInBackground(String... strings) {

        try {

            final MonriHttpResult<PaymentStatusResponse> statusResult = monriHttpApi.paymentStatus(strings[0]);

            if (statusResult.getCause() != null) {
                return MonriAsyncTaskResult.failed(statusResult.getCause());
            }

            final PaymentStatusResponse paymentStatusResponse = statusResult.getResult();

            return MonriAsyncTaskResult.success(paymentStatusResponse);
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
