package com.monri.android.example;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.monri.android.model.Token;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jasminsuljic on 2019-11-12.
 * MonriAndroid
 */
public class OrderRepository {

    private final ExampleApi exampleApi;
    private final Context context;

    public OrderRepository(Context context) {
        this.context = context;
        ExampleModule module = new ExampleModule("https://mobile.webteh.hr/");
        exampleApi = module.publicApi();
    }

    Disposable order(Token token) {
        return exampleApi
                .order(new OrderRequest(token.getId(), UUID.randomUUID().toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<OrderResponse>() {
                    @Override
                    public void accept(OrderResponse orderResponse) throws Exception {
                        handleOrderResponse(orderResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handleOrderFailure(throwable);
                    }
                });
    }


    //        TODO: replace with your merchant's merchant key
    String merchantKey() {
        return "TestKeyXULLyvgWyPJSwOHe";
    }

    //        TODO: replace with your merchant's authenticity monriToken
    String authenticityToken() {
        return "6a13d79bde8da9320e88923cb3472fb638619ccb";
    }

    void handleOrderResponse(OrderResponse orderResponse) {
        final String status = orderResponse.getStatus();
        switch (status) {
            case OrderResponse.STATUS_ACTION_REQUIRED:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(orderResponse.getAction().getRedirectTo()));
                context.startActivity(browserIntent);
                break;
            case OrderResponse.STATUS_APPROVED:
                Toast.makeText(context, "Order approved", Toast.LENGTH_LONG).show();
                break;
            case OrderResponse.STATUS_DECLINED:
                Toast.makeText(context, "Order declined", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, String.format("Unknown status %s", status), Toast.LENGTH_LONG).show();
                break;
        }
    }

    void handleOrderFailure(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }
}
