package com.monri.android;

import com.monri.android.model.CreateCustomerParams;
import com.monri.android.model.Customer;
import com.monri.android.model.CustomerPaymentMethodParams;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.DeleteCustomerParams;
import com.monri.android.model.DeleteCustomerResponse;
import com.monri.android.model.MerchantCustomers;
import com.monri.android.model.GetCustomerParams;
import com.monri.android.model.RetrieveCustomerViaMerchantCustomerUuidParams;
import com.monri.android.model.UpdateCustomerParams;

public class CustomerApi {
    private final MonriHttpApi monriHttpApi;
    private final TaskRunner taskRunner;

    public CustomerApi(MonriHttpApi monriHttpApi, TaskRunner taskRunner) {
        this.monriHttpApi = monriHttpApi;
        this.taskRunner = taskRunner;
    }


    public void create(
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

    public void update(
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

    public void delete(
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

    public void get(final GetCustomerParams retrieveCustomerParams, final ResultCallback<Customer> callback) {
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

    public void getViaMerchantCustomerUuid(final RetrieveCustomerViaMerchantCustomerUuidParams retrieveCustomerViaMerchantCustomerUuidParams, final ResultCallback<Customer> callback) {
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

    public void paymentMethods(final CustomerPaymentMethodParams customerPaymentMethodParams, final ResultCallback<CustomerPaymentMethodResponse> callback) {
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

    public void all(final String accessToken, ResultCallback<MerchantCustomers> callback) {
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
