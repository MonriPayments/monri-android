package com.monri.android;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
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
    public MonriHttpResult<Customer> createCustomer(@NonNull final CreateCustomerParams createCustomerParams) {
        try {
            final MonriHttpResult<JSONObject> response = httpsPOST(
                    baseUrl + "/v2/customers",
                    createCustomerParams.getCustomer().toJSON(),
                    new HashMap<>(){{
                        put("authorization", createCustomerParams.getAccessToken());
                    }}
            );
            return MonriHttpResult.success(Customer.fromJSON(response.getResult()), response.getResponseCode());
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<Customer> retrieveCustomer(final GetCustomerParams retrieveCustomerParams) {
        final MonriHttpResult<JSONObject> response = httpsGET(
                baseUrl + "/v2/customers/" + retrieveCustomerParams.getCustomerUuid(),
                new HashMap<>(){{
                    put("authorization", retrieveCustomerParams.getAccessToken());
                }}
        );

        try {
            if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                return MonriHttpResult.success(Customer.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(MonriHttpException.create(response.getResult().toString(), MonriHttpExceptionCode.REQUEST_FAILED));
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<Customer> retrieveCustomerViaMerchantCustomerId(final RetrieveCustomerViaMerchantCustomerUuidParams retrieveCustomerViaMerchantCustomerUuidParams) {
        final MonriHttpResult<JSONObject> response = httpsGET(
                baseUrl + "/v2/merchants/customers/" + retrieveCustomerViaMerchantCustomerUuidParams.getMerchantCustomerUuid(),
                new HashMap<>(){{
                    put("authorization", retrieveCustomerViaMerchantCustomerUuidParams.getAccessToken());
                }}
        );

        try {
            if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                return MonriHttpResult.success(Customer.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(MonriHttpException.create(response.getResult().toString(), MonriHttpExceptionCode.REQUEST_FAILED));
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<Customer> updateCustomer(@NonNull final UpdateCustomerParams updateCustomerParams) {
        try {
            final MonriHttpResult<JSONObject> response = httpsPOST(
                    baseUrl + "/v2/customers/" + updateCustomerParams.getCustomerUuid(),
                    updateCustomerParams.getCustomer().toJSON(),
                    new HashMap<>(){{
                        put("authorization", updateCustomerParams.getAccessToken());
                    }}
            );
            return MonriHttpResult.success(Customer.fromJSON(response.getResult()), response.getResponseCode());
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<DeleteCustomerResponse> deleteCustomer(final DeleteCustomerParams deleteCustomerParams) {
        final MonriHttpResult<JSONObject> response = httpsDELETE(
                baseUrl + "/v2/customers/" + deleteCustomerParams.getCustomerUuid(),
                new HashMap<>(){{
                    put("authorization", deleteCustomerParams.getAccessToken());
                }}
        );

        try {
            if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                return MonriHttpResult.success(DeleteCustomerResponse.fromJSON(response.getResult()), response.getResponseCode());
            } else {
                return MonriHttpResult.failed(MonriHttpException.create(response.getResult().toString(), MonriHttpExceptionCode.REQUEST_FAILED));
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<MerchantCustomers> retrieveAllCustomers(final String accessToken) {
        final MonriHttpResult<JSONObject> response = httpsGET(
                baseUrl + "/v2/customers",
                new HashMap<>(){{
                    put("authorization", accessToken);
                }}
        );

        try {
            if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                final MerchantCustomers merchantCustomers = MerchantCustomers.fromJSON(response.getResult());
                return MonriHttpResult.success(merchantCustomers, response.getResponseCode());
            } else {
                return MonriHttpResult.failed(MonriHttpException.create(response.getResult().toString(), MonriHttpExceptionCode.REQUEST_FAILED));
            }
        } catch (JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    @Override
    public MonriHttpResult<CustomerPaymentMethodResponse> getPaymentMethodsForCustomer(final CustomerPaymentMethodParams customerPaymentMethodParams) {
        final MonriHttpResult<JSONObject> response = httpsGET(
                baseUrl +
                        "/v2/customers/" +
                        customerPaymentMethodParams.getCustomerUuid() +
                        "/payment-methods?limit=" +
                        customerPaymentMethodParams.getLimit() +
                        "&offset="
                        + customerPaymentMethodParams.getOffset(),
                new HashMap<>(){{
                    put("authorization", customerPaymentMethodParams.getAccessToken());
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
