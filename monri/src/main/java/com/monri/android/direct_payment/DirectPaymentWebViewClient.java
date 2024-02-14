package com.monri.android.direct_payment;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.monri.android.logger.MonriLogger;
import com.monri.android.logger.MonriLoggerFactory;

public class DirectPaymentWebViewClient extends WebViewClient {

    private static final MonriLogger logger = MonriLoggerFactory.get("DirectPaymentWebViewClient");
    private final Delegate delegate;

    public DirectPaymentWebViewClient(final Delegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        delegate.onPageLoadFinished();

        logger.trace(String.format("onPageFinished url [%s]", url));

        super.onPageFinished(view, url);
    }

    public interface Delegate {
        void onPageLoadFinished();
    }
}
