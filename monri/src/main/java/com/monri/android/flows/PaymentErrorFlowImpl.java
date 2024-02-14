package com.monri.android.flows;

import com.monri.android.ApiException;
import com.monri.android.activity.UiDelegate;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.PaymentStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasminsuljic on 2019-12-09.
 * MonriAndroid
 */
public class PaymentErrorFlowImpl implements PaymentErrorFlow {

    private final UiDelegate uiDelegate;

    public PaymentErrorFlowImpl(final UiDelegate uiDelegate) {
        this.uiDelegate = uiDelegate;
    }

    @Override
    public void handleResult(Throwable throwable) {

        final List<String> messages = new ArrayList<>();

        if (throwable instanceof ApiException) {
            messages.addAll(((ApiException) throwable).getErrors());
        } else {
            messages.add(String.format("Unknown exception occurred, class = [%s]", throwable.getClass().getName()));
            messages.add(String.format("Message = [%s]", throwable.getMessage()));

            final Writer writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            messages.add(String.format("Stack trace = [%s]", writer));
        }

        final PaymentResult paymentResult = new PaymentResult(PaymentStatus.DECLINED.getStatus(), messages);
        uiDelegate.handlePaymentResult(paymentResult);
    }
}
