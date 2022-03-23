package com.monri.android;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.CustomerRequest;
import com.monri.android.model.CustomerResponse;
import com.monri.android.model.PaymentStatusResponse;

import java.util.Map;

interface MonriHttpApi {

    static MonriHttpApi create(final String baseUrl, final Map<String, String> headers) {
        return new MonriHttpApiImpl(
                baseUrl,
                headers
        );
    }

    //post v2/payment/{id}/confirm
    MonriHttpResult<ConfirmPaymentResponse> confirmPayment(ConfirmPaymentParams confirmPaymentParams);

    //get v2/payment/{id}/status
    MonriHttpResult<PaymentStatusResponse> paymentStatus(String id);

    //create customer v2/customers
    MonriHttpResult<CustomerResponse> createCustomer(CustomerRequest customerRequest);
}
