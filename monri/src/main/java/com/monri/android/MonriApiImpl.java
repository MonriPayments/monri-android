package com.monri.android;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
class MonriApiImpl implements MonriApi {

    private final MonriRetrofitApi monriRetrofitApi;
    private final ObjectMapper objectMapper;
    private final MonriHttpApi monriHttpApi;

    MonriApiImpl(MonriRetrofitApi monriRetrofitApi, ObjectMapper objectMapper, final MonriHttpApi monriHttpApi) {
        this.monriRetrofitApi = monriRetrofitApi;
        this.objectMapper = objectMapper;
        this.monriHttpApi = monriHttpApi;
    }

    @Override
    public void confirmPayment(ConfirmPaymentParams params, ResultCallback<ConfirmPaymentResponse> callback) {

        try {
//            monriRetrofitApi.confirmPayment(params.getPaymentId(), params).enqueue(new Callback<ConfirmPaymentResponse>() {
//                @Override
//                public void onResponse(@NonNull Call<ConfirmPaymentResponse> call,
//                                       @NonNull Response<ConfirmPaymentResponse> response) {
//                    responseHandler(response, callback);
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<ConfirmPaymentResponse> call, @NonNull Throwable t) {
//                    callback.onError(t);
//                }
//            });


            //HTTP request
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

//    private <T> void responseHandler(Response<T> response, ResultCallback<T> callback) {
//        if (response.isSuccessful()) {
//            callback.onSuccess(response.body());
//        } else if (422 == response.code()) {
//            try {
//                String string = response.errorBody().string();
//                ApiException apiException = objectMapper.readValue(string, ApiException.class);
//                callback.onError(apiException);
//            } catch (IOException e) {
//                callback.onError(e);
//            }
//        } else {
//            // TODO: log this case
//            callback.onError(new ApiException(Collections.singletonList("Payment failed")));
//        }
//    }

    @Override
    public void paymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback) {
        try {
//            monriRetrofitApi.paymentStatus(params.getClientSecret())
//                    .enqueue(new Callback<PaymentStatusResponse>() {
//                        @Override
//                        public void onResponse(@NonNull Call<PaymentStatusResponse> call, @NonNull Response<PaymentStatusResponse> response) {
//                            responseHandler(response, callback);
//                        }
//
//                        @Override
//                        public void onFailure(@NonNull Call<PaymentStatusResponse> call, Throwable t) {
//                            callback.onError(t);
//                        }
//                    });

            //HTTP request
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
