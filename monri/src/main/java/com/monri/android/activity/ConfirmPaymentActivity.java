package com.monri.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.annotation.Nullable;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.button.ButtonOptions;
import com.google.android.gms.wallet.button.PayButton;
import com.monri.android.BuildConfig;
import com.monri.android.Monri;
import com.monri.android.MonriUtil;
import com.monri.android.R;
import com.monri.android.googlepay.GooglePayButtonOptions;
import com.monri.android.googlepay.GooglePayHandler;
import com.monri.android.googlepay.GooglePayHandlerCallbacks;
import com.monri.android.googlepay.GooglePayHandlerException;
import com.monri.android.model.BrowserInfo;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentMethod;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.PaymentStatus;
import com.monri.android.three_ds1.auth.PaymentAuthWebView;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ConfirmPaymentActivity extends ComponentActivity implements UiDelegate, GooglePayHandlerCallbacks {

    private static final String CONFIRM_PAYMENT_PARAMS_BUNDLE = "CONFIRM_PAYMENT_PARAMS_BUNDLE";
    private static final String MONRI_API_OPTIONS = "MONRI_API_OPTIONS";
    private static final String GOOGLE_PAY_BUTTON_OPTIONS = "GOOGLE_PAY_BUTTON_OPTIONS";
    private final ScheduledExecutorService backgroundThreadExecutor = Executors.newScheduledThreadPool(5);
    Monri monri;
    PaymentAuthWebView webView;
    ProgressBar progressBar;
    private LinearLayout googlePayButtonContainer;
    private GooglePayButtonOptions googlePayButtonOptions;
    private GooglePayHandler googlePayHandler;

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
        intent.putExtra(GOOGLE_PAY_BUTTON_OPTIONS, input.googlePayButtonOptions);

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

        final String paymentMethodType = confirmPaymentParams.getPaymentMethod().getType();
        final Map<String, String> paymentMethodData = confirmPaymentParams.getPaymentMethod().getData();

        if (PaymentMethod.DIRECT_PAYMENT_METHODS.contains(paymentMethodType)) {
            confirmDirectPayment(confirmPaymentParams, apiOptions);
        } else if (PaymentMethod.TYPE_GOOGLE_PAY.equals(paymentMethodType) && paymentMethodData.isEmpty()) {
            googlePayButtonOptions = getIntent().getParcelableExtra(GOOGLE_PAY_BUTTON_OPTIONS);

            initializeGooglePayPayment(confirmPaymentParams, apiOptions);
        } else {
            confirmCardRelatedPayment(confirmPaymentParams);
        }
    }

    @Override
    public void onGooglePayButtonReady(final PayButton payButton) {
        hideLoading();

        googlePayButtonContainer.addView(payButton);
        googlePayButtonContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfirmGooglePayPaymentDataReady(final ConfirmPaymentParams googlePayConfirmPaymentParams) {
        googlePayButtonContainer.setVisibility(View.GONE);
        showLoading();

        confirmCardRelatedPayment(googlePayConfirmPaymentParams);
    }

    @Override
    public void onGooglePayError(final GooglePayHandlerException googlePayHandlerException) {
        returnGooglePayErrorResponse(googlePayHandlerException);
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
        ensureBrowserInfo(confirmPaymentParams);
        final ConfirmDirectPaymentFlow confirmDirectPaymentFlow = ConfirmDirectPaymentFlow.create(backgroundThreadExecutor, this, monri.getMonriApi(), confirmPaymentParams, apiOptions);
        confirmDirectPaymentFlow.execute();
    }

    private void initializeGooglePayPayment(final ConfirmPaymentParams confirmPaymentParams, final MonriApiOptions apiOptions) {
        final int walletEnvironment = (apiOptions.isDevelopmentMode()) ? WalletConstants.ENVIRONMENT_TEST : WalletConstants.ENVIRONMENT_PRODUCTION;

        final ButtonOptions.Builder buttonOptionsBuilder = ButtonOptions.newBuilder();

        if (googlePayButtonOptions != null) {
            buttonOptionsBuilder.setButtonTheme(googlePayButtonOptions.getButtonTheme())
                                .setButtonType(googlePayButtonOptions.getButtonType())
                                .setCornerRadius(googlePayButtonOptions.getCornerRadius());
        }

        googlePayButtonContainer = findViewById(R.id.confirm_payment_google_pay_button_container);
        googlePayHandler = new GooglePayHandler(this, this, walletEnvironment, monri, buttonOptionsBuilder, this);

        googlePayHandler.startGooglePayPayment(confirmPaymentParams);
    }

    private void confirmCardRelatedPayment(final ConfirmPaymentParams confirmPaymentParams) {
        ensureBrowserInfo(confirmPaymentParams);

        final ConfirmPaymentResponseCallback responseCallback = ConfirmPaymentResponseCallback.create(this, monri.getMonriApi());

        monri.getMonriApi().confirmPayment(confirmPaymentParams, responseCallback);
    }

    private void ensureBrowserInfo(final ConfirmPaymentParams confirmPaymentParams) {
        if (confirmPaymentParams.getBrowserInfo() == null) {
            confirmPaymentParams.setBrowserInfo(BrowserInfo.create(getApplicationContext()));
        }
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

    private void returnGooglePayErrorResponse(final GooglePayHandlerException googlePayHandlerException) {
        final List<String> errors = List.of(googlePayHandlerException.toString());

        handlePaymentResult(new PaymentResult(PaymentStatus.GOOGLE_PAY_ERROR.getStatus(), errors));
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
        GooglePayButtonOptions googlePayButtonOptions;

        public Request(ConfirmPaymentParams params, MonriApiOptions apiOptions) {
            this.params = params;
            this.apiOptions = apiOptions;
        }

        public Request(final ConfirmPaymentParams params, final MonriApiOptions apiOptions,
                       final GooglePayButtonOptions googlePayButtonOptions
        ) {
            this.params = params;
            this.apiOptions = apiOptions;
            this.googlePayButtonOptions = googlePayButtonOptions;
        }

        protected Request(Parcel in) {
            params = in.readParcelable(ConfirmPaymentParams.class.getClassLoader());
            apiOptions = in.readParcelable(MonriApiOptions.class.getClassLoader());
            googlePayButtonOptions = in.readParcelable(GooglePayButtonOptions.class.getClassLoader());
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
            dest.writeParcelable(googlePayButtonOptions, flags);
        }
    }
}
