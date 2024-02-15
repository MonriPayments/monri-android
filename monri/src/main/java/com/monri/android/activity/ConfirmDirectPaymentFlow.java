package com.monri.android.activity;

import android.webkit.WebViewClient;

import com.monri.android.MonriApi;
import com.monri.android.ResultCallback;
import com.monri.android.direct_payment.DirectPaymentWebViewClient;
import com.monri.android.flows.PaymentErrorFlow;
import com.monri.android.flows.PaymentErrorFlowImpl;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

final class ConfirmDirectPaymentFlow implements ResultCallback<PaymentStatusResponse>, DirectPaymentWebViewClient.Delegate {

    private static final long CHECK_FOR_PAYMENT_STATUS_DELAY_MILLIS = 1_000L;
    private static final String DIRECT_PAYMENT_REDIRECTION_ENDPOINT = "/v2/direct-payment/pay-cek-hr/%s/redirect-to-payment-url";

    private final ScheduledExecutorService backgroundThreadExecutor;
    private final MonriApi monriApi;
    private final UiDelegate uiDelegate;
    private final ConfirmPaymentParams confirmPaymentParams;
    private final MonriApiOptions apiOptions;
    private final PaymentErrorFlow paymentErrorFlow;

    private ConfirmDirectPaymentFlow(final ScheduledExecutorService backgroundThreadExecutor,
                                     final MonriApi monriApi,
                                     final UiDelegate uiDelegate,
                                     final ConfirmPaymentParams confirmPaymentParams,
                                     final MonriApiOptions apiOptions) {
        this.backgroundThreadExecutor = backgroundThreadExecutor;
        this.monriApi = monriApi;
        this.uiDelegate = uiDelegate;
        this.confirmPaymentParams = confirmPaymentParams;
        this.apiOptions = apiOptions;

        this.paymentErrorFlow = new PaymentErrorFlowImpl(uiDelegate);

        final WebViewClient client = new DirectPaymentWebViewClient(this);
        uiDelegate.initializeWebView(client);
    }

    public static ConfirmDirectPaymentFlow create(final ScheduledExecutorService backgroundThreadExecutor,
                                                  final UiDelegate uiDelegate,
                                                  final MonriApi monriApi,
                                                  final ConfirmPaymentParams confirmPaymentParams,
                                                  final MonriApiOptions apiOptions
    ) {
        Objects.requireNonNull(backgroundThreadExecutor, "ScheduledExecutorService == null");
        Objects.requireNonNull(uiDelegate, "UiDelegate == null");
        Objects.requireNonNull(monriApi, "MonriApi == null");
        Objects.requireNonNull(confirmPaymentParams, "ConfirmPaymentParams == null");
        Objects.requireNonNull(apiOptions, "MonriApiOptions == null");

        return new ConfirmDirectPaymentFlow(backgroundThreadExecutor, monriApi, uiDelegate, confirmPaymentParams, apiOptions);
    }

    public void execute() {
        uiDelegate.showLoading();
        uiDelegate.showWebView();
        uiDelegate.loadWebViewUrl(apiOptions.url() + String.format(DIRECT_PAYMENT_REDIRECTION_ENDPOINT, confirmPaymentParams.getPaymentId()));

        checkForPaymentStatus();
    }

    private void checkForPaymentStatus() {
        backgroundThreadExecutor.schedule(
                () -> monriApi.paymentStatus(new PaymentStatusParams(confirmPaymentParams.getPaymentId()), this),
                CHECK_FOR_PAYMENT_STATUS_DELAY_MILLIS,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void onSuccess(final PaymentStatusResponse result) {
        if (result == null || result.getPaymentStatus() == null) {
            paymentErrorFlow.handleResult(new IllegalStateException("Result == null"));

        } else {
            switch (result.getPaymentStatus()) {
                case PAYMENT_METHOD_REQUIRED:
                    checkForPaymentStatus();
                    break;

                case APPROVED:
                case EXECUTED:
                case DECLINED:
                    handlePaymentFinished(result);
                    break;

                case ACTION_REQUIRED:
                    /* no-op */
                    break;
            }
        }
    }

    private void handlePaymentFinished(final PaymentStatusResponse result) {
        uiDelegate.hideLoading();
        uiDelegate.makeWebViewGone();

        uiDelegate.handlePaymentResult(result.getPaymentResult());
    }

    @Override
    public void onError(Throwable throwable) {
        paymentErrorFlow.handleResult(throwable);
    }

    @Override
    public void onPageLoadFinished() {
        uiDelegate.hideLoading();
    }
}
