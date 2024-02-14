package com.monri.android.three_ds1.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.monri.android.logger.MonriLogger;
import com.monri.android.logger.MonriLoggerFactory;

/**
 * Created by jasminsuljic on 2019-12-08.
 * MonriAndroid
 */
public final class PaymentAuthWebView extends WebView {

    private static final MonriLogger logger = MonriLoggerFactory.get("PaymentAuthWebView");

    public PaymentAuthWebView(Context context) {
        super(context);
    }

    public PaymentAuthWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaymentAuthWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initializeForInAppRendering(final WebViewClient webViewClient) {
        final WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(false);
        settings.setDomStorageEnabled(true);
        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (consoleMessage != null) {
                    String message = consoleMessage.message();
                    if (message != null) {
                        logger.trace(message);
                    }
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });
        setWebViewClient(webViewClient);
    }
}
