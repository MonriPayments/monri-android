package com.monri.android.flows;

import android.os.Handler;
import android.os.Looper;
import android.webkit.WebViewClient;

import com.monri.android.MonriApi;
import com.monri.android.ResultCallback;
import com.monri.android.activity.UiDelegate;
import com.monri.android.logger.MonriLogger;
import com.monri.android.logger.MonriLoggerFactory;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;
import com.monri.android.three_ds1.auth.PaymentAuthWebViewClient;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public class ActivityActionRequiredFlow implements ActionRequiredFlow, PaymentAuthWebViewClient.Delegate {

    private final UiDelegate uiDelegate;
    private final MonriApi monriApi;

    private final AtomicInteger atomicInteger = new AtomicInteger();
    private final MonriLogger logger = MonriLoggerFactory.get("ActivityActReqFlow");


    private InvocationState invocationState = InvocationState.CALLBACK_NOT_INVOKED;

    public ActivityActionRequiredFlow(final UiDelegate uiDelegate, final MonriApi monriApi) {
        this.uiDelegate = uiDelegate;
        this.monriApi = monriApi;

        final WebViewClient client = new PaymentAuthWebViewClient(this);
        uiDelegate.initializeWebView(client);
    }

    @Override
    public void handleResult(ConfirmPaymentResponse confirmPaymentResponse) {

        executeIfStatus(InvocationState.CALLBACK_NOT_INVOKED, InvocationState.HANDLE_RESULT, () -> {
            final String acsUrl = confirmPaymentResponse.getActionRequired().getAcsUrl();
            logger.info(String.format("Handle result invoked with acsUrl = [%s]", acsUrl));
            executeOnUiThread(() -> {
                uiDelegate.showLoading();
                uiDelegate.hideWebView();
                uiDelegate.loadWebViewUrl(confirmPaymentResponse.getActionRequired().getRedirectTo());
            });
        });
    }

    @Override
    public void threeDs1Result(String status, String clientSecret) {

        logger.info(String.format("ThreeDs1Result, status = %s, clientSecret = %s", status, clientSecret));
        atomicInteger.set(0);
        executeOnUiThread(() -> {
            uiDelegate.hideLoading();
            uiDelegate.makeWebViewGone();
        });

        checkPaymentStatus(clientSecret, atomicInteger.incrementAndGet());
    }

    @Override
    public void redirectingToAcs() {

        executeIfStatus(InvocationState.HANDLE_RESULT, InvocationState.REDIRECTING_TO_ACS, () -> {
            logger.info("redirectingToAcs");
            executeOnUiThread(() -> {
                uiDelegate.showWebView();
                uiDelegate.hideLoading();
            });
        });
    }

    @Override
    public void acsAuthenticationFinished() {
        logger.info("acsAuthenticationFinished");
        executeOnUiThread(() -> {
            uiDelegate.hideWebView();
            uiDelegate.showLoading();
        });
    }

    private void executeIfStatus(InvocationState state, InvocationState newState, Runnable runnable) {
        if (invocationState != state) {
            logger.warn(String.format("Tried changing to state = [%s] from state [%s], currentState = [%s]", newState.name(), state.name(), invocationState.name()));
        } else {
            logger.info(String.format("Changing state to state = [%s] from currentState = [%s]", newState.name(), state.name()));
            this.invocationState = newState;
            runnable.run();
        }
    }

    private void executeOnUiThread(Runnable runnable) {
        // Code here will run in UI thread
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    private void checkPaymentStatus(String clientSecret, int count) {

        if (count >= 3) {
            final PaymentResult paymentResult = new PaymentResult("pending");
            uiDelegate.handlePaymentResult(paymentResult);
        } else {
            monriApi.paymentStatus(new PaymentStatusParams(clientSecret), new ResultCallback<>() {
                @Override
                public void onSuccess(PaymentStatusResponse result) {
                    uiDelegate.handlePaymentResult(result.getPaymentResult());
                }

                @Override
                public void onError(Throwable throwable) {
                    checkPaymentStatus(clientSecret, atomicInteger.incrementAndGet());
                }
            });
        }
    }

    enum InvocationState {
        CALLBACK_NOT_INVOKED,
        THREE_DS_RESULT,
        REDIRECTING_TO_ACS,
        ACS_LOAD_FINISHED,
        ACS_AUTHENTICATION_FINISHED,
        HANDLE_RESULT
    }
}
