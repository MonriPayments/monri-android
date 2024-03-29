package com.monri.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.monri.android.activity.ConfirmPaymentActivity;
import com.monri.android.exception.MonriException;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentMethod;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.monri.android.MonriConfig.PROD_ENV_HOST;
import static com.monri.android.MonriConfig.TEST_ENV_HOST;

/**
 * Created by jasminsuljic on 2019-08-21.
 * MonriAndroidSDK
 */
public final class Monri {
    private final String authenticityToken;
    private final MonriApiOptions apiOptions;
    private final MonriApi monriApi;
    private PaymentController paymentController;
    @VisibleForTesting
    private
    TokenCreator mTokenCreator = (apiOptions, tokenParams, executor, callback) -> {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, ResponseWrapper> task =
                new AsyncTask<Void, Void, ResponseWrapper>() {
                    @Override
                    protected ResponseWrapper doInBackground(Void... params) {
                        try {
                            Token token = MonriApiHandler.createToken(
                                    apiOptions, tokenParams
                            );
                            return new ResponseWrapper(token);
                        } catch (MonriException e) {
                            return new ResponseWrapper(e);
                        }
                    }

                    @Override
                    protected void onPostExecute(ResponseWrapper result) {
                        tokenTaskPostExecution(result, callback);
                    }
                };

        executeTask(executor, task);
    };


    @Deprecated
    public Monri(Context context, String authenticityToken) {
        this(context, MonriApiOptions.create(authenticityToken, false));
    }

    @Deprecated
    public Monri(Context context, MonriApiOptions monriApiOptions) {
        this.authenticityToken = monriApiOptions.getAuthenticityToken();
        this.apiOptions = monriApiOptions;

        String url = monriApiOptions.isDevelopmentMode() ? TEST_ENV_HOST : PROD_ENV_HOST;

        final String authorizationHeader = String.format("WP3-v2-Client %s", apiOptions.getAuthenticityToken());

        this.monriApi = new MonriApiImpl(getMonriHttpApi(url, getHttpHeaders(authorizationHeader)));

        paymentController = new MonriPaymentController(monriApiOptions);
    }

    public Monri(ActivityResultCaller activityResultCaller, MonriApiOptions monriApiOptions) {
        this.authenticityToken = monriApiOptions.getAuthenticityToken();
        this.apiOptions = monriApiOptions;

        String url = monriApiOptions.isDevelopmentMode() ? TEST_ENV_HOST : PROD_ENV_HOST;

        final String authorizationHeader = String.format("WP3-v2-Client %s", apiOptions.getAuthenticityToken());

        this.monriApi = new MonriApiImpl(getMonriHttpApi(url, getHttpHeaders(authorizationHeader)));

        ActivityResultLauncher<ConfirmPaymentActivity.Request> registeredForActivityResult = activityResultCaller.<ConfirmPaymentActivity.Request, ConfirmPaymentActivity.Response>registerForActivityResult(new ActivityResultContract<>() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, ConfirmPaymentActivity.Request input) {
                return ConfirmPaymentActivity.createIntent(context, input);
            }

            @Override
            public ConfirmPaymentActivity.Response parseResult(int resultCode, @Nullable Intent intent) {
                return ConfirmPaymentActivity.parseResponse(resultCode, intent);
            }
        }, result -> {
            paymentController.acceptResult(result.getPaymentResult(), null);
        });
        paymentController = new MonriPaymentController(monriApiOptions, registeredForActivityResult);
    }

    private MonriHttpApi getMonriHttpApi(final String baseUrl, final Map<String, String> headers) {
        return new MonriHttpApiImpl(
                baseUrl,
                headers
        );
    }

    private Map<String, String> getHttpHeaders(final String auth) {
        return new HashMap<String, String>() {{
            put("Authorization", auth);
            put("Content-Type", "application/json; charset=UTF-8");
            put("Accept", "application/json");
        }};
    }

    public void createToken(@NonNull TokenRequest tokenRequest, @NonNull PaymentMethod paymentMethod, @NonNull final TokenCallback callback) {

        try {
            final CreateTokenRequest createTokenRequest = CreateTokenRequest.create(paymentMethod, tokenRequest, authenticityToken);
            mTokenCreator.create(apiOptions, createTokenRequest.toJson(), null, callback);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    /**
     * @deprecated use {@link #confirmPayment(ConfirmPaymentParams, ActionResultConsumer)}
     * @param context
     * @param confirmPaymentParams
     */
    @Deprecated(since = "1.4.0", forRemoval = true)
    public void confirmPayment(Activity context,
                               ConfirmPaymentParams confirmPaymentParams) {
        paymentController.confirmPayment(context, confirmPaymentParams);
    }

    public void confirmPayment(ConfirmPaymentParams confirmPaymentParams, ActionResultConsumer<PaymentResult> callback) {
        paymentController.confirmPayment(confirmPaymentParams, callback);
    }

    private void tokenTaskPostExecution(ResponseWrapper result, TokenCallback callback) {
        if (result.token != null) {
            callback.onSuccess(result.token);
        } else if (result.error != null) {
            callback.onError(result.error);
        } else {
            callback.onError(new RuntimeException("Somehow got neither a token response or an " +
                    "error response"));
        }
    }

    private void executeTask(Executor executor, AsyncTask<Void, Void, ResponseWrapper> task) {
        if (executor != null) {
            task.executeOnExecutor(executor);
        } else {
            task.execute();
        }
    }

    public MonriApi getMonriApi() {
        return monriApi;
    }

    @Deprecated
    public boolean onPaymentResult(int requestCode, Intent data, ResultCallback<PaymentResult> callback) {

        if (!paymentController.shouldHandlePaymentResult(requestCode, data)) {
            return false;
        }

        paymentController.handlePaymentResult(requestCode, data, callback);
        return true;

    }

    @VisibleForTesting
    interface TokenCreator {
        void create(MonriApiOptions apiOptions, Map<String, Object> params,
                    Executor executor,
                    TokenCallback callback);
    }

    private static class ResponseWrapper {
        final Token token;
        final Exception error;

        private ResponseWrapper(Token token) {
            this.token = token;
            this.error = null;
        }

        private ResponseWrapper(Exception error) {
            this.error = error;
            this.token = null;
        }
    }

}
