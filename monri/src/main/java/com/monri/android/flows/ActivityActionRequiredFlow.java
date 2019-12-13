package com.monri.android.flows;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.ProgressBar;

import com.monri.android.MonriApi;
import com.monri.android.ResultCallback;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;
import com.monri.android.three_ds1.auth.PaymentAuthWebView;
import com.monri.android.three_ds1.auth.PaymentAuthWebViewClient;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public class ActivityActionRequiredFlow implements ActionRequiredFlow, PaymentAuthWebViewClient.Delegate {

    private final Activity activity;
    private final PaymentAuthWebView webView;
    private final ProgressBar progressBar;
    private final MonriApi monriApi;

    private final AtomicInteger atomicInteger = new AtomicInteger();
    private final PaymentAuthWebViewClient client;

    @SuppressLint("SetJavaScriptEnabled")
    public ActivityActionRequiredFlow(Activity activity, PaymentAuthWebView webView, ProgressBar progressBar, MonriApi monriApi) {
        this.activity = activity;
        this.webView = webView;
        this.progressBar = progressBar;
        this.monriApi = monriApi;
        webView.setWebChromeClient(new WebChromeClient());
        client = new PaymentAuthWebViewClient(PaymentAuthWebViewClient.Delegate.loggerProxy(this));
        webView.setWebViewClient(client);
        // TODO: investigate XSS vulnerability
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void handleResult(ConfirmPaymentResponse confirmPaymentResponse) {
        webView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        client.setAcsUrl(confirmPaymentResponse.getActionRequired().getAcsUrl());
        webView.loadUrl(confirmPaymentResponse.getActionRequired().getRedirectTo());
    }

    @Override
    public void threeDs1Result(String status, String clientSecret) {
        atomicInteger.set(0);
        progressBar.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.GONE);

        checkPaymentStatus(clientSecret, atomicInteger.incrementAndGet());
    }

    @Override
    public void redirectingToAcs() {
        webView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void acsLoadFinished() {
        webView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void acsAuthenticationFinished() {
        webView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void checkPaymentStatus(String clientSecret, int count) {

        if (count >= 3) {
            Intent intent = new Intent();
            PaymentResult paymentResult = new PaymentResult("pending");
            intent.putExtra(PaymentResult.BUNDLE_NAME, paymentResult);
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
        } else {
            monriApi.paymentStatus(new PaymentStatusParams(clientSecret), new ResultCallback<PaymentStatusResponse>() {
                @Override
                public void onSuccess(PaymentStatusResponse result) {

                    if (result.isPending()) {
                        // TODO: log this case, should not happen
                        checkPaymentStatus(clientSecret, atomicInteger.incrementAndGet());
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(PaymentResult.BUNDLE_NAME, result.getPaymentResult());
                        activity.setResult(Activity.RESULT_OK, intent);
                        activity.finish();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    checkPaymentStatus(clientSecret, atomicInteger.incrementAndGet());
                }
            });
        }

    }
}
