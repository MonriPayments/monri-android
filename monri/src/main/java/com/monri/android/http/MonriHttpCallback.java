package com.monri.android.http;

public interface MonriHttpCallback {
    void onSuccess(MonriHttpResult result);

    void onError(MonriHttpException error);
}
