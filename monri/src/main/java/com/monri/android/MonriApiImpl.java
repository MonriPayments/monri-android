package com.monri.android;

import androidx.annotation.NonNull;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
class MonriApiImpl implements MonriApi {

    private final MonriRetrofitApi monriRetrofitApi;

    MonriApiImpl(MonriRetrofitApi monriRetrofitApi) {
        this.monriRetrofitApi = monriRetrofitApi;
    }

    @Override
    public void confirmPayment(ConfirmPaymentParams params, ResultCallback<ConfirmPaymentResponse> callback) {

        try {
            monriRetrofitApi.confirmPayment(params.getPaymentId(), params).enqueue(new Callback<ConfirmPaymentResponse>() {
                @Override
                public void onResponse(@NonNull Call<ConfirmPaymentResponse> call,
                                       @NonNull Response<ConfirmPaymentResponse> response) {
                    callback.onSuccess(response.body());
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

    @Override
    public void paymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback) {
        try {
            monriRetrofitApi.paymentStatus(params.getClientSecret())
                    .enqueue(new Callback<PaymentStatusResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<PaymentStatusResponse> call, @NonNull Response<PaymentStatusResponse> response) {

                            callback.onSuccess(response.body());
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
