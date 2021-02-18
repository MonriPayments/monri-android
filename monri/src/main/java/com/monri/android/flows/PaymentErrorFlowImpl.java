package com.monri.android.flows;

import android.app.Activity;
import android.content.Intent;

import com.monri.android.ApiException;
import com.monri.android.model.ConfirmPaymentParams;
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

    private final Activity activity;
    private final ConfirmPaymentParams paymentParams;

    public PaymentErrorFlowImpl(Activity activity, ConfirmPaymentParams paymentParams) {
        this.activity = activity;
        this.paymentParams = paymentParams;
    }

    @Override
    public void handleResult(Throwable throwable) {
        Intent intent = new Intent();
        List<String> messages = new ArrayList<>();
        if (throwable instanceof ApiException) {
            messages.addAll(((ApiException) throwable).getErrors());
        } else {
            // TODO: improve errors api
            messages.add(String.format("Unknown exception occurred, class = [%s]", throwable.getClass().getName()));
            messages.add(String.format("Message = [%s]", throwable.getMessage()));
            Writer writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            messages.add(String.format("Stack trace = [%s]", writer.toString()));
        }

        PaymentResult paymentResult = new PaymentResult(PaymentStatus.DECLINED.getStatus(), messages);
        intent.putExtra(PaymentResult.BUNDLE_NAME, paymentResult);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();

    }
}
