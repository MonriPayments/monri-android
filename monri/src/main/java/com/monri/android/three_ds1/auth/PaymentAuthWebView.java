package com.monri.android.three_ds1.auth;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by jasminsuljic on 2019-12-08.
 * MonriAndroid
 */
public class PaymentAuthWebView extends WebView {
    public PaymentAuthWebView(Context context) {
        super(context);
    }

    public void initialize(Activity activity, String paymentId) {

    }

    public PaymentAuthWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaymentAuthWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void cleanUp() {
        destroy();
    }
}
