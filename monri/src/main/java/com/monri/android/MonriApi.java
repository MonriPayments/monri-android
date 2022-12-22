package com.monri.android;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.CustomerDeleteRequest;
import com.monri.android.model.CustomerDeleteResponse;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.CustomerCreateRequest;
import com.monri.android.model.CustomerResponse;
import com.monri.android.model.CustomerRetrieveMerchantIdRequest;
import com.monri.android.model.CustomerRetrieveRequest;
import com.monri.android.model.CustomerPaymentMethodRequest;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;
import com.monri.android.model.CustomerUpdateRequest;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public interface MonriApi {

    void confirmPayment(ConfirmPaymentParams params, ResultCallback<ConfirmPaymentResponse> callback);

    void paymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback);

    void createCustomer(final CustomerCreateRequest customerCreateRequest, final ResultCallback<CustomerResponse> callback);

    void updateCustomer(final CustomerUpdateRequest customerUpdateRequest, final ResultCallback<CustomerResponse> callback);

    void deleteCustomer(final CustomerDeleteRequest customerDeleteRequest, final ResultCallback<CustomerDeleteResponse> callback);

    void retrieveCustomer(final CustomerRetrieveRequest customerRetrieveRequest, final ResultCallback<CustomerResponse> callback);

    void retrieveCustomerViaMerchantCustomerId(final CustomerRetrieveMerchantIdRequest customerRetrieveMerchantIdRequest, final ResultCallback<CustomerResponse> callback);

    void getAllCustomers(final String accessToken, ResultCallback<Object> callback);

    void retrieveCustomerPaymentMethods(final CustomerPaymentMethodRequest customerPaymentMethodRequest, final ResultCallback<CustomerPaymentMethodResponse> callback);

}
