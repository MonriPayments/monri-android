package com.monri.android;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.CustomerCreateRequest;
import com.monri.android.model.CustomerDeleteRequest;
import com.monri.android.model.CustomerDeleteResponse;
import com.monri.android.model.CustomerPaymentMethodRequest;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.CustomerResponse;
import com.monri.android.model.CustomerRetrieveMerchantIdRequest;
import com.monri.android.model.CustomerRetrieveRequest;
import com.monri.android.model.CustomerUpdateRequest;
import com.monri.android.model.PaymentStatusParams;
import com.monri.android.model.PaymentStatusResponse;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
class MonriApiImpl implements MonriApi {

    private final MonriHttpApi monriHttpApi;
    private final TaskRunner taskRunner;

    MonriApiImpl(final MonriHttpApi monriHttpApi) {
        this.monriHttpApi = monriHttpApi;
        this.taskRunner = new TaskRunner();
    }

    @Override
    public void confirmPayment(ConfirmPaymentParams params, ResultCallback<ConfirmPaymentResponse> callback) {
        taskRunner.executeAsync(
                () -> {
                    MonriHttpResult<ConfirmPaymentResponse> result = monriHttpApi.confirmPayment(params);
                    if (result.getCause() != null) {
                        throw result.getCause();
                    } else {
                        return result.getResult();
                    }
                },
                callback);
    }

    @Override
    public void paymentStatus(PaymentStatusParams params, ResultCallback<PaymentStatusResponse> callback) {
        taskRunner.executeAsync(
                () -> {
                    MonriHttpResult<PaymentStatusResponse> result = monriHttpApi.paymentStatus(params.getClientSecret());
                    if (result.getCause() != null) {
                        throw result.getCause();
                    } else {
                        return result.getResult();
                    }
                },
                callback
        );
    }

    @Override
    public void createCustomer(
            final CustomerCreateRequest customerCreateRequest,
            final ResultCallback<CustomerResponse> callback
    ) {
        taskRunner.executeAsync(
                () -> {
                    MonriHttpResult<CustomerResponse> result = monriHttpApi.createCustomer(customerCreateRequest);
                    if (result.getCause() != null) {
                        throw result.getCause();
                    } else {
                        return result.getResult();
                    }
                },
                callback
        );
    }

    @Override
    public void updateCustomer(
            final CustomerUpdateRequest customerUpdateRequest,
            final ResultCallback<CustomerResponse> callback
    ) {
        taskRunner.executeAsync(
                () -> {
                    MonriHttpResult<CustomerResponse> result = monriHttpApi.updateCustomer(customerUpdateRequest);
                    if (result.getCause() != null) {
                        throw result.getCause();
                    } else {
                        return result.getResult();
                    }
                },
                callback
        );
    }

    @Override
    public void deleteCustomer(
            final CustomerDeleteRequest customerDeleteRequest,
            final ResultCallback<CustomerDeleteResponse> callback
    ) {
        taskRunner.executeAsync(
                () -> {
                    final MonriHttpResult<CustomerDeleteResponse> result = monriHttpApi.deleteCustomer(customerDeleteRequest);
                    if (result.getCause() != null) {
                        throw result.getCause();
                    } else {
                        return result.getResult();
                    }
                },
                callback
        );
    }

    @Override
    public void retrieveCustomer(final CustomerRetrieveRequest customerRetrieveRequest, final ResultCallback<CustomerResponse> callback) {

    }

    @Override
    public void retrieveCustomerViaMerchantCustomerId(final CustomerRetrieveMerchantIdRequest customerRetrieveMerchantIdRequest, final ResultCallback<CustomerResponse> callback) {

    }

    @Override
    public void retrieveCustomerPaymentMethods(final CustomerPaymentMethodRequest customerPaymentMethodRequest, final ResultCallback<CustomerPaymentMethodResponse> callback) {

    }

    @Override
    public void getAllCustomers(final String accessToken, ResultCallback<Object> callback) {
        taskRunner.executeAsync(
                () -> {
                    MonriHttpResult<Object> result = monriHttpApi.getAllCustomers(accessToken);
                    if (result.getCause() != null) {
                        throw result.getCause();
                    } else {
                        return result.getResult();
                    }
                },
                callback
        );
    }
}
