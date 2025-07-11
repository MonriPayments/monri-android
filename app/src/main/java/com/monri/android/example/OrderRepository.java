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
import java.util.concurrent.TimeUnit;
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

    private final ObjectMapper objectMapper;

    private static final int AMOUNT = 1;
    private static final String CURRENCY = "EUR";
    private static final String TRANSACTION_TYPE = "purchase";
    private static final String ORDER_INFO = "Create payment session order info";
    private static final String EXAMPLE_BACKEND_URL = "https://dashboard.monri.com/api/examples/ruby/";

    private static final String ADD_PAYMENT_METHOD_SCENARIO = "add_payment_method";

    private static final String CHARGE_SCENARIO = "charge";

    private static final String AUTHORIZATION_STRING_PREFIX = "WP3-v2 ";

    private static final String DIGEST_ALGORITHM = "SHA-512";

    private static final String ORDER_NUMBER_PREFIX = "random";

    private static final String CONTENT_TYPE = "application/json";

    public OrderRepository(Context context, ViewDelegate viewDelegate) {
        this.context = context;
        this.viewDelegate = viewDelegate;

        final ExampleModule module = new ExampleModule(EXAMPLE_BACKEND_URL);
        exampleApi = module.publicApi();

        final WebPayModule webPayModule = new WebPayModule(monriApiOptions().url() + "/");
        webPayApi = webPayModule.webPayApi();

        objectMapper = new ObjectMapper();
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
        final long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final NewPaymentRequest newPaymentRequest = createNewPaymentRequest(timestamp, addPaymentMethod);

        final String bodyAsString;

        try {
            bodyAsString = objectMapper.writeValueAsString(newPaymentRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        final String dataToHash = merchantKey() + timestamp + authenticityToken() + bodyAsString;

        final MessageDigest digest;

        try {
            digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return webPayApi.createPaymentSession(CONTENT_TYPE, createAuthorizationString(digest, dataToHash, timestamp), newPaymentRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    private String createAuthorizationString(final MessageDigest digest, final String dataToHash, final Long timestamp) {
        byte[] hashBytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
        String hashString = HexUtil.bytesToHex(hashBytes);

        return AUTHORIZATION_STRING_PREFIX + authenticityToken() + " " + timestamp + " " + hashString;
    }

    private static NewPaymentRequest createNewPaymentRequest(final long timestamp, final boolean addPaymentMethod) {
        final String orderNumber = ORDER_NUMBER_PREFIX + timestamp;
        final String scenario;

        if (addPaymentMethod) {
            scenario = ADD_PAYMENT_METHOD_SCENARIO;
        } else {
            scenario = CHARGE_SCENARIO;
        }

        return new NewPaymentRequest(
                AMOUNT,
                orderNumber,
                CURRENCY,
                TRANSACTION_TYPE,
                ORDER_INFO,
                scenario
        );
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
