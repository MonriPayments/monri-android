package com.monri.android.http;

import androidx.annotation.VisibleForTesting;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentActionRequired;
import com.monri.android.model.PaymentMethodParams;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.PaymentStatus;
import com.monri.android.model.PaymentStatusResponse;
import com.monri.android.model.SavedCardPaymentMethod;
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
import java.util.List;
import java.util.Map;

public class MonriHttpApi {

    private final String baseUrl;
    private final Map<String, String> headers;

    private HttpURLConnection createHttpURLConnection(final String endpoint,
                                                      final MonriHttpMethod monriHttpMethod) throws IOException {
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

        return urlConnection;

    }

    public MonriHttpApi(final String baseUrl, final Map<String, String> headers) {
        this.baseUrl = baseUrl;
        this.headers = headers;
    }

    //post v2/payment/{id}/confirm
    public MonriHttpResult<ConfirmPaymentResponse> confirmPayment(ConfirmPaymentParams confirmPaymentParams) {
        HttpURLConnection urlConnection = null;

        try {
            final JSONObject confirmPaymentParamsJSON = confirmPaymentParamsToJSON(confirmPaymentParams);

            urlConnection = createHttpURLConnection(baseUrl + "/v2/payment/" + confirmPaymentParams.getPaymentId() + "/confirm", MonriHttpMethod.POST);

            OutputStreamWriter wr = null;

            try {
                wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(confirmPaymentParamsJSON.toString());
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

                return MonriHttpResult.success(ConfirmPaymentResponseJSONToClass(jsonResponse));

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

    //get v2/payment/{id}/status
    public MonriHttpResult<PaymentStatusResponse> paymentStatus(String id) {

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createHttpURLConnection(baseUrl + "/v2/payment/" + id + "/status", MonriHttpMethod.GET);

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonStringResponse = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    jsonStringResponse.append(line).append('\n');
                }

                final JSONObject jsonResponse = new JSONObject(jsonStringResponse.toString());

                return MonriHttpResult.success(paymentStatusResponseJSONToClass(jsonResponse));

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


    @VisibleForTesting
    public static ConfirmPaymentResponse ConfirmPaymentResponseJSONToClass(final JSONObject confirmPaymentResponseJSON) throws JSONException {
        final PaymentStatus status = PaymentStatus.forValue(confirmPaymentResponseJSON.getString("status"));

        PaymentActionRequired paymentActionRequired = null;

        if (confirmPaymentResponseJSON.has("action_required")) {

            final JSONObject actionRequiredJSON = confirmPaymentResponseJSON.getJSONObject("action_required");
            final String redirectTo = actionRequiredJSON.getString("redirect_to");
            final String acsUrl = actionRequiredJSON.getString("acs_url");
            paymentActionRequired = new PaymentActionRequired(redirectTo, acsUrl);
        }


        final JSONObject paymentResultJSON = confirmPaymentResponseJSON.getJSONObject("payment_result");
        final PaymentResult paymentResult = paymentResultJSONToClass(paymentResultJSON);

        String idFromResponse = null;

        if (confirmPaymentResponseJSON.has("client_secret")) {
            idFromResponse = confirmPaymentResponseJSON.getString("client_secret");
        }

        return new ConfirmPaymentResponse(status, paymentActionRequired, paymentResult, idFromResponse);
    }

    @VisibleForTesting
    public static PaymentStatusResponse paymentStatusResponseJSONToClass(final JSONObject paymentStatusResponseJSON) throws JSONException {
        final String status = paymentStatusResponseJSON.getString("status");
        final PaymentStatus paymentStatus = PaymentStatus.forValue(paymentStatusResponseJSON.getString("payment_status"));

        final JSONObject paymentResultJSON = paymentStatusResponseJSON.getJSONObject("payment_result");
        final PaymentResult paymentResult = paymentResultJSONToClass(paymentResultJSON);

        return new PaymentStatusResponse(
                paymentStatus,
                status,
                paymentResult
        );

    }

    @VisibleForTesting
    public static PaymentResult paymentResultJSONToClass(JSONObject paymentResultJSON) throws JSONException {

        final String paymentStatusResult = paymentResultJSON.getString("status");
        final String paymentStatusCurrency = paymentResultJSON.getString("currency");
        final Integer paymentStatusAmount = paymentResultJSON.getInt("amount");
        final String paymentStatusOrderNumber = paymentResultJSON.getString("order_number");

        String paymentStatusPanToken = "null";

        if (paymentResultJSON.has("pan_token")) {
            paymentStatusPanToken = paymentResultJSON.getString("pan_token");
        }

        final String paymentStatusCreatedAt = paymentResultJSON.getString("created_at");
        final String paymentStatusTransactionType = paymentResultJSON.getString("transaction_type");

        SavedCardPaymentMethod savedCardPaymentMethod = null;

        if (paymentResultJSON.has("payment_method")) {
            final JSONObject paymentStatusPaymentMethodJSON = paymentResultJSON.getJSONObject("payment_method");
            final String paymentStatusPaymentMethodType = paymentStatusPaymentMethodJSON.getString("type");
            final JSONObject pmData = paymentStatusPaymentMethodJSON.getJSONObject("data");
            final String brand = pmData.getString("brand");
            final String issuer = pmData.getString("issuer");
            final String masked = pmData.getString("masked");
            final String expiration_date = pmData.getString("expiration_date");
            final String token = pmData.getString("token");
            final SavedCardPaymentMethod.Data data = new SavedCardPaymentMethod.Data(brand, issuer, masked, expiration_date, token);

            savedCardPaymentMethod = new SavedCardPaymentMethod(
                    paymentStatusPaymentMethodType,
                    data
            );
        }

        List<String> paymentStatusErrors = null;

        if (paymentResultJSON.has("errors")) {
            paymentStatusErrors = new ArrayList<>();
            JSONArray jsonArray = paymentResultJSON.getJSONArray("errors");
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0;i<len;i++){
                    paymentStatusErrors.add(jsonArray.get(i).toString());
                }
            }
        }

        PaymentResult paymentResult = new PaymentResult(
                paymentStatusResult,
                paymentStatusCurrency,
                paymentStatusAmount,
                paymentStatusOrderNumber,
                paymentStatusPanToken,
                paymentStatusCreatedAt,
                paymentStatusTransactionType,
                savedCardPaymentMethod,
                paymentStatusErrors
        );

        return paymentResult;
    }

    @VisibleForTesting
    public static JSONObject confirmPaymentParamsToJSON(ConfirmPaymentParams confirmPaymentParams) throws JSONException {
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
        final Map<String, String> transactionData = transaction.getData();
        JSONObject dataTransactionMapJSON = new JSONObject();

        for (String key : transactionData.keySet()) {
            dataTransactionMapJSON.put(key, transactionData.get(key));
        }

        JSONObject confirmPaymentParamsJSON = new JSONObject();
        confirmPaymentParamsJSON.put("payment_method", paymentMethodJSON);
        confirmPaymentParamsJSON.put("transaction", dataTransactionMapJSON);//check maybe this should not be map, maybe it should be TransactionParams object..
        return confirmPaymentParamsJSON;
    }

}
