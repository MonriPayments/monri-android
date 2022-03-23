package com.monri.android;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.CustomerRequest;
import com.monri.android.model.CustomerResponse;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public interface MonriApi {

    void confirmPayment(ConfirmPaymentParams params, ResultCallback<ConfirmPaymentResponse> callback);

    void paymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback);

    void createCustomer(CustomerRequest customerRequest, ResultCallback<CustomerResponse> callback);

}
