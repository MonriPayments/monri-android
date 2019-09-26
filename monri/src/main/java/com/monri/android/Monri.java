package com.monri.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.annotation.VisibleForTesting;

import com.monri.android.exception.StripeException;
import com.monri.android.model.Card;
import com.monri.android.model.Token;

import java.util.Map;
import java.util.concurrent.Executor;

import static com.monri.android.StripeNetworkUtils.hashMapFromCard;

/**
 * Created by jasminsuljic on 2019-08-21.
 * MonriAndroidSDK
 */
public final class Monri {
    private final Context context;
    private final String authenticityToken;
    private final MonriOptions options;
    @VisibleForTesting
    TokenCreator mTokenCreator = new TokenCreator() {
        @Override
        public void create(
                final Map<String, Object> tokenParams,
                final String publishableKey,
                final String stripeAccount,
                final Executor executor,
                final TokenCallback callback) {
            @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, ResponseWrapper> task =
                    new AsyncTask<Void, Void, ResponseWrapper>() {
                        @Override
                        protected ResponseWrapper doInBackground(Void... params) {
                            try {
                                RequestOptions requestOptions =
                                        RequestOptions.builder(
                                                publishableKey,
                                                stripeAccount,
                                                RequestOptions.TYPE_QUERY).build();
                                Token token = StripeApiHandler.createToken(
                                        context,
                                        tokenParams,
                                        requestOptions);
                                return new ResponseWrapper(token);
                            } catch (StripeException e) {
                                return new ResponseWrapper(e);
                            }
                        }

                        @Override
                        protected void onPostExecute(ResponseWrapper result) {
                            tokenTaskPostExecution(result, callback);
                        }
                    };

            executeTask(executor, task);
        }
    };

    private static final MonriOptions defaultOptions = new MonriOptions("auto");

    public Monri(Context context, String authenticityToken) {
        this(context, authenticityToken, defaultOptions);
    }

    private Monri(Context context, String authenticityToken, MonriOptions options) {
        this.context = context;
        this.authenticityToken = authenticityToken;
        this.options = options;
    }

    public void createToken(@NonNull TokenRequest tokenRequest,@NonNull Card card, @NonNull final TokenCallback callback) {

    }

    /**
     * The simplest way to create a token, using a {@link Card} and {@link TokenCallback}. This
     * runs on the default {@link Executor} and with the
     * currently set {@link #authenticityToken}.
     *
     * @param card     the {@link Card} used to create this payment token
     * @param callback a {@link TokenCallback} to receive either the token or an error
     */
    public void createToken(@NonNull final Card card, @NonNull final TokenCallback callback) {
        createToken(card, authenticityToken, callback);
    }

    /**
     * Call to create a {@link Token} with a specific public key.
     *
     * @param card           the {@link Card} used for this transaction
     * @param publishableKey the public key used for this transaction
     * @param callback       a {@link TokenCallback} to receive the result of this operation
     */
    public void createToken(
            @NonNull final Card card,
            @NonNull final String publishableKey,
            @NonNull final TokenCallback callback) {
        createToken(card, publishableKey, null, callback);
    }

    /**
     * Call to create a {@link Token} with the publishable key and {@link Executor} specified.
     *
     * @param card           the {@link Card} used for this token
     * @param publishableKey the publishable key to use
     * @param executor       an {@link Executor} to run this operation on. If null, this is run on a
     *                       default non-ui executor
     * @param callback       a {@link TokenCallback} to receive the result or error message
     */
    public void createToken(
            @NonNull final Card card,
            @NonNull @Size(min = 1) final String publishableKey,
            @Nullable final Executor executor,
            @NonNull final TokenCallback callback) {
        if (card == null) {
            throw new RuntimeException(
                    "Required Parameter: 'card' is required to create a token");
        }

        createTokenFromParams(
                hashMapFromCard(context, card),
                publishableKey,
                executor,
                callback);
    }

    public void createToken(
            TokenRequest tokenRequest,
            @NonNull final Card card,
            @NonNull @Size(min = 1) final String publishableKey,
            @Nullable final Executor executor,
            @NonNull final TokenCallback callback) {
        if (card == null) {
            throw new RuntimeException(
                    "Required Parameter: 'card' is required to create a token");
        }

        createTokenFromParams(
                hashMapFromCard(context, card),
                publishableKey,
                executor,
                callback);
    }

    private void createTokenFromParams(
            @NonNull final Map<String, Object> tokenParams,
            @NonNull @Size(min = 1) final String publishableKey,
            @Nullable final Executor executor,
            @NonNull final TokenCallback callback) {
        if (callback == null) {
            throw new RuntimeException(
                    "Required Parameter: 'callback' is required to use the created " +
                            "token and handle errors");
        }

        validateKey(publishableKey);
        mTokenCreator.create(
                tokenParams,
                publishableKey,
                authenticityToken,
                executor,
                callback);
    }

    private void validateKey(@NonNull @Size(min = 1) String publishableKey) {
        //noinspection ConstantConditions
        if (publishableKey == null || publishableKey.length() == 0) {
            throw new IllegalArgumentException("Invalid Publishable Key: " +
                                                       "You must use a valid publishable key to create a token.  " +
                                                       "For more info, see https://stripe.com/docs/stripe.js.");
        }

        if (publishableKey.startsWith("sk_")) {
            throw new IllegalArgumentException("Invalid Publishable Key: " +
                                                       "You are using a secret key to create a token, " +
                                                       "instead of the publishable one. For more info, " +
                                                       "see https://stripe.com/docs/stripe.js");
        }
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

    @VisibleForTesting
    interface TokenCreator {
        void create(Map<String, Object> params,
                    String publishableKey,
                    String stripeAccount,
                    Executor executor,
                    TokenCallback callback);
    }

    private class ResponseWrapper {
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
