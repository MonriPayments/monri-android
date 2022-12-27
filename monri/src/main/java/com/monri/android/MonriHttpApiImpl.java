package com.monri.android;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.CustomerAllResponse;
import com.monri.android.model.CustomerDeleteRequest;
import com.monri.android.model.CustomerDeleteResponse;
import com.monri.android.model.CustomerCreateRequest;
import com.monri.android.model.CustomerPaymentMethodRequest;
import com.monri.android.model.CustomerPaymentMethodResponse;
import com.monri.android.model.CustomerResponse;
import com.monri.android.model.CustomerRetrieveMerchantIdRequest;
import com.monri.android.model.CustomerRetrieveRequest;
import com.monri.android.model.CustomerUpdateRequest;
import com.monri.android.model.PaymentMethodParams;
import com.monri.android.model.PaymentStatusResponse;
import com.monri.android.model.TransactionParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MonriHttpApiImpl implements MonriHttpApi {

    private final String baseUrl;
    private final Map<String, String> headers;

    private HttpURLConnection createHttpURLConnection(
            final String endpoint,
            final MonriHttpMethod monriHttpMethod,
            final Map<String, String> additionalHeader
    ) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(monriHttpMethod.getValue());

        switch (monriHttpMethod) {
            case GET:
                break;
            case POST:
                urlConnection.setDoInput(true);//Allow Inputs
                urlConnection.setDoOutput(true);//Allow Outputs
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setUseCaches(false);//Don't use a cached Copy
                break;
            default:
        }

        for (String key : headers.keySet()) {
            urlConnection.setRequestProperty(key, headers.get(key));
        }

        for (String key : additionalHeader.keySet()) {
            urlConnection.setRequestProperty(key, additionalHeader.get(key));
        }

        return urlConnection;

    }

    private MonriHttpResult<JSONObject> httpsPOST(
            final String endpoint,
            final JSONObject body,
            final Map<String, String> additionalHeader
    ) {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = createHttpURLConnection(endpoint, MonriHttpMethod.POST, additionalHeader);

            OutputStreamWriter wr = null;

            try {
                wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(body.toString());
                wr.flush();

            } finally {
                if (wr != null) {
                    wr.close();
                }
            }

            //now read response
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonStringResponse = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    jsonStringResponse.append(line).append('\n');
                }

                JSONObject jsonResponse = new JSONObject(jsonStringResponse.toString());

                //todo what if backend return response without data e.g. 401, 403, 404 etc.
                return MonriHttpResult.success(jsonResponse, urlConnection.getResponseCode());

            } finally {
                urlConnection.disconnect();
            }


        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }

    }

    private MonriHttpResult<JSONObject> httpsGET(
            final String endpoint,
            final Map<String, String> additionalHeader
    ) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createHttpURLConnection(endpoint, MonriHttpMethod.GET, additionalHeader);

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonStringResponse = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    jsonStringResponse.append(line).append('\n');
                }

                final JSONObject jsonResponse = new JSONObject(jsonStringResponse.toString());

                return MonriHttpResult.success(jsonResponse, urlConnection.getResponseCode());

            } finally {
                urlConnection.disconnect();
            }

        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }

    }

    private MonriHttpResult<JSONObject> httpsDELETE(
            final String endpoint,
            final Map<String, String> additionalHeader
    ) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createHttpURLConnection(endpoint, MonriHttpMethod.DELETE, additionalHeader);

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonStringResponse = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    jsonStringResponse.append(line).append('\n');
                }

                final JSONObject jsonResponse = new JSONObject(jsonStringResponse.toString());

                return MonriHttpResult.success(jsonResponse, urlConnection.getResponseCode());

            } finally {
                urlConnection.disconnect();
            }

        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }

    }

    public MonriHttpApiImpl(final String baseUrl, final Map<String, String> headers) {
        this.baseUrl = baseUrl;
        this.headers = headers;
    }

    //post v2/payment/{id}/confirm
    @Override
    public MonriHttpResult<ConfirmPaymentResponse> confirmPayment(@NonNull ConfirmPaymentParams confirmPaymentParams) {
        try {
            final MonriHttpResult<JSONObject> response = httpsPOST(
                    baseUrl + "/v2/payment/" + confirmPaymentParams.getPaymentId() + "/confirm",
                    confirmPaymentParamsToJSON(confirmPaymentParams),
                    new HashMap<>()
            );
            return MonriHttpResult.success(ConfirmPaymentResponse.fromJSON(response.getResult()), response.getResponseCode());
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    //get v2/payment/{id}/status
    @Override
    public MonriHttpResult<PaymentStatusResponse> paymentStatus(String id) {
        final MonriHttpResult<JSONObject> response = httpsGET(baseUrl + "/v2/payment/" + id + "/status", new HashMap<>());

        try {
            if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                return MonriHttpResult.success(PaymentStatusResponse.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(MonriHttpException.create(response.getResult().toString(), MonriHttpExceptionCode.REQUEST_FAILED));
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    //post v2/customers
    @Override
    public MonriHttpResult<CustomerResponse> createCustomer(@NonNull final CustomerCreateRequest customerCreateRequest) {
        try {
            final MonriHttpResult<JSONObject> response = httpsPOST(
                    baseUrl + "/v2/customers",
                    customerCreateRequest.getCustomer().toJSON(),
                    new HashMap<>(){{
                        put("authorization", customerCreateRequest.getAccessToken());
                    }}
            );
            return MonriHttpResult.success(CustomerResponse.fromJSON(response.getResult()), response.getResponseCode());
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<CustomerResponse> retrieveCustomer(final CustomerRetrieveRequest customerRetrieveRequest) {
        final MonriHttpResult<JSONObject> response = httpsGET(
                baseUrl + "/v2/customers/" + customerRetrieveRequest.getCustomerUuid(),
                new HashMap<>(){{
                    put("authorization", customerRetrieveRequest.getAccessToken());
                }}
        );

        try {
            if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                return MonriHttpResult.success(CustomerResponse.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(MonriHttpException.create(response.getResult().toString(), MonriHttpExceptionCode.REQUEST_FAILED));
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<CustomerResponse> retrieveCustomerViaMerchantCustomerId(final CustomerRetrieveMerchantIdRequest customerRetrieveMerchantIdRequest) {
        final MonriHttpResult<JSONObject> response = httpsGET(
                baseUrl + "/v2/merchants/customers/" + customerRetrieveMerchantIdRequest.getMerchantCustomerUuid(),
                new HashMap<>(){{
                    put("authorization", customerRetrieveMerchantIdRequest.getAccessToken());
                }}
        );

        try {
            if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                return MonriHttpResult.success(CustomerResponse.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(MonriHttpException.create(response.getResult().toString(), MonriHttpExceptionCode.REQUEST_FAILED));
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<CustomerResponse> updateCustomer(@NonNull final CustomerUpdateRequest customerUpdateRequest) {
        try {
            final MonriHttpResult<JSONObject> response = httpsPOST(
                    baseUrl + "/v2/customers/" + customerUpdateRequest.getCustomerUuid(),
                    customerUpdateRequest.getCustomer().toJSON(),
                    new HashMap<>(){{
                        put("authorization", customerUpdateRequest.getAccessToken());
                    }}
            );
            return MonriHttpResult.success(CustomerResponse.fromJSON(response.getResult()), response.getResponseCode());
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<CustomerDeleteResponse> deleteCustomer(final CustomerDeleteRequest customerDeleteRequest) {
        final MonriHttpResult<JSONObject> response = httpsDELETE(
                baseUrl + "/v2/customers/" + customerDeleteRequest.getCustomerUuid(),
                new HashMap<>(){{
                    put("authorization", customerDeleteRequest.getAccessToken());
                }}
        );

        try {
            if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                return MonriHttpResult.success(CustomerDeleteResponse.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(MonriHttpException.create(response.getResult().toString(), MonriHttpExceptionCode.REQUEST_FAILED));
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<CustomerAllResponse> getAllCustomers(final String accessToken) {
        final MonriHttpResult<JSONObject> response = httpsGET(
                baseUrl + "/v2/customers",
                new HashMap<>(){{
                    put("authorization", accessToken);
                }}
        );

        try {
            if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                final CustomerAllResponse customerAllResponse = CustomerAllResponse.fromJSON(response.getResult());
                return MonriHttpResult.success(customerAllResponse, response.getResponseCode());
            } else {
                return MonriHttpResult.failed(MonriHttpException.create(response.getResult().toString(), MonriHttpExceptionCode.REQUEST_FAILED));
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<CustomerPaymentMethodResponse> getPaymentMethodsForCustomer(final CustomerPaymentMethodRequest customerPaymentMethodRequest) {
        final MonriHttpResult<JSONObject> response = httpsGET(
                baseUrl +
                        "/v2/customers/" +
                        customerPaymentMethodRequest.getMonriCustomerUuid() +
                        "/payment-methods?limit=" +
                        customerPaymentMethodRequest.getLimit() +
                        "&offset="
                        + customerPaymentMethodRequest.getOffset(),
                new HashMap<>(){{
                    put("authorization", customerPaymentMethodRequest.getAccessToken());
                }}
        );

        try {
            if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                return MonriHttpResult.success(CustomerPaymentMethodResponse.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(MonriHttpException.create(response.getResult().toString(), MonriHttpExceptionCode.REQUEST_FAILED));
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

        JSONObject dataMapJSON = new JSONObject();

        for (String key : data.keySet()) {
            dataMapJSON.put(key, data.get(key));
        }

        JSONObject paymentMethodJSON = new JSONObject();

        paymentMethodJSON.put("type", type);
        paymentMethodJSON.put("data", dataMapJSON);

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
