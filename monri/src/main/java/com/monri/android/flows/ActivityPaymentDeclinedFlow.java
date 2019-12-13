package com.monri.android.flows;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;

import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentResult;
import com.monri.android.three_ds1.auth.PaymentAuthWebView;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public class ActivityPaymentDeclinedFlow implements PaymentDeclinedFlow {

    private final Activity activity;
    private final PaymentAuthWebView webView;
    private final ProgressBar progressBar;

    public ActivityPaymentDeclinedFlow(Activity activity, PaymentAuthWebView webView, ProgressBar progressBar) {
        this.activity = activity;
        this.webView = webView;
        this.progressBar = progressBar;
    }

    @Override
    public void handleResult(ConfirmPaymentResponse response) {
        progressBar.setEnabled(false);
        webView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent();
        PaymentResult paymentResult = new PaymentResult(response.getStatus().getStatus());
        intent.putExtra(PaymentResult.BUNDLE_NAME, paymentResult);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }
}
