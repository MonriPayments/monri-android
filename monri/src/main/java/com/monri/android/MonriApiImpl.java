package com.monri.android;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;

import java.io.IOException;
import java.util.Arrays;
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

    MonriApiImpl(MonriRetrofitApi monriRetrofitApi, ObjectMapper objectMapper) {
        this.monriRetrofitApi = monriRetrofitApi;
        this.objectMapper = objectMapper;
    }

    @Override
    public void confirmPayment(ConfirmPaymentParams params, ResultCallback<ConfirmPaymentResponse> callback) {

        try {
            monriRetrofitApi.confirmPayment(params.getPaymentId(), params).enqueue(new Callback<ConfirmPaymentResponse>() {
                @Override
                public void onResponse(@NonNull Call<ConfirmPaymentResponse> call,
                                       @NonNull Response<ConfirmPaymentResponse> response) {
                    responseHandler(response, callback);
                }

                @Override
                public void onFailure(@NonNull Call<ConfirmPaymentResponse> call, @NonNull Throwable t) {
                    callback.onError(t);
                }
            });
        } catch (Exception e) {
            callback.onError(e);
        }


    }

    private <T> void responseHandler(Response<T> response, ResultCallback<T> callback) {
        if (response.isSuccessful()) {
            callback.onSuccess(response.body());
        } else if (422 == response.code()) {
            try {
                String string = response.errorBody().string();
                ApiException apiException = objectMapper.readValue(string, ApiException.class);
                callback.onError(apiException);
            } catch (IOException e) {
                callback.onError(e);
            }
        } else {
            // TODO: log this case
            callback.onError(new ApiException(Collections.singletonList("Payment failed")));
        }
    }

    @Override
    public void paymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback) {
        try {
            monriRetrofitApi.paymentStatus(params.getClientSecret())
                    .enqueue(new Callback<PaymentStatusResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<PaymentStatusResponse> call, @NonNull Response<PaymentStatusResponse> response) {
                            responseHandler(response, callback);
                        }

                        @Override
                        public void onFailure(@NonNull Call<PaymentStatusResponse> call, Throwable t) {
                            callback.onError(t);
                        }
                    });
        } catch (Exception e) {
            callback.onError(e);
        }
    }


}
