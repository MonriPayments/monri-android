package com.monri.android.flows;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.monri.android.MonriApi;
import com.monri.android.ResultCallback;
import com.monri.android.logger.MonriLogger;
import com.monri.android.logger.MonriLoggerFactory;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;
import com.monri.android.three_ds1.auth.PaymentAuthWebView;
import com.monri.android.three_ds1.auth.PaymentAuthWebViewClient;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public class ActivityActionRequiredFlow implements ActionRequiredFlow, PaymentAuthWebViewClient.Delegate {

    private final Activity activity;
    private final PaymentAuthWebView webView;
    private final ProgressBar progressBar;
    private final MonriApi monriApi;

    private final AtomicInteger atomicInteger = new AtomicInteger();
    private final PaymentAuthWebViewClient client;
    private final MonriLogger logger = MonriLoggerFactory.get("ActivityActReqFlow");


    private InvocationState invocationState = InvocationState.CALLBACK_NOT_INVOKED;

    @SuppressLint("SetJavaScriptEnabled")
    public ActivityActionRequiredFlow(Activity activity, PaymentAuthWebView webView, ProgressBar progressBar, MonriApi monriApi) {
        this.activity = activity;
        this.webView = webView;
        this.progressBar = progressBar;
        this.monriApi = monriApi;
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(false);
        settings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (consoleMessage != null) {
                    String message = consoleMessage.message();
                    if (message != null) {
                        logger.trace(message);
                    }
                }
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }
        });
        client = new PaymentAuthWebViewClient(this);
        webView.setWebViewClient(client);
    }

    @Override
    public void handleResult(ConfirmPaymentResponse confirmPaymentResponse) {

        executeIfStatus(InvocationState.CALLBACK_NOT_INVOKED, InvocationState.HANDLE_RESULT, () -> {
            final String acsUrl = confirmPaymentResponse.getActionRequired().getAcsUrl();
            logger.info(String.format("Handle result invoked with acsUrl = [%s]", acsUrl));
            client.setAcsUrl(acsUrl);
            executeOnUiThread(() -> {
                webView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                webView.loadUrl(confirmPaymentResponse.getActionRequired().getRedirectTo());
            });
        });
    }

    @Override
    public void threeDs1Result(String status, String clientSecret) {

        executeIfStatus(InvokationState.ACS_AUTHENTICATION_FINISHED, InvokationState.THREE_DS_RESULT, () -> {
            logger.info(String.format("ThreeDs1Result, status = %s, clientSecret = %s", status, clientSecret));
            atomicInteger.set(0);
            executeOnUiThread(() -> {
                progressBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            });

            checkPaymentStatus(clientSecret, atomicInteger.incrementAndGet());
        });

    }

    @Override
    public void redirectingToAcs() {

        executeIfStatus(InvocationState.HANDLE_RESULT, InvocationState.REDIRECTING_TO_ACS, () -> {
            logger.info("redirectingToAcs");
            executeOnUiThread(() -> {
                webView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            });
        });
    }

    @Override
    public void acsAuthenticationFinished() {
        executeIfStatus(InvocationState.REDIRECTING_TO_ACS, InvocationState.ACS_AUTHENTICATION_FINISHED, () -> {
            logger.info("acsAuthenticationFinished");
            executeOnUiThread(() -> {
                webView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            });
        });

    }


    private void executeIfStatus(InvokationState state, InvokationState newState, Runnable runnable) {
        if (invokationState != state) {
            logger.warn(String.format("Tried changing to state = [%s] from state [%s], currentState = [%s]", newState.name(), state.name(), invokationState.name()));
        } else {
            logger.info(String.format("Changing state to state = [%s] from currentState = [%s]", newState.name(), state.name()));
            this.invokationState = newState;
            runnable.run();
        }
    }

    private void executeOnUiThread(Runnable runnable) {
        // Code here will run in UI thread
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    private void checkPaymentStatus(String clientSecret, int count) {

        if (count >= 3) {
            Intent intent = new Intent();
            PaymentResult paymentResult = new PaymentResult("pending");
            intent.putExtra(PaymentResult.BUNDLE_NAME, paymentResult);
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
        } else {
            monriApi.paymentStatus(new PaymentStatusParams(clientSecret), new ResultCallback<PaymentStatusResponse>() {
                @Override
                public void onSuccess(PaymentStatusResponse result) {
                    Intent intent = new Intent();
                    intent.putExtra(PaymentResult.BUNDLE_NAME, result.getPaymentResult());
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
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
