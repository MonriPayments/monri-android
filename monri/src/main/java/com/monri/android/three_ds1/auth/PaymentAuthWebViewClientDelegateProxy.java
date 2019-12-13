package com.monri.android.three_ds1.auth;

import android.os.Handler;
import android.os.Looper;

import com.monri.android.logger.MonriLogger;
import com.monri.android.logger.MonriLoggerFactory;

/**
 * Created by jasminsuljic on 2019-12-13.
 * MonriAndroid
 */
public class PaymentAuthWebViewClientDelegateProxy implements PaymentAuthWebViewClient.Delegate {

    private final PaymentAuthWebViewClient.Delegate delegate;
    private final MonriLogger logger;

    PaymentAuthWebViewClientDelegateProxy(PaymentAuthWebViewClient.Delegate delegate) {
        this.delegate = delegate;
        this.logger = MonriLoggerFactory.get(this.getClass());
    }

    @Override
    public void threeDs1Result(String status, String clientSecret) {
        logger.info("ThreeDs1Result, status = %s, clientSecret = %s", status, clientSecret);
        executeOnUiThread(() -> {
            delegate.threeDs1Result(status, clientSecret);
        });
    }



    @Override
    public void redirectingToAcs() {
        logger.info("redirectingToAcs");
        executeOnUiThread(delegate::redirectingToAcs);
    }

    @Override
    public void acsLoadFinished() {
        logger.info("acsLoadFinished");
        executeOnUiThread(delegate::acsLoadFinished);
    }

    @Override
    public void acsAuthenticationFinished() {
        logger.info("acsAuthenticationFinished");
        executeOnUiThread(delegate::acsAuthenticationFinished);
    }

    private void executeOnUiThread(Runnable runnable) {
        // Code here will run in UI thread
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
