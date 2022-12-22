package com.monri.android;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.CustomerDeleteRequest;
import com.monri.android.model.CustomerDeleteResponse;
import com.monri.android.model.CustomerPaymentMethodRequest;
import com.monri.android.model.CustomerCreateRequest;
import com.monri.android.model.CustomerResponse;
import com.monri.android.model.CustomerRetrieveMerchantIdRequest;
import com.monri.android.model.CustomerRetrieveRequest;
import com.monri.android.model.CustomerUpdateRequest;
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
    MonriHttpResult<CustomerResponse> createCustomer(final CustomerCreateRequest customerCreateRequest);

    //update customer v2/customers
    MonriHttpResult<CustomerResponse> updateCustomer(final CustomerUpdateRequest customerUpdateRequest);

    //delete customer v2/customers
    MonriHttpResult<CustomerDeleteResponse> deleteCustomer(final CustomerDeleteRequest customerDeleteRequest);

    //retrieve customer /v2/customers/:uuid
    MonriHttpResult<CustomerResponse> retrieveCustomer(final CustomerRetrieveRequest customerRetrieveRequest);

    //Retrieve a customer via merchant_customer_id /v2/merchants/customers/:merchant_customer_id
    MonriHttpResult<CustomerResponse> retrieveCustomerViaMerchantCustomerId(final CustomerRetrieveMerchantIdRequest customerRetrieveMerchantIdRequest);

    //get customers
    MonriHttpResult<Object> getAllCustomers(final String accessToken);

    //get all paymentMethods
    MonriHttpResult<Object> getPaymentMethodsForCustomer(final CustomerPaymentMethodRequest customerPaymentMethodRequest);
}
