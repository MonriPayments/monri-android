package com.monri.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.monri.android.exception.MonriException;
import com.monri.android.model.Card;
import com.monri.android.model.Token;

import java.util.Map;
import java.util.concurrent.Executor;

import static com.monri.android.MonriNetworkUtils.hashMapFromCard;

/**
 * Created by jasminsuljic on 2019-08-21.
 * MonriAndroidSDK
 */
public final class Monri {
    @SuppressWarnings("FieldCanBeLocal") private final Context context;
    private final String authenticityToken;
    @VisibleForTesting private
    TokenCreator mTokenCreator = new TokenCreator() {
        @Override
        public void create(
                final Map<String, Object> tokenParams,
                final Executor executor,
                final TokenCallback callback) {
            @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, ResponseWrapper> task =
                    new AsyncTask<Void, Void, ResponseWrapper>() {
                        @Override
                        protected ResponseWrapper doInBackground(Void... params) {
                            try {
                                Token token = MonriApiHandler.createToken(
                                        tokenParams
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
        }
    };


    public Monri(Context context, String authenticityToken) {
        this.context = context;
        this.authenticityToken = authenticityToken;
    }

    public void createToken(@NonNull TokenRequest tokenRequest, @NonNull Card card, @NonNull final TokenCallback callback) {

        mTokenCreator.create(
                hashMapFromCard(card, tokenRequest, authenticityToken),
                null,
                callback);

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
