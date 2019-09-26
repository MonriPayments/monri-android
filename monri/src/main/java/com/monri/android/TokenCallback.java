package com.monri.android;

import com.monri.android.model.Token;

/**
 * Created by jasminsuljic on 2019-08-21.
 * MonriAndroidSDK
 */
public interface TokenCallback {
    void onSuccess(Token token);

    void onError(Exception exception);
}
