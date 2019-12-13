package com.monri.android.example;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by jasminsuljic on 2019-10-30.
 * MonriAndroid
 */
public interface ExampleApi {
    @POST("example/order")
    Single<OrderResponse> order(@Body OrderRequest orderRequest);

    @POST("example/prepare-transaction")
    Single<PrepareTransactionResponse> prepareTransaction();

    @POST("example/create-payment-session")
    Single<NewPaymentResponse> createPaymentSession(@Body NewPaymentRequest request);
}
