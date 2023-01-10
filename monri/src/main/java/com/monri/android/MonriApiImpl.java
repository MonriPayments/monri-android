package com.monri.android;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.MerchantCustomers;
import com.monri.android.model.CreateCustomerParams;
import com.monri.android.model.DeleteCustomerParams;
import com.monri.android.model.DeleteCustomerResponse;
import com.monri.android.model.CustomerPaymentMethodParams;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.Customer;
import com.monri.android.model.RetrieveCustomerViaMerchantCustomerUuidParams;
import com.monri.android.model.RetrieveCustomerParams;
import com.monri.android.model.UpdateCustomerParams;
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
            final CreateCustomerParams createCustomerParams,
            final ResultCallback<Customer> callback
    ) {
        taskRunner.executeAsync(
                () -> {
                    MonriHttpResult<Customer> result = monriHttpApi.createCustomer(createCustomerParams);
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
            final UpdateCustomerParams updateCustomerParams,
            final ResultCallback<Customer> callback
    ) {
        taskRunner.executeAsync(
                () -> {
                    MonriHttpResult<Customer> result = monriHttpApi.updateCustomer(updateCustomerParams);
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
            final DeleteCustomerParams deleteCustomerParams,
            final ResultCallback<DeleteCustomerResponse> callback
    ) {
        taskRunner.executeAsync(
                () -> {
                    final MonriHttpResult<DeleteCustomerResponse> result = monriHttpApi.deleteCustomer(deleteCustomerParams);
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
    public void retrieveCustomer(final RetrieveCustomerParams retrieveCustomerParams, final ResultCallback<Customer> callback) {
        taskRunner.executeAsync(
                () -> {
                    final MonriHttpResult<Customer> result = monriHttpApi.retrieveCustomer(retrieveCustomerParams);
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
    public void retrieveCustomerViaMerchantCustomerUuid(final RetrieveCustomerViaMerchantCustomerUuidParams retrieveCustomerViaMerchantCustomerUuidParams, final ResultCallback<Customer> callback) {
        taskRunner.executeAsync(
                () -> {
                    final MonriHttpResult<Customer> result = monriHttpApi.retrieveCustomerViaMerchantCustomerId(retrieveCustomerViaMerchantCustomerUuidParams);
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
    public void retrieveCustomerPaymentMethods(final CustomerPaymentMethodParams customerPaymentMethodParams, final ResultCallback<CustomerPaymentMethodResponse> callback) {
        taskRunner.executeAsync(
                () -> {
                    final MonriHttpResult<CustomerPaymentMethodResponse> result = monriHttpApi.getPaymentMethodsForCustomer(customerPaymentMethodParams);
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
    public void retrieveAllCustomers(final String accessToken, ResultCallback<MerchantCustomers> callback) {
        taskRunner.executeAsync(
                () -> {
                    MonriHttpResult<MerchantCustomers> result = monriHttpApi.retrieveAllCustomers(accessToken);
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
