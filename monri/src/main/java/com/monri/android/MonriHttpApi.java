package com.monri.android;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.MerchantCustomers;
import com.monri.android.model.DeleteCustomerParams;
import com.monri.android.model.DeleteCustomerResponse;
import com.monri.android.model.CustomerPaymentMethodParams;
import com.monri.android.model.CreateCustomerParams;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.Customer;
import com.monri.android.model.RetrieveCustomerViaMerchantCustomerUuidParams;
import com.monri.android.model.GetCustomerParams;
import com.monri.android.model.UpdateCustomerParams;
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
    MonriHttpResult<Customer> createCustomer(final CreateCustomerParams createCustomerParams);

    //update customer v2/customers
    MonriHttpResult<Customer> updateCustomer(final UpdateCustomerParams updateCustomerParams);

    //delete customer v2/customers
    MonriHttpResult<DeleteCustomerResponse> deleteCustomer(final DeleteCustomerParams deleteCustomerParams);

    //retrieve customer /v2/customers/:uuid
    MonriHttpResult<Customer> retrieveCustomer(final GetCustomerParams retrieveCustomerParams);

    //Retrieve a customer via merchant_customer_id /v2/merchants/customers/:merchant_customer_id
    MonriHttpResult<Customer> retrieveCustomerViaMerchantCustomerId(final RetrieveCustomerViaMerchantCustomerUuidParams retrieveCustomerViaMerchantCustomerUuidParams);

    //get customers
    MonriHttpResult<MerchantCustomers> retrieveAllCustomers(final String accessToken);

    //get all paymentMethods
    MonriHttpResult<CustomerPaymentMethodResponse> getPaymentMethodsForCustomer(final CustomerPaymentMethodParams customerPaymentMethodParams);
}
