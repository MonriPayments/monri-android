package com.monri.android;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import com.monri.android.model.BrowserInfo;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.GooglePayPayment;
import com.monri.android.model.MerchantCustomers;
import com.monri.android.model.DeleteCustomerParams;
import com.monri.android.model.DeleteCustomerResponse;
import com.monri.android.model.CreateCustomerParams;
import com.monri.android.model.CustomerPaymentMethodParams;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.Customer;
import com.monri.android.model.RetrieveCustomerViaMerchantCustomerUuidParams;
import com.monri.android.model.GetCustomerParams;
import com.monri.android.model.UpdateCustomerParams;
import com.monri.android.model.PaymentMethodParams;
import com.monri.android.model.PaymentStatusResponse;
import com.monri.android.model.TransactionParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

class MonriHttpApiImpl implements MonriHttpApi {

    private final Map<String, String> headers;
    private final HttpsClientProxyImpl httpsClientProxy;
    private final MonriUrlProvider urlProvider;
    private static final String GOOGLE_PAY_TOKENIZATION_DATA_KEY = "tokenizationData";
    private static final String GOOGLE_PAY_TOKEN_KEY = "token";
    private static final String PAYMENT_METHOD_DATA_KEY = "data";
    private static final String PAYMENT_METHOD_TYPE_KEY = "type";
    private static final String AUTHORIZATION_HEADER_KEY = "authorization";
    private static final String BROWSER_INFO_KEY = "browser_info";

    public MonriHttpApiImpl(final String baseUrl, final Map<String, String> headers) {
        this.headers = headers;
        this.httpsClientProxy = new HttpsClientProxyImpl();
        this.urlProvider = new MonriUrlProvider(baseUrl);
    }

    @Override
    public MonriHttpResult<ConfirmPaymentResponse> confirmPayment(@NonNull final ConfirmPaymentParams confirmPaymentParams) {
        try {
            return httpsClientProxy.doPostRequest(
                    urlProvider.getConfirmPaymentUrl(confirmPaymentParams.getPaymentId()),
                    headers,
                    confirmPaymentParamsToJSON(confirmPaymentParams).toString(),
                    false,
                    ConfirmPaymentResponse::fromJSON
            );
        } catch (final Exception e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<JSONObject> startGooglePayPayment(final String paymentId) {
        return httpsClientProxy.doPostRequest(
                urlProvider.getGooglePayPaymentUrl(paymentId),
                headers,
                null,
                false,
                (json) -> json
        );
    }

    //get v2/payment/{id}/status
    @Override
    public MonriHttpResult<PaymentStatusResponse> paymentStatus(final String id) {
        return httpsClientProxy.doGetRequest(
                urlProvider.getPaymentStatusUrl(id),
                new HashMap<>(),
                PaymentStatusResponse::fromJSON
        );
    }

    //post v2/customers
    @Override
    public MonriHttpResult<Customer> createCustomer(@NonNull final CreateCustomerParams createCustomerParams) {
        try {
            return httpsClientProxy.doPostRequest(
                    urlProvider.getCustomersBaseUrl(),
                    createAuthorizationHeader(createCustomerParams.getAccessToken()),
                    createCustomerParams.getCustomer().toJSON().toString(),
                    true,
                    Customer::fromJSON
            );
        } catch (final JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<Customer> retrieveCustomer(final GetCustomerParams retrieveCustomerParams) {
        return httpsClientProxy.doGetRequest(
                urlProvider.getCustomerUrl(retrieveCustomerParams.getCustomerUuid()),
                createAuthorizationHeader(retrieveCustomerParams.getAccessToken()),
                Customer::fromJSON
        );
    }

    @Override
    public MonriHttpResult<Customer> retrieveCustomerViaMerchantCustomerId(final RetrieveCustomerViaMerchantCustomerUuidParams retrieveCustomerViaMerchantCustomerUuidParams) {
        return httpsClientProxy.doGetRequest(
                urlProvider.getRetrieveCustomerViaMerchantCustomerIdUrl(retrieveCustomerViaMerchantCustomerUuidParams.getMerchantCustomerUuid()),
                createAuthorizationHeader(retrieveCustomerViaMerchantCustomerUuidParams.getAccessToken()),
                Customer::fromJSON
        );
    }

    @Override
    public MonriHttpResult<Customer> updateCustomer(@NonNull final UpdateCustomerParams updateCustomerParams) {
        try {
            return httpsClientProxy.doPostRequest(
                    urlProvider.getCustomerUrl(updateCustomerParams.getCustomerUuid()),
                    createAuthorizationHeader(updateCustomerParams.getAccessToken()),
                    updateCustomerParams.getCustomer().toJSON().toString(),
                    true,
                    Customer::fromJSON
            );
        } catch (final JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<DeleteCustomerResponse> deleteCustomer(final DeleteCustomerParams deleteCustomerParams) {
        return httpsClientProxy.doDeleteRequest(
                urlProvider.getCustomerUrl(deleteCustomerParams.getCustomerUuid()),
                createAuthorizationHeader(deleteCustomerParams.getAccessToken()),
                DeleteCustomerResponse::fromJSON
        );
    }

    @Override
    public MonriHttpResult<MerchantCustomers> retrieveAllCustomers(final String accessToken) {
        return httpsClientProxy.doGetRequest(
                urlProvider.getCustomersBaseUrl(),
                createAuthorizationHeader(accessToken),
                MerchantCustomers::fromJSON
        );
    }

    @Override
    public MonriHttpResult<CustomerPaymentMethodResponse> getPaymentMethodsForCustomer(final CustomerPaymentMethodParams customerPaymentMethodParams) {
        return httpsClientProxy.doGetRequest(
                urlProvider.getPaymentMethodForCustomerUrl(
                        customerPaymentMethodParams.getCustomerUuid(),
                        customerPaymentMethodParams.getLimit(),
                        customerPaymentMethodParams.getOffset()
                ),
                createAuthorizationHeader(customerPaymentMethodParams.getAccessToken()),
                CustomerPaymentMethodResponse::fromJSON
        );
    }

    private Map<String, String> createAuthorizationHeader(final String authorizationValue) {
        return  new HashMap<>() {{
            put(AUTHORIZATION_HEADER_KEY, authorizationValue);
        }};
    }

    @NonNull
    @VisibleForTesting
    public static JSONObject confirmPaymentParamsToJSON(@NonNull final ConfirmPaymentParams confirmPaymentParams) throws JSONException {
        final PaymentMethodParams paymentMethodParams = confirmPaymentParams.getPaymentMethod();
        final String type = paymentMethodParams.getType();
        final Map<String, String> data = paymentMethodParams.getData();

        final JSONObject paymentMethodJSON = new JSONObject();
        paymentMethodJSON.put(PAYMENT_METHOD_TYPE_KEY, type);

        if (type.equals(GooglePayPayment.TYPE_GOOGLE_PAY)) {
            final JSONObject googlePaymentMethodObject = new JSONObject(data.get(GooglePayPayment.GOOGLE_PAYMENT_METHOD_DATA_KEY));
            final JSONObject tokenDataObject = new JSONObject(googlePaymentMethodObject.getJSONObject(GOOGLE_PAY_TOKENIZATION_DATA_KEY).getString(GOOGLE_PAY_TOKEN_KEY));

            paymentMethodJSON.put(PAYMENT_METHOD_DATA_KEY, tokenDataObject);
        } else {
            final JSONObject dataMapJSON = new JSONObject();

            for (final String key : data.keySet()) {
                dataMapJSON.put(key, data.get(key));
            }

            paymentMethodJSON.put(PAYMENT_METHOD_DATA_KEY, dataMapJSON);
        }

        //converting transactionParams to JSON
        final TransactionParams transaction = confirmPaymentParams.getTransaction();
        final Map<String, Object> transactionData = pruneTransactionDataSetMetaData(transaction.getData());
        final JSONObject dataTransactionMapJSON = new JSONObject();

        for (final String key : transactionData.keySet()) {
            dataTransactionMapJSON.put(key, transactionData.get(key));
        }

        final BrowserInfo browserInfo = confirmPaymentParams.getBrowserInfo();
        if (browserInfo != null) {
            dataTransactionMapJSON.put(BROWSER_INFO_KEY, browserInfo.toJSON());
        }

        final JSONObject confirmPaymentParamsJSON = new JSONObject();
        confirmPaymentParamsJSON.put("payment_method", paymentMethodJSON);
        confirmPaymentParamsJSON.put("transaction", dataTransactionMapJSON);
        return confirmPaymentParamsJSON;
    }

    private static Map<String, Object> pruneTransactionDataSetMetaData(final Map<String, String> transactionData) throws JSONException {
        final Map<String, Object> returnValue = new HashMap<>(transactionData);
        final JSONObject meta = new JSONObject();

        for (final String metaKey : MetaUtility.META_KEYS) {
            // integration_type
            // meta.integration_type
            final String key = String.format("meta.%s", metaKey);
            if (transactionData.containsKey(key)) {
                meta.put(metaKey, transactionData.get(key));
                returnValue.remove(key);
            }
        }

        if (meta.length() > 0) {
            returnValue.put("meta", meta);
        }

        return returnValue;
    }
}
