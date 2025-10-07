package com.monri.android;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

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

    private final String baseUrl;
    private final Map<String, String> headers;
    private final HttpsClient httpsClient;
    private static final String GOOGLE_PAY_TOKENIZATION_DATA_KEY = "tokenizationData";
    private static final String GOOGLE_PAY_TOKEN_KEY = "token";
    private static final String PAYMENT_METHOD_DATA_KEY = "data";
    private static final String PAYMENT_METHOD_TYPE_KEY = "type";

    public MonriHttpApiImpl(final String baseUrl, final Map<String, String> headers) {
        this.baseUrl = baseUrl;
        this.headers = headers;
        this.httpsClient = new HttpsClient();
    }

    //post v2/payment/{id}/confirm
    @Override
    public MonriHttpResult<ConfirmPaymentResponse> confirmPayment(@NonNull ConfirmPaymentParams confirmPaymentParams) {
        try {
            final MonriHttpResult<JSONObject> response = httpsClient.httpsPOST(
                    baseUrl + "/v2/payment/" + confirmPaymentParams.getPaymentId() + "/confirm",
                    confirmPaymentParamsToJSON(confirmPaymentParams),
                    headers,
                    false
            );
            if (response.getCause() == null) {
                return MonriHttpResult.success(ConfirmPaymentResponse.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(response.getCause());
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<JSONObject> startGooglePayPayment(final String paymentId) {
        try {
            final MonriHttpResult<JSONObject> response = httpsClient.httpsPOST(
                    baseUrl + "/v2/google-pay/" + paymentId + "/start-payment",
                    headers
            );
            if (response.getCause() == null) {
                return MonriHttpResult.success((response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(response.getCause());
            }
        } catch (Exception e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    //get v2/payment/{id}/status
    @Override
    public MonriHttpResult<PaymentStatusResponse> paymentStatus(String id) {
        final MonriHttpResult<JSONObject> response = httpsClient.httpsGET(baseUrl + "/v2/payment/" + id + "/status", new HashMap<>());

        try {
            if (response.getCause() == null) {
                return MonriHttpResult.success(PaymentStatusResponse.fromJSON(response.getResult()), response.getResponseCode());
            }else {
                return MonriHttpResult.failed(response.getCause());
            }

        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    //post v2/customers
    @Override
    public MonriHttpResult<Customer> createCustomer(@NonNull final CreateCustomerParams createCustomerParams) {
        try {
            final MonriHttpResult<JSONObject> response = httpsClient.httpsPOST(
                    baseUrl + "/v2/customers",
                    createCustomerParams.getCustomer().toJSON(),
                    new HashMap<>() {{
                        put("authorization", createCustomerParams.getAccessToken());
                    }},
                    true
            );
            if (response.getCause() == null) {
                return MonriHttpResult.success(Customer.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(response.getCause());
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<Customer> retrieveCustomer(final GetCustomerParams retrieveCustomerParams) {
        final MonriHttpResult<JSONObject> response = httpsClient.httpsGET(
                baseUrl + "/v2/customers/" + retrieveCustomerParams.getCustomerUuid(),
                new HashMap<>() {{
                    put("authorization", retrieveCustomerParams.getAccessToken());
                }}
        );

        try {
            if (response.getCause() == null) {
                return MonriHttpResult.success(Customer.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(response.getCause());
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<Customer> retrieveCustomerViaMerchantCustomerId(final RetrieveCustomerViaMerchantCustomerUuidParams retrieveCustomerViaMerchantCustomerUuidParams) {
        final MonriHttpResult<JSONObject> response = httpsClient.httpsGET(
                baseUrl + "/v2/merchants/customers/" + retrieveCustomerViaMerchantCustomerUuidParams.getMerchantCustomerUuid(),
                new HashMap<>() {{
                    put("authorization", retrieveCustomerViaMerchantCustomerUuidParams.getAccessToken());
                }}
        );

        try {
            if (response.getCause() == null) {
                return MonriHttpResult.success(Customer.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(response.getCause());
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<Customer> updateCustomer(@NonNull final UpdateCustomerParams updateCustomerParams) {
        try {
            final MonriHttpResult<JSONObject> response = httpsClient.httpsPOST(
                    baseUrl + "/v2/customers/" + updateCustomerParams.getCustomerUuid(),
                    updateCustomerParams.getCustomer().toJSON(),
                    new HashMap<>() {{
                        put("authorization", updateCustomerParams.getAccessToken());
                    }},
                    true
            );

            if (response.getCause() == null) {
                return MonriHttpResult.success(Customer.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(response.getCause());
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<DeleteCustomerResponse> deleteCustomer(final DeleteCustomerParams deleteCustomerParams) {
        final MonriHttpResult<JSONObject> response = httpsClient.httpsDELETE(
                baseUrl + "/v2/customers/" + deleteCustomerParams.getCustomerUuid(),
                new HashMap<>() {{
                    put("authorization", deleteCustomerParams.getAccessToken());
                }}
        );

        try {
            if (response.getCause() == null) {
                return MonriHttpResult.success(DeleteCustomerResponse.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(response.getCause());
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<MerchantCustomers> retrieveAllCustomers(final String accessToken) {
        final MonriHttpResult<JSONObject> response = httpsClient.httpsGET(
                baseUrl + "/v2/customers",
                new HashMap<>() {{
                    put("authorization", accessToken);
                }}
        );

        try {
            if (response.getCause() == null) {
                final MerchantCustomers merchantCustomers = MerchantCustomers.fromJSON(response.getResult());
                return MonriHttpResult.success(merchantCustomers, response.getResponseCode());
            } else {
                return MonriHttpResult.failed(response.getCause());
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<CustomerPaymentMethodResponse> getPaymentMethodsForCustomer(final CustomerPaymentMethodParams customerPaymentMethodParams) {
        final MonriHttpResult<JSONObject> response = httpsClient.httpsGET(
                baseUrl +
                        "/v2/customers/" +
                        customerPaymentMethodParams.getCustomerUuid() +
                        "/payment-methods?limit=" +
                        customerPaymentMethodParams.getLimit() +
                        "&offset="
                        + customerPaymentMethodParams.getOffset(),
                new HashMap<>() {{
                    put("authorization", customerPaymentMethodParams.getAccessToken());
                }}
        );

        try {
            if (response.getCause() == null) {
                return MonriHttpResult.success(CustomerPaymentMethodResponse.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(response.getCause());
            }

        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @NonNull
    @VisibleForTesting
    public static JSONObject confirmPaymentParamsToJSON(@NonNull ConfirmPaymentParams confirmPaymentParams) throws JSONException {
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

            for (String key : data.keySet()) {
                dataMapJSON.put(key, data.get(key));
            }

            paymentMethodJSON.put(PAYMENT_METHOD_DATA_KEY, dataMapJSON);
        }

        //converting transactionParams to JSON
        final TransactionParams transaction = confirmPaymentParams.getTransaction();
        final Map<String, Object> transactionData = pruneTransactionDataSetMetaData(transaction.getData());
        JSONObject dataTransactionMapJSON = new JSONObject();

        for (String key : transactionData.keySet()) {
            dataTransactionMapJSON.put(key, transactionData.get(key));
        }

        JSONObject confirmPaymentParamsJSON = new JSONObject();
        confirmPaymentParamsJSON.put("payment_method", paymentMethodJSON);
        confirmPaymentParamsJSON.put("transaction", dataTransactionMapJSON);
        return confirmPaymentParamsJSON;
    }

    private static Map<String, Object> pruneTransactionDataSetMetaData(Map<String, String> transactionData) throws JSONException {
        Map<String, Object> returnValue = new HashMap<>(transactionData);
        JSONObject meta = new JSONObject();

        for (String metaKey : MetaUtility.META_KEYS) {
            // integration_type
            // meta.integration_type
            String key = String.format("meta.%s", metaKey);
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
