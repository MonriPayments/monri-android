package com.monri.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.annotation.Nullable;

import com.monri.android.BuildConfig;
import com.monri.android.Monri;
import com.monri.android.MonriUtil;
import com.monri.android.R;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentMethod;
import com.monri.android.model.PaymentResult;
import com.monri.android.three_ds1.auth.PaymentAuthWebView;

import java.util.Objects;

public class ConfirmPaymentActivity extends ComponentActivity implements UiDelegate {

    private static final String CONFIRM_PAYMENT_PARAMS_BUNDLE = "CONFIRM_PAYMENT_PARAMS_BUNDLE";
    private static final String MONRI_API_OPTIONS = "MONRI_API_OPTIONS";

    Monri monri;

    PaymentAuthWebView webView;
    ProgressBar progressBar;

    /**
     * @deprecated use {@link #createIntent(Context context, Request input)}
     */
    @Deprecated(forRemoval = true)
    public static Intent createIntent(Context context, ConfirmPaymentParams params, MonriApiOptions apiOptions) {
        final Intent intent = new Intent(context, ConfirmPaymentActivity.class);
        intent.putExtra(CONFIRM_PAYMENT_PARAMS_BUNDLE, params);
        intent.putExtra(MONRI_API_OPTIONS, apiOptions);
        return intent;
    }

    @Nullable
    public static Response parseResponse(int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentResult paymentResult = intent.getParcelableExtra(PaymentResult.BUNDLE_NAME);
            return new Response(paymentResult);
        }
        return null;
    }

    public static Intent createIntent(final Context context, final Request input) {

        Objects.requireNonNull(input.params, "Request.ConfirmPaymentParams == null");
        Objects.requireNonNull(input.apiOptions, "Request.MonriApiOptions == null");

        final Intent intent = new Intent(context, ConfirmPaymentActivity.class);
        intent.putExtra(CONFIRM_PAYMENT_PARAMS_BUNDLE, input.params);
        intent.putExtra(MONRI_API_OPTIONS, input.apiOptions);
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

        Objects.requireNonNull(confirmPaymentParams, "ConfirmPaymentParams == null");
        Objects.requireNonNull(apiOptions, "MonriApiOptions == null");

        confirmPaymentParams
                .getTransaction()
                .set("meta.integration_type", "android-sdk")
                .set("meta.library", MonriUtil.library(getApplicationContext()))
                .set("meta.library_version", BuildConfig.MONRI_SDK_VERSION);

        monri = new Monri(((ActivityResultCaller) this), apiOptions);

        initBackNavigation();

        if (PaymentMethod.TYPE_DIRECT_PAYMENT.equals(confirmPaymentParams.getPaymentMethod().getType())) {
            confirmDirectPayment(confirmPaymentParams, apiOptions);

        } else {
            confirmCardRelatedPayment(confirmPaymentParams);
        }
    }

    private void initBackNavigation() {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                final Intent intent = new Intent();
                final PaymentResult paymentResult = new PaymentResult("pending");
                intent.putExtra(PaymentResult.BUNDLE_NAME, paymentResult);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void confirmDirectPayment(final ConfirmPaymentParams confirmPaymentParams, final MonriApiOptions apiOptions) {
        final ConfirmDirectPaymentFlow confirmDirectPaymentFlow = ConfirmDirectPaymentFlow.create(this, monri.getMonriApi(), confirmPaymentParams, apiOptions);
        confirmDirectPaymentFlow.execute();
    }

    private void confirmCardRelatedPayment(final ConfirmPaymentParams confirmPaymentParams) {
        final ConfirmPaymentResponseCallback responseCallback = ConfirmPaymentResponseCallback.create(this, monri.getMonriApi());
        monri.getMonriApi().confirmPayment(confirmPaymentParams, responseCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.resumeTimers();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showWebView() {
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadWebViewUrl(String url) {
        webView.loadUrl(url);
    }

    @Override
    public void hideWebView() {
        webView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void makeWebViewGone() {
        webView.setVisibility(View.GONE);
    }

    @Override
    public void handlePaymentResult(PaymentResult paymentResult) {
        final Intent intent = new Intent();
        intent.putExtra(PaymentResult.BUNDLE_NAME, paymentResult);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void initializeWebView(final WebViewClient delegate) {
        webView.initializeForInAppRendering(delegate);
    }

    @Override
    protected void onPause() {
        webView.pauseTimers();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        monri = null;
        super.onDestroy();
    }

    public static class Response implements Parcelable {
        PaymentResult paymentResult;

        public Response(PaymentResult paymentResult) {
            this.paymentResult = paymentResult;
        }

        protected Response(Parcel in) {
            paymentResult = in.readParcelable(PaymentResult.class.getClassLoader());
        }

        public static final Creator<Response> CREATOR = new Creator<>() {
            @Override
            public Response createFromParcel(Parcel in) {
                return new Response(in);
            }

            @Override
            public Response[] newArray(int size) {
                return new Response[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(paymentResult, flags);
        }

        public PaymentResult getPaymentResult() {
            return paymentResult;
        }
    }

    public static class Request implements Parcelable {
        ConfirmPaymentParams params;
        MonriApiOptions apiOptions;

        public Request(ConfirmPaymentParams params, MonriApiOptions apiOptions) {
            this.params = params;
            this.apiOptions = apiOptions;
        }

        protected Request(Parcel in) {
            params = in.readParcelable(ConfirmPaymentParams.class.getClassLoader());
            apiOptions = in.readParcelable(MonriApiOptions.class.getClassLoader());
        }

        public static final Creator<Request> CREATOR = new Creator<>() {
            @Override
            public Request createFromParcel(Parcel in) {
                return new Request(in);
            }

            @Override
            public Request[] newArray(int size) {
                return new Request[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(params, flags);
            dest.writeParcelable(apiOptions, flags);
        }
    }
}
