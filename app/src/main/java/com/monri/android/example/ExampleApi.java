package com.monri.android.example;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by jasminsuljic on 2019-10-30.
 * MonriAndroid
 */
public interface ExampleApi {
    @POST("examples/order")
    Single<OrderResponse> order(@Body OrderRequest orderRequest);

    @POST("examples/prepare-transaction")
    Single<PrepareTransactionResponse> prepareTransaction();

    @POST("examples/create-payment-session")
    Single<NewPaymentResponse> createPaymentSession(@Body NewPaymentRequest request);

    @GET("examples/access_token")
    Single<AccessTokenResponse> createAccessToken();
}
