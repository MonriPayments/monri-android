package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.Token;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jasminsuljic on 2019-11-12.
 * MonriAndroid
 */
public class OrderRepository {

    private final ExampleApi exampleApi;

    private final WebPayApi webPayApi;

    private final Context context;
    private final ViewDelegate viewDelegate;

    public OrderRepository(Context context, ViewDelegate viewDelegate) {
        this.context = context;
        this.viewDelegate = viewDelegate;

        String url = "https://dashboard.monri.com/api/examples/ruby/";

        ExampleModule module = new ExampleModule(url);
        exampleApi = module.publicApi();

        WebPayModule webPayModule = new WebPayModule(monriApiOptions().url() + "/");
        webPayApi = webPayModule.webPayApi();
    }

    Disposable order(Token token) {
        return exampleApi
                .order(new OrderRequest(token.getId(), UUID.randomUUID().toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleOrderResponse, this::handleOrderFailure);
    }

    Single<PrepareTransactionResponse> prepareTransaction() {
        return exampleApi.prepareTransaction()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Single<AccessTokenResponse> createAccessToken() {
        return exampleApi.createAccessToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Single<NewPaymentResponse> createPayment(final boolean addPaymentMethod) {
        final long timestamp = System.currentTimeMillis() / 1000L;
        final NewPaymentRequest newPaymentRequest = createNewPaymentRequest(timestamp, addPaymentMethod);

        final String bodyAsString;

        try {
            bodyAsString = new ObjectMapper().writeValueAsString(newPaymentRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        final String dataToHash = merchantKey() + timestamp + authenticityToken() + bodyAsString;

        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hashBytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
        String hashString = bytesToHex(hashBytes);

        final String authorization = "WP3-v2 " + authenticityToken() + " " + timestamp + " " + hashString;

        return webPayApi.createPaymentSession("application/json", authorization, newPaymentRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    private static NewPaymentRequest createNewPaymentRequest(final long timestamp, final boolean addPaymentMethod) {
        final String orderNumber = "random" + timestamp;
        final int amount = 1;
        final String currency = "EUR";
        final String transactionType = "purchase";
        final String orderInfo = "Create payment session order info";
        String scenario;

        if (addPaymentMethod) {
            scenario = "add_payment_method";
        } else {
            scenario = "charge";
        }

        final NewPaymentRequest newPaymentRequest = new NewPaymentRequest(
                amount,
                orderNumber,
                currency,
                transactionType,
                orderInfo,
                scenario
        );

        return newPaymentRequest;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    //        TODO: replace with your merchant's authenticity token
    String authenticityToken() {
        return "authenticityToken";
    }

    String merchantKey() {
        return "merchantKey";
    }

    MonriApiOptions monriApiOptions() {
        return MonriApiOptions.create(authenticityToken(), true);
    }

    void handleOrderResponse(OrderResponse orderResponse) {
        final String status = orderResponse.getStatus();
        switch (status) {
            case OrderResponse.STATUS_ACTION_REQUIRED:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(orderResponse.getAction().getRedirectTo()));
                context.startActivity(browserIntent);
                break;
            case OrderResponse.STATUS_APPROVED:
                viewDelegate.statusMessage("Order approved");
                break;
            case OrderResponse.STATUS_DECLINED:
                viewDelegate.statusMessage("Order declined");
                break;
            default:
                viewDelegate.statusMessage(String.format("Unknown status %s", status));
                break;
        }
    }

    void handleOrderFailure(Throwable throwable) {
        throwable.printStackTrace();
        viewDelegate.statusMessage(throwable.getMessage());
    }
}
