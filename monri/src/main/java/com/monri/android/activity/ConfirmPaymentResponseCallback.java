package com.monri.android.activity;

import android.app.Activity;
import android.widget.ProgressBar;

import com.monri.android.MonriApi;
import com.monri.android.ResultCallback;
import com.monri.android.flows.ActionRequiredFlow;
import com.monri.android.flows.ActivityActionRequiredFlow;
import com.monri.android.flows.ActivityPaymentApprovedFlow;
import com.monri.android.flows.ActivityPaymentDeclinedFlow;
import com.monri.android.flows.PaymentApprovedFlow;
import com.monri.android.flows.PaymentDeclinedFlow;
import com.monri.android.flows.PaymentErrorFlow;
import com.monri.android.flows.PaymentErrorFlowImpl;
import com.monri.android.flows.UnknownFlow;
import com.monri.android.flows.UnknownFlowImpl;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.three_ds1.auth.PaymentAuthWebView;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public final class ConfirmPaymentResponseCallback implements ResultCallback<ConfirmPaymentResponse> {

    private final ActionRequiredFlow actionRequiredFlow;
    private final PaymentApprovedFlow paymentApprovedFlow;
    private final PaymentDeclinedFlow paymentDeclinedFlow;
    private final UnknownFlow unknownFlow;
    private final PaymentErrorFlow paymentErrorFlow;

    private ConfirmPaymentResponseCallback(ActionRequiredFlow actionRequiredFlow, PaymentApprovedFlow paymentApprovedFlow, PaymentDeclinedFlow paymentDeclinedFlow, UnknownFlow unknownFlow, PaymentErrorFlow paymentErrorFlow) {
        this.actionRequiredFlow = actionRequiredFlow;
        this.paymentApprovedFlow = paymentApprovedFlow;
        this.paymentDeclinedFlow = paymentDeclinedFlow;
        this.unknownFlow = unknownFlow;
        this.paymentErrorFlow = paymentErrorFlow;
    }

    public static ConfirmPaymentResponseCallback create(Activity activity,
                                                        PaymentAuthWebView webView,
                                                        ProgressBar progressBar,
                                                        ConfirmPaymentParams params, MonriApi monriApi) {
        return new ConfirmPaymentResponseCallback(
                new ActivityActionRequiredFlow(activity, webView, progressBar, monriApi),
                new ActivityPaymentApprovedFlow(activity, webView, progressBar, params),
                new ActivityPaymentDeclinedFlow(activity, webView, progressBar),
                new UnknownFlowImpl(),
                new PaymentErrorFlowImpl(activity, params)
        );
    }

    @Override
    public void onSuccess(ConfirmPaymentResponse result) {

        if (result == null) {
            paymentErrorFlow.handleResult(new IllegalStateException("Result == null"));
        } else {
            switch (result.getStatus()) {
                case ACTION_REQUIRED:
                    actionRequiredFlow.handleResult(result);
                    break;
                case DECLINED:
                    paymentDeclinedFlow.handleResult(result);
                    break;
                case APPROVED:
                    paymentApprovedFlow.handleResult(result);
                    break;
                default:
                    unknownFlow.handleResult(result);
                    onError(new IllegalStateException(String.format("Status %s not supported", result.getStatus())));
            }
        }

    }

    @Override
    public void onError(Throwable throwable) {
        paymentErrorFlow.handleResult(throwable);
    }
}
