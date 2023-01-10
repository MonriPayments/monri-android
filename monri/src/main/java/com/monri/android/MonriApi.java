package com.monri.android;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.MerchantCustomers;
import com.monri.android.model.DeleteCustomerParams;
import com.monri.android.model.DeleteCustomerResponse;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.CreateCustomerParams;
import com.monri.android.model.Customer;
import com.monri.android.model.RetrieveCustomerViaMerchantCustomerUuidParams;
import com.monri.android.model.RetrieveCustomerParams;
import com.monri.android.model.CustomerPaymentMethodParams;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;
import com.monri.android.model.UpdateCustomerParams;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public interface MonriApi {

    void confirmPayment(ConfirmPaymentParams params, ResultCallback<ConfirmPaymentResponse> callback);

    void paymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback);

    void createCustomer(final CreateCustomerParams createCustomerParams, final ResultCallback<Customer> callback);

    void updateCustomer(final UpdateCustomerParams updateCustomerParams, final ResultCallback<Customer> callback);

    void deleteCustomer(final DeleteCustomerParams deleteCustomerParams, final ResultCallback<DeleteCustomerResponse> callback);

    void retrieveCustomer(final RetrieveCustomerParams retrieveCustomerParams, final ResultCallback<Customer> callback);

    void retrieveCustomerViaMerchantCustomerUuid(final RetrieveCustomerViaMerchantCustomerUuidParams retrieveCustomerViaMerchantCustomerUuidParams, final ResultCallback<Customer> callback);

    void retrieveAllCustomers(final String accessToken, ResultCallback<MerchantCustomers> callback);

    void retrieveCustomerPaymentMethods(final CustomerPaymentMethodParams customerPaymentMethodParams, final ResultCallback<CustomerPaymentMethodResponse> callback);
}
