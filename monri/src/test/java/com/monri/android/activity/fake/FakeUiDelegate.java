package com.monri.android.activity.fake;

import android.webkit.WebViewClient;

import com.monri.android.activity.UiDelegate;
import com.monri.android.model.PaymentResult;

/**
 * Fake UiDelegate that keeps track of the UI state.
 * */
public final class FakeUiDelegate implements UiDelegate {

    private String currentUrl;

    private boolean webViewVisible;
    private boolean loadingVisible;
    private PaymentResult paymentResultToHandle;

    @Override
    public void showLoading() {
        loadingVisible = true;
    }

    @Override
    public void hideLoading() {
        loadingVisible = false;
    }

    @Override
    public void showWebView() {
        webViewVisible = true;
    }

    @Override
    public void loadWebViewUrl(String url) {
        this.currentUrl = url;
    }

    @Override
    public void hideWebView() {
        webViewVisible = false;
    }

    @Override
    public void makeWebViewGone() {
        webViewVisible = false;
    }

    @Override
    public void handlePaymentResult(PaymentResult paymentResult) {
        paymentResultToHandle = paymentResult;
    }

    @Override
    public void initializeWebView(WebViewClient webViewClient) {
        /* no-op */
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public PaymentResult getPaymentResultToHandle() {
        return paymentResultToHandle;
    }

    public boolean isWebViewVisible() {
        return webViewVisible;
    }

    public boolean isLoadingVisible() {
        return loadingVisible;
    }
}
