package com.monri.android;

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
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpsClient {
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    private static final String RESPONSE_ERROR_MESSAGE_KEY = "message";

    protected MonriHttpResult<JSONObject> httpsPOST(
            final String endpoint,
            final JSONObject body,
            final Map<String, String> headers,
            final boolean useChunkedStreamingMode
    ) {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = createHttpPOSTURLConnection(endpoint, headers);

            if (useChunkedStreamingMode) {
                urlConnection.setChunkedStreamingMode(0);
            } else {
                final int contentLength = body.toString().getBytes(StandardCharsets.UTF_8).length;
                urlConnection.setFixedLengthStreamingMode(contentLength);
                urlConnection.addRequestProperty(CONTENT_LENGTH_HEADER, String.valueOf(contentLength));
                urlConnection.addRequestProperty(CONTENT_TYPE_HEADER, APPLICATION_JSON_CONTENT_TYPE);
            }

            writeToOutputStream(urlConnection, body);

            return readResponseFromInputStream(urlConnection);

        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    protected MonriHttpResult<JSONObject> httpsPOST(final String endpoint, final Map<String, String> headers) {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = createHttpURLConnection(endpoint, MonriHttpMethod.POST, headers);

            return readResponseFromInputStream(urlConnection);
        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    protected MonriHttpResult<JSONObject> httpsGET(
            final String endpoint,
            final Map<String, String> headers
    ) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createHttpURLConnection(endpoint, MonriHttpMethod.GET, headers);

            return readResponseFromInputStream(urlConnection);

        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }
    }

    protected MonriHttpResult<JSONObject> httpsDELETE(
            final String endpoint,
            final Map<String, String> headers
    ) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createHttpURLConnection(endpoint, MonriHttpMethod.DELETE, headers);

            return readResponseFromInputStream(urlConnection);

        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return MonriHttpResult.failed(MonriHttpException.create(e, MonriHttpExceptionCode.REQUEST_FAILED));
        }

    }

    private HttpURLConnection createHttpURLConnection(
            final String endpoint,
            final MonriHttpMethod monriHttpMethod,
            final Map<String, String> headers
    ) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(monriHttpMethod.getValue());

        addHeadersToConnection(urlConnection, headers);

        return urlConnection;
    }

    private HttpURLConnection createHttpPOSTURLConnection(
            final String endpoint,
            final Map<String, String> additionalHeader
    ) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(MonriHttpMethod.POST.getValue());

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);

        addHeadersToConnection(urlConnection, additionalHeader);

        return urlConnection;
    }

    protected void addHeadersToConnection(final HttpURLConnection urlConnection, final Map<String, String> headers) {
        for (String key : headers.keySet()) {
            urlConnection.setRequestProperty(key, headers.get(key));
        }
    }

    private static void writeToOutputStream(final HttpURLConnection urlConnection, final JSONObject body) throws IOException {
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
    }

    private static MonriHttpResult<JSONObject> readResponseFromInputStream(final HttpURLConnection urlConnection) throws IOException, JSONException {
        try {
            int responseCode = urlConnection.getResponseCode();
            InputStream inputStream;
            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                inputStream = urlConnection.getInputStream();
            } else {
                inputStream = urlConnection.getErrorStream();
            }

            InputStream in = new BufferedInputStream(inputStream);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonStringResponse = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                jsonStringResponse.append(line).append('\n');
            }

            JSONObject jsonResponse = new JSONObject(jsonStringResponse.toString());

            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                return MonriHttpResult.success(jsonResponse, urlConnection.getResponseCode());
            } else {
                String errorMessage = jsonResponse.has(RESPONSE_ERROR_MESSAGE_KEY) ? jsonResponse.getString(RESPONSE_ERROR_MESSAGE_KEY) : jsonResponse.toString();
                return MonriHttpResult.failed(MonriHttpException.create(errorMessage, MonriHttpExceptionCode.REQUEST_FAILED));
            }

        } finally {
            urlConnection.disconnect();
        }
    }
}
