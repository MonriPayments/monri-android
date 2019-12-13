package com.monri.android;

import android.app.Activity;

import com.monri.android.model.ConfirmPaymentResponse;

import java.lang.ref.WeakReference;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public class ConfirmCallbackResultCallback implements ResultCallback<ConfirmPaymentResponse> {

    private final WeakReference<Activity> activity;

    ConfirmCallbackResultCallback(WeakReference<Activity> activity) {
        this.activity = activity;
    }

    @Override
    public void onSuccess(ConfirmPaymentResponse result) {
        switch (result.getStatus()) {
            case APPROVED:
                approvedFlow(result);
                break;
            case DECLINED:
                declinedFlow(result);
                break;
            case ACTION_REQUIRED:
                actionRequiredFlow(result);
                break;
            default:
                throw new IllegalStateException(String.format("Status %s not supported", result.getStatus().getStatus()));
        }
    }

    private void actionRequiredFlow(ConfirmPaymentResponse result) {

    }

    private void declinedFlow(ConfirmPaymentResponse result) {

    }

    private void approvedFlow(ConfirmPaymentResponse result) {

    }

    @Override
    public void onError(Throwable throwable) {

    }
}
