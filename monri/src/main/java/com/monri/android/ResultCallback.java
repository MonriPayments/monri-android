package com.monri.android;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public interface ResultCallback<T> {
    void onSuccess(T result);

    void onError(Throwable throwable);

}
