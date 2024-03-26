package com.monri.android.activity;

import android.webkit.WebViewClient;

import com.monri.android.model.PaymentResult;

public interface UiDelegate {

    void showLoading();

    void hideLoading();

    void showWebView();

    void loadWebViewUrl(final String url);

    void hideWebView();

    void makeWebViewGone();

    void handlePaymentResult(PaymentResult paymentResult);

    void initializeWebView(WebViewClient webViewClient);
}
