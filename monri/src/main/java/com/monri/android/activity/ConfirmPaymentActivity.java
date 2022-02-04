package com.monri.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import com.monri.android.Monri;
import com.monri.android.R;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.three_ds1.auth.PaymentAuthWebView;

public class ConfirmPaymentActivity extends Activity {

    private static final String CONFIRM_PAYMENT_PARAMS_BUNDLE = "CONFIRM_PAYMENT_PARAMS_BUNDLE";
    private static final String MONRI_API_OPTIONS = "MONRI_API_OPTIONS";
    Monri monri;

    PaymentAuthWebView webView;
    ProgressBar progressBar;

    public static Intent createIntent(Context context, ConfirmPaymentParams params, MonriApiOptions apiOptions) {
        final Intent intent = new Intent(context, ConfirmPaymentActivity.class);
        intent.putExtra(CONFIRM_PAYMENT_PARAMS_BUNDLE, params);
        intent.putExtra(MONRI_API_OPTIONS, apiOptions);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);

        webView = findViewById(R.id.web_view_confirm_payment);
        progressBar = findViewById(R.id.progress_bar_confirm_payment);

        final ConfirmPaymentParams confirmPaymentParams = getIntent().getParcelableExtra(CONFIRM_PAYMENT_PARAMS_BUNDLE);
        final MonriApiOptions apiOptions = getIntent().getParcelableExtra(MONRI_API_OPTIONS);

        monri = new Monri(this, apiOptions);

        ConfirmPaymentResponseCallback responseCallback = ConfirmPaymentResponseCallback.create(this, webView, progressBar, confirmPaymentParams, monri.getMonriApi());

        monri.getMonriApi().confirmPayment(confirmPaymentParams, responseCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        monri = null;
    }
}
