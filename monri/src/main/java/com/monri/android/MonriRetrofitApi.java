package com.monri.android;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentStatusResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
interface MonriRetrofitApi {

    @POST("v2/payment/{id}/confirm")
    Call<ConfirmPaymentResponse> confirmPayment(@Path("id") String id, @Body ConfirmPaymentParams params);

    @GET("v2/payment/{id}/status")
    Call<PaymentStatusResponse> paymentStatus(@Path("id") String id);
}
