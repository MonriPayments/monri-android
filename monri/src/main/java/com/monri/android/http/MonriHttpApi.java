package com.monri.android.http;

import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.ConfirmPaymentResponse;
import com.monri.android.model.PaymentMethodParams;
import com.monri.android.model.PaymentResult;
import com.monri.android.model.PaymentStatus;
import com.monri.android.model.PaymentStatusResponse;
import com.monri.android.model.SavedCardPaymentMethod;
import com.monri.android.model.TransactionParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MonriHttpApi {

    private final String baseUrl;
    private final Map<String, String> params;

    public MonriHttpApi(final String baseUrl, final Map<String, String> params) {
        this.baseUrl = baseUrl;
        this.params = params;
    }

    //post v2/payment/{id}/confirm
    MonriHttpResult<ConfirmPaymentResponse> confirmPayment(String id, ConfirmPaymentParams params) {

        try {
            //converting PaymentMethodParams to JSON
            final PaymentMethodParams paymentMethod = params.getPaymentMethod();
            final String type = paymentMethod.getType();
            final Map<String, String> data = paymentMethod.getData();

            JSONObject dataMapJSON = new JSONObject();

            for (String key : data.keySet()) {
                dataMapJSON.put(key, data.get(key));
            }

            JSONObject paymentMethodJSON = new JSONObject();

            paymentMethodJSON.put("type", type);
            paymentMethodJSON.put("data", dataMapJSON);

            //converting transactionParams to JSON
            final TransactionParams transaction = params.getTransaction();
            transaction.getData();
            JSONObject dataTransactionMapJSON = new JSONObject();

            for (String key : data.keySet()) {
                dataTransactionMapJSON.put(key, data.get(key));
            }


            JSONObject confirmPaymentParamsJSON = new JSONObject();
            confirmPaymentParamsJSON.put("payment_method", paymentMethodJSON);
            confirmPaymentParamsJSON.put("transaction", dataTransactionMapJSON);//check maybe this should not be map, maybe it should be TransactionParams object..

            URL url = new URL(baseUrl + "/v2/payment/" + id + "/confirm");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
//            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            urlConnection.setRequestProperty("Accept", "application/json");

            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(confirmPaymentParamsJSON.toString());
            wr.flush();
            wr.close();

            //now read response
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }

                JSONObject jsonObject = new JSONObject(total.toString());

                //without compiler error..
                return MonriHttpResult.success(new ConfirmPaymentResponse());

            }catch (Exception e){
                urlConnection.disconnect();
                return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
            }


        } catch (IOException | JSONException e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.JSON_PARSE_ERROR));
        }

    }

    //get v2/payment/{id}/status
    public MonriHttpResult<PaymentStatusResponse> paymentStatus(String id) {

        try {
            // Instantiate the RequestQueue.
            URL url = new URL(baseUrl + "/v2/payment/" + id + "/status");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            //headers
            for (String key : params.keySet()) {
                urlConnection.setRequestProperty(key, params.get(key));
            }

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }

                JSONObject jsonObject = new JSONObject(total.toString());

                final String status = jsonObject.getString("status");
                final PaymentStatus paymentStatus = PaymentStatus.forValue(jsonObject.getString("payment_status"));

                final JSONObject paymentResultJSON = jsonObject.getJSONObject("payment_result");

                final String paymentStatusResult = paymentResultJSON.getString("status");
                final String paymentStatusCurrency = paymentResultJSON.getString("currency");
                final Integer paymentStatusAmount = paymentResultJSON.getInt("amount");
                final String paymentStatusOrderNumber = paymentResultJSON.getString("order_number");
                final String paymentStatusPanToken = paymentResultJSON.getString("pan_token");
                final String paymentStatusCreatedAt = paymentResultJSON.getString("created_at");
                final String paymentStatusTransactionType = paymentResultJSON.getString("transaction_type");

                SavedCardPaymentMethod savedCardPaymentMethod = new SavedCardPaymentMethod();

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

                List<String> paymentStatusErrors = new ArrayList<>();

                if (paymentResultJSON.has("errors")) {
                    paymentStatusErrors = (List<String>) paymentResultJSON.getJSONArray("errors");
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

                final PaymentStatusResponse paymentStatusResponse = new PaymentStatusResponse(
                        paymentStatus,
                        status,
                        paymentResult
                );

                return MonriHttpResult.success(paymentStatusResponse);

            } finally {
                urlConnection.disconnect();
            }

        } catch (Exception e) {
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

}
