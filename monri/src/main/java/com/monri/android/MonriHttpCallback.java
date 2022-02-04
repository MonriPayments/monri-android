package com.monri.android;

public interface MonriHttpCallback {
    void onSuccess(MonriHttpResult result);

    void onError(MonriHttpException error);
}
