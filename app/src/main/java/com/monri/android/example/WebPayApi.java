package com.monri.android.example;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface WebPayApi {

    @POST("v2/payment/new")
    Single<NewPaymentResponse> createPaymentSession(
            @Header("Content-Type") String type,
            @Header("Authorization") String authorization,
            @Body NewPaymentRequest newPaymentRequest
    );
}