package com.monri.android.activity;

import android.annotation.SuppressLint;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.button.ButtonConstants;
import com.google.android.gms.wallet.button.ButtonOptions;
import com.google.android.gms.wallet.button.PayButton;
import com.google.android.gms.wallet.contract.TaskResultContracts;
import com.monri.android.BuildConfig;
import com.monri.android.Monri;
import com.monri.android.MonriUtil;
import com.monri.android.R;
import com.monri.android.ResultCallback;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.GooglePayPayment;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentMethod;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.PaymentStatus;
import com.monri.android.three_ds1.auth.PaymentAuthWebView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ConfirmPaymentActivity extends ComponentActivity implements UiDelegate {

    private static final String CONFIRM_PAYMENT_PARAMS_BUNDLE = "CONFIRM_PAYMENT_PARAMS_BUNDLE";
    private static final String MONRI_API_OPTIONS = "MONRI_API_OPTIONS";
    private static final String GOOGLE_PAY_BUTTON_THEME = "GOOGLE_PAY_BUTTON_THEME";
    private static final String GOOGLE_PAY_BUTTON_TYPE = "GOOGLE_PAY_BUTTON_TYPE";
    private static final String GOOGLE_PAY_BUTTON_CORNER_RADIUS = "GOOGLE_PAY_BUTTON_CORNER_RADIUS";
    private final ScheduledExecutorService backgroundThreadExecutor = Executors.newScheduledThreadPool(5);
    Monri monri;
    PaymentAuthWebView webView;
    ProgressBar progressBar;
    private LinearLayout googlePayButtonContainer;
    private PaymentsClient googlePaymentsClient;
    private MonriGooglePaymentRequestHelper monriGooglePaymentRequestHelper;
    private ActivityResultLauncher<Task<PaymentData>> paymentMethodLauncher;
    private ConfirmPaymentParams googlePayConfirmPaymentParams;
    private int googlePayButtonType;
    private int googlePayButtonTheme;
    private int googlePayButtonCornerRadius;
    private static final int DEFAULT_GOOGLE_PAY_BUTTON_THEME = ButtonConstants.ButtonTheme.DARK;
    private static final int DEFAULT_GOOGLE_PAY_BUTTON_TYPE = ButtonConstants.ButtonType.BUY;
    private static final int DEFAULT_GOOGLE_PAY_BUTTON_CORNER_RADIUS = 10;

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
        intent.putExtra(GOOGLE_PAY_BUTTON_TYPE, input.googleButtonType);
        intent.putExtra(GOOGLE_PAY_BUTTON_THEME, input.googleButtonTheme);
        intent.putExtra(GOOGLE_PAY_BUTTON_CORNER_RADIUS, input.googleButtonCornerRadius);

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

        googlePayButtonType = getIntent().getIntExtra(GOOGLE_PAY_BUTTON_TYPE, DEFAULT_GOOGLE_PAY_BUTTON_TYPE);
        googlePayButtonTheme = getIntent().getIntExtra(GOOGLE_PAY_BUTTON_THEME, DEFAULT_GOOGLE_PAY_BUTTON_THEME);
        googlePayButtonCornerRadius = getIntent().getIntExtra(GOOGLE_PAY_BUTTON_CORNER_RADIUS, DEFAULT_GOOGLE_PAY_BUTTON_CORNER_RADIUS);

        Objects.requireNonNull(confirmPaymentParams, "ConfirmPaymentParams == null");
        Objects.requireNonNull(apiOptions, "MonriApiOptions == null");

        confirmPaymentParams
                .getTransaction()
                .set("meta.integration_type", "android-sdk")
                .set("meta.library", MonriUtil.library(getApplicationContext()))
                .set("meta.library_version", BuildConfig.MONRI_SDK_VERSION);

        monri = new Monri(((ActivityResultCaller) this), apiOptions);

        initBackNavigation();

        if (PaymentMethod.DIRECT_PAYMENT_METHODS.contains(confirmPaymentParams.getPaymentMethod().getType())) {
            confirmDirectPayment(confirmPaymentParams, apiOptions);

        } else if (PaymentMethod.TYPE_GOOGLE_PAY.equals(confirmPaymentParams.getPaymentMethod().getType())) {
            if (confirmPaymentParams.getPaymentMethod().getData().isEmpty()) {
                googlePayButtonContainer = findViewById(R.id.confirm_payment_google_pay_button_container);
                googlePayConfirmPaymentParams = confirmPaymentParams;
                initializeGooglePayPayment(confirmPaymentParams, apiOptions);
            } else {
                confirmCardRelatedPayment(confirmPaymentParams);
            }
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
        final ConfirmDirectPaymentFlow confirmDirectPaymentFlow = ConfirmDirectPaymentFlow.create(backgroundThreadExecutor, this, monri.getMonriApi(), confirmPaymentParams, apiOptions);
        confirmDirectPaymentFlow.execute();
    }

    public void initializeGooglePayPayment(final ConfirmPaymentParams confirmPaymentParams, final MonriApiOptions apiOptions) {
        final int walletEnvironment = (apiOptions.isDevelopmentMode()) ? WalletConstants.ENVIRONMENT_TEST : WalletConstants.ENVIRONMENT_PRODUCTION;

        googlePayButtonContainer = findViewById(R.id.confirm_payment_google_pay_button_container);
        googlePaymentsClient = Wallet.getPaymentsClient(this, new Wallet.WalletOptions.Builder().setEnvironment(walletEnvironment).build());
        monriGooglePaymentRequestHelper = new MonriGooglePaymentRequestHelper();
        initPaymentMethodDataLauncher(this);

        monri.getMonriApi().startGooglePayPayment(confirmPaymentParams.getPaymentId(), new ResultCallback<>() {

            @Override
            public void onSuccess(final JSONObject result) {
                monriGooglePaymentRequestHelper.setMonriGooglePaySessionParameters(result);
                isReadyToPay();
            }

            @Override
            public void onError(final Throwable throwable) {
                returnGooglePayErrorResponse(GooglePayHandlerException.Error.START_PAYMENT_SESSION_ERROR);
            }
        });
    }

    private void initPaymentMethodDataLauncher(final ActivityResultCaller activityResultCaller) {
        this.paymentMethodLauncher = activityResultCaller.registerForActivityResult(new TaskResultContracts.GetPaymentDataResult(), result -> {
            int statusCode = result.getStatus().getStatusCode();

            if (statusCode != CommonStatusCodes.SUCCESS) {
                returnGooglePayErrorResponse(GooglePayHandlerException.Error.GET_ALLOWED_PAYMENT_METHODS_ERROR);
            } else {
                preparePaymentMethodDataForConfirmPayment(result.getResult());
            }
        });
    }

    private void isReadyToPay() {
        try {
            final IsReadyToPayRequest isReadyToPayRequest = monriGooglePaymentRequestHelper.getIsReadyToPayRequest();
            googlePaymentsClient.isReadyToPay(isReadyToPayRequest)
                    .addOnSuccessListener(this::processIsReadyToPayRequest)
                    .addOnFailureListener(e -> returnGooglePayErrorResponse(GooglePayHandlerException.Error.IS_READY_TO_PAY_PAYMENTS_CLIENT_ERROR));
        } catch (final JSONException e) {
            returnGooglePayErrorResponse(GooglePayHandlerException.Error.PREPARE_IS_READY_TO_PAY_REQ_ERROR);
        }
    }

    private void processIsReadyToPayRequest(final Boolean isReadyToPay) {
        if (isReadyToPay) {
            prepareGooglePayButton();
        } else {
            returnGooglePayErrorResponse(GooglePayHandlerException.Error.NOT_READY_TO_PAY_WITH_GOOGLE_PAY_ERROR);
        }
    }

    private void prepareGooglePayButton() {
        final ButtonOptions.Builder buttonOptionsBuilder = ButtonOptions.newBuilder()
                .setButtonTheme(googlePayButtonTheme)
                .setButtonType(googlePayButtonType)
                .setCornerRadius(googlePayButtonCornerRadius);

        try {
            final String allowedPaymentMethods = monriGooglePaymentRequestHelper.getAllowedPaymentMethods().toString();
            final ButtonOptions buttonOptions = buttonOptionsBuilder.setAllowedPaymentMethods(allowedPaymentMethods).build();

            final PayButton payButton = new PayButton(this);
            payButton.initialize(buttonOptions);
            payButton.setOnClickListener((v) -> requestPayment());

            showGooglePayButton(payButton);
        } catch (final JSONException e) {
            returnGooglePayErrorResponse(GooglePayHandlerException.Error.GET_ALLOWED_PAYMENT_METHODS_ERROR);
        }
    }

    private void showGooglePayButton(final PayButton payButton) {
        hideLoading();

        googlePayButtonContainer.addView(payButton);
        googlePayButtonContainer.setVisibility(View.VISIBLE);
    }

    private void requestPayment() {
        try {
            final PaymentDataRequest paymentDataRequest = monriGooglePaymentRequestHelper.getPaymentDataRequest();

            googlePaymentsClient.loadPaymentData(paymentDataRequest)
                    .addOnCompleteListener(paymentMethodLauncher::launch);
        } catch (final JSONException e) {
            returnGooglePayErrorResponse(GooglePayHandlerException.Error.PREPARE_PAYMENT_DATA_REQUEST_ERROR);
        }
    }

    private void preparePaymentMethodDataForConfirmPayment(final PaymentData paymentData) {
        try {
            final JSONObject googlePaymentMethodData = monriGooglePaymentRequestHelper.getPaymentMethodDataFromPaymentData(paymentData);

            confirmGooglePayPayment(googlePaymentMethodData);
        } catch (Exception e) {
            returnGooglePayErrorResponse(GooglePayHandlerException.Error.PARSE_PAYMENT_METHOD_DATA_JSON_ERROR);
        }
    }

    private void confirmGooglePayPayment(final JSONObject googlePaymentMethodData) {
        final GooglePayPayment googlePayPayment = new GooglePayPayment(GooglePayPayment.Provider.GOOGLE_PAY, googlePaymentMethodData);
        final ConfirmPaymentParams confirmPaymentParams = ConfirmPaymentParams.create(googlePayConfirmPaymentParams.getPaymentId(), googlePayPayment.toPaymentMethodParams(), googlePayConfirmPaymentParams.getTransaction());

        googlePayButtonContainer.setVisibility(View.GONE);
        confirmCardRelatedPayment(confirmPaymentParams);
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

    private void returnGooglePayErrorResponse(final GooglePayHandlerException.Error googlePayError) {
        final List<String> errors = new ArrayList<>();

        errors.add(String.format("Google pay error with code: %d", googlePayError.getCode()));
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
        int googleButtonType;
        int googleButtonTheme;
        int googleButtonCornerRadius;

        public Request(ConfirmPaymentParams params, MonriApiOptions apiOptions) {
            this.params = params;
            this.apiOptions = apiOptions;
        }

        public Request(final ConfirmPaymentParams params, final MonriApiOptions apiOptions,
                       final int googleButtonType, final int googleButtonTheme, final int googleButtonCornerRadius
        ) {
            this.params = params;
            this.apiOptions = apiOptions;
            this.googleButtonType = googleButtonType;
            this.googleButtonTheme = googleButtonTheme;
            this.googleButtonCornerRadius = googleButtonCornerRadius;
        }

        protected Request(Parcel in) {
            params = in.readParcelable(ConfirmPaymentParams.class.getClassLoader());
            apiOptions = in.readParcelable(MonriApiOptions.class.getClassLoader());
            googleButtonType = in.readInt();
            googleButtonTheme = in.readInt();
            googleButtonCornerRadius = in.readInt();
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
            dest.writeInt(googleButtonType);
            dest.writeInt(googleButtonTheme);
            dest.writeInt(googleButtonCornerRadius);
        }
    }
}
