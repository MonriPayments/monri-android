package com.monri.android.three_ds1.auth;

import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.monri.android.logger.MonriLogger;
import com.monri.android.logger.MonriLoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jasminsuljic on 2019-12-08.
 * MonriAndroid
 */
public class PaymentAuthWebViewClient extends WebViewClient {

    private static final List<String> WHITELISTED_HOST_NAMES = Arrays.asList(
            "https://ipgtest.monri.com",
            "https://ipg.monri.com",
            "https://ipgtest.webteh.hr",
            "https://ipg.webteh.hr"
    );

    private final Delegate delegate;

    private String acsHost;

    private static final MonriLogger logger = MonriLoggerFactory.get("PaymentAuthWebViewClient");

    public PaymentAuthWebViewClient(Delegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onPageFinished(WebView view, String url) {

        logger.trace(String.format("onPageFinished url [%s]", url));
        if (url.contains(acsHost)) {
//            delegate.acsLoadFinished();
        }

        super.onPageFinished(view, url);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        loadingUrlChange(request.getUrl(), true);
        return super.shouldInterceptRequest(view, request);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        loadingUrlChange(Uri.parse(url), true);
        return super.shouldInterceptRequest(view, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        loadingUrlChange(request.getUrl(), false);
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        loadingUrlChange(Uri.parse(url), false);
        return super.shouldOverrideUrlLoading(view, url);
    }

    public void setAcsUrl(String acsHost) {
        this.acsHost = acsHost;
    }


    private void loadingUrlChange(Uri uri, boolean interceptedRequest) {
        final String url = uri.toString();


        if (!validateHost(url)) {
            Log.d("PaymentAuthWebClient", "Host validation failed");
            return;
        }

        if (interceptedRequest) {
            logger.trace(String.format("intercepted url [%s]", url));
            if (url.contains("/client_redirect")) {
                delegate.redirectingToAcs();
            } else if (url.contains("/client_return")) {
                delegate.acsAuthenticationFinished();
            }
        } else {
            logger.trace(String.format("shouldOverrideUrlLoading url = [%s]",  url));
            if (url.contains("v2/payment/hooks/3ds1")) {
                delegate.threeDs1Result(uri.getQueryParameter("status"), uri.getQueryParameter("client_secret"));
            }
        }
    }


    private boolean validateHost(String url) {

        if (url == null || !url.contains("http")) {
            return false;
        }

        for (String whitelistedHostName : WHITELISTED_HOST_NAMES) {
            if (url.contains(whitelistedHostName)) {
                return true;
            }
        }

        return false;
    }

    public interface Delegate {
        void threeDs1Result(String status, String clientSecret);

        void redirectingToAcs();

        void acsAuthenticationFinished();

    }
}
