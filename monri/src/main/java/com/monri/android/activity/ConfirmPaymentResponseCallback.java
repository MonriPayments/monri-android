package com.monri.android.activity;

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
import com.monri.android.model.ConfirmPaymentResponse;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
final class ConfirmPaymentResponseCallback implements ResultCallback<ConfirmPaymentResponse> {

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

    public static ConfirmPaymentResponseCallback create(UiDelegate uiDelegate, MonriApi monriApi) {
        return new ConfirmPaymentResponseCallback(
                new ActivityActionRequiredFlow(uiDelegate, monriApi),
                new ActivityPaymentApprovedFlow(uiDelegate),
                new ActivityPaymentDeclinedFlow(uiDelegate),
                new UnknownFlowImpl(),
                new PaymentErrorFlowImpl(uiDelegate)
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
