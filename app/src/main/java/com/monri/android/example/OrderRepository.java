package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.Token;

import java.util.UUID;
import java.util.concurrent.Callable;

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
    private final Context context;
    private final ViewDelegate viewDelegate;

    public OrderRepository(Context context, ViewDelegate viewDelegate) {
        this.context = context;
        this.viewDelegate = viewDelegate;
        String url = "https://dashboard.monri.com/api/examples/ruby/";
        ExampleModule module = new ExampleModule(url);
        exampleApi = module.publicApi();
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

    Single<NewPaymentResponse> createPayment() {
        return createPayment(false);
    }

    Single<NewPaymentResponse> createPayment(boolean addPaymentMethod) {
        return exampleApi.createPaymentSession(new NewPaymentRequest(addPaymentMethod))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //        TODO: replace with your merchant's authenticity token
    String authenticityToken() {
        return "6a13d79bde8da9320e88923cb3472fb638619ccb";
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
