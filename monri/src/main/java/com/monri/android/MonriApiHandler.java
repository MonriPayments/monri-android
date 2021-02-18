package com.monri.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.monri.android.exception.APIConnectionException;
import com.monri.android.exception.APIException;
import com.monri.android.exception.AuthenticationException;
import com.monri.android.exception.CardException;
import com.monri.android.exception.InvalidRequestException;
import com.monri.android.exception.PermissionException;
import com.monri.android.exception.RateLimitException;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Handler for calls to the Monri API.
 */
public class MonriApiHandler {

    private static final String POST = "POST";

    //    TODO: fix live vs test issue
    // Add somekind of api gateway?
    private static final String API_BASE = "https://ipgtest.monri.com";

    private static final String CHARSET = "UTF-8";
    private static final String TOKENS = "temp-tokenize";
    private static final String DNS_CACHE_TTL_PROPERTY_NAME = "networkaddress.cache.ttl";
    private static final SSLSocketFactory SSL_SOCKET_FACTORY = new MonriSSLSocketFactory();

    /**
     * Create a {@link Token} using the input token parameters.
     *
     *
     * @param apiOptions
     * @param tokenParams a mapped set of parameters representing the object for which this token
     *                    is being created
     * @return a {@link Token} that can be used to perform other operations with this card
     * @throws AuthenticationException if there is a problem authenticating to the Monri API
     * @throws InvalidRequestException if one or more of the parameters is incorrect
     * @throws APIConnectionException  if there is a problem connecting to the Monri API
     * @throws CardException           if there is a problem with the card information
     * @throws APIException            for unknown Monri API errors. These should be rare.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    static Token createToken(
            MonriApiOptions apiOptions, @NonNull Map<String, Object> tokenParams)
            throws AuthenticationException,
            InvalidRequestException,
            APIConnectionException,
            CardException,
            APIException {

        return requestToken(getApiUrl(apiOptions), tokenParams);
    }


    static String getApiUrl(MonriApiOptions options) {
        return String.format(Locale.ENGLISH, "%s/v2/%s", options.url(), TOKENS);
    }

    /**
     * Converts a string-keyed {@link Map} into a {@link JSONObject}. This will cause a
     * {@link ClassCastException} if any sub-map has keys that are not {@link String Strings}.
     *
     * @param mapObject the {@link Map} that you'd like in JSON form
     * @return a {@link JSONObject} representing the input map, or {@code null} if the input
     * object is {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private static JSONObject mapToJsonObject(@Nullable Map<String, ? extends Object> mapObject) {
        if (mapObject == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        for (String key : mapObject.keySet()) {
            Object value = mapObject.get(key);
            if (value == null) {
                continue;
            }

            try {
                if (value instanceof Map<?, ?>) {
                    try {
                        Map<String, Object> mapValue = (Map<String, Object>) value;
                        jsonObject.put(key, mapToJsonObject(mapValue));
                    } catch (ClassCastException classCastException) {
                        // We don't include the item in the JSONObject if the keys are not Strings.
                    }
                } else if (value instanceof List<?>) {
                    jsonObject.put(key, listToJsonArray((List<Object>) value));
                } else if (value instanceof Number || value instanceof Boolean) {
                    jsonObject.put(key, value);
                } else {
                    jsonObject.put(key, value.toString());
                }
            } catch (JSONException jsonException) {
                // Simply skip this value
            }
        }
        return jsonObject;
    }

    /**
     * Converts a {@link List} into a {@link JSONArray}. A {@link ClassCastException} will be
     * thrown if any object in the list (or any sub-list or sub-map) is a {@link Map} whose keys
     * are not {@link String Strings}.
     *
     * @param values a {@link List} of values to be put in a {@link JSONArray}
     * @return a {@link JSONArray}, or {@code null} if the input was {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private static JSONArray listToJsonArray(@Nullable List values) {
        if (values == null) {
            return null;
        }

        JSONArray jsonArray = new JSONArray();
        for (Object object : values) {
            if (object instanceof Map<?, ?>) {
                // We are ignoring type erasure here and crashing on bad input.
                // Now that this method is not public, we have more control on what is
                // passed to it.
                Map<String, Object> mapObject = (Map<String, Object>) object;
                jsonArray.put(mapToJsonObject(mapObject));
            } else if (object instanceof List<?>) {
                jsonArray.put(listToJsonArray((List) object));
            } else if (object instanceof Number || object instanceof Boolean) {
                jsonArray.put(object);
            } else {
                jsonArray.put(object.toString());
            }
        }
        return jsonArray;
    }

    private static java.net.HttpURLConnection createPostConnection(
            @NonNull String url,
            @NonNull Map<String, Object> params) throws IOException, InvalidRequestException {
        java.net.HttpURLConnection conn = createMonriConnection(url);

        conn.setDoOutput(true);
        conn.setRequestMethod(POST);
        conn.setRequestProperty("Content-Type", getContentType());

        OutputStream output = null;
        try {
            output = conn.getOutputStream();
            output.write(getOutputBytes(params));
        } finally {
            if (output != null) {
                output.close();
            }
        }
        return conn;
    }

    private static java.net.HttpURLConnection createMonriConnection(
            String url)
            throws IOException {
        URL monriUrl;

        monriUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) monriUrl.openConnection();
        conn.setConnectTimeout(30 * 1000);
        conn.setReadTimeout(80 * 1000);
        conn.setUseCaches(false);

        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(SSL_SOCKET_FACTORY);
        }

        return conn;
    }

    private static String getContentType() {
        return String.format("application/json; charset=%s", CHARSET);
    }

    private static byte[] getOutputBytes(
            @NonNull Map<String, Object> params) throws InvalidRequestException {
        try {
            JSONObject jsonData = mapToJsonObject(params);
            if (jsonData == null) {
                throw new InvalidRequestException("Unable to create JSON data from parameters. "
                                                          + "Please contact support@monri.com for assistance.",
                                                  null, null, 0, null);
            }
            return jsonData.toString().getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new InvalidRequestException("Unable to encode parameters to "
                                                      + CHARSET
                                                      + ". Please contact support@monri.com for assistance.",
                                              null, null, 0, e);
        }
    }

    private static String getResponseBody(InputStream responseStream)
            throws IOException {
        //\A is the beginning of
        // the stream boundary
        Scanner scanner = new Scanner(responseStream, CHARSET).useDelimiter("\\A");
        String rBody = scanner.hasNext() ? scanner.next() : null;
        responseStream.close();
        return rBody;
    }

    private static MonriResponse getMonriResponse(
            String url,
            Map<String, Object> params)
            throws InvalidRequestException, APIConnectionException, APIException {
        // HTTPSURLConnection verifies SSL cert by default
        java.net.HttpURLConnection conn = null;
        try {
            conn = createPostConnection(url, params);
            // trigger the request
            int rCode = conn.getResponseCode();
            String rBody;
            Map<String, List<String>> headers;

            if (rCode >= 200 && rCode < 300) {
                rBody = getResponseBody(conn.getInputStream());
            } else {
                rBody = getResponseBody(conn.getErrorStream());
            }
            headers = conn.getHeaderFields();
            return new MonriResponse(rCode, rBody, headers);

        } catch (IOException e) {
            throw new APIConnectionException(
                    String.format(
                            "IOException during API request to Monri (%s): %s "
                                    + "Please check your internet connection and try again. "
                                    + "If this problem persists, you should check Monri's "
                                    + "service status at https://twitter.com/monristatus, "
                                    + "or let us know at support@monri.com.",
                            url, e.getMessage()), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static void handleAPIError(String rBody, int rCode, String requestId)
            throws InvalidRequestException, AuthenticationException,
            CardException, APIException {

        ErrorParser.MonriError monriError = ErrorParser.parseError(rBody);
        switch (rCode) {
            case 400:
                throw new InvalidRequestException(
                        monriError.message,
                        monriError.param,
                        requestId,
                        rCode,
                        null);
            case 404:
                throw new InvalidRequestException(
                        monriError.message,
                        monriError.param,
                        requestId,
                        rCode,
                        null);
            case 401:
                throw new AuthenticationException(monriError.message, requestId, rCode);
            case 402:
                throw new CardException(
                        monriError.message,
                        requestId,
                        monriError.code,
                        monriError.param,
                        monriError.decline_code,
                        monriError.charge,
                        rCode,
                        null);
            case 403:
                throw new PermissionException(monriError.message, requestId, rCode);
            case 429:
                throw new RateLimitException(monriError.message, monriError.param, requestId,
                                             rCode, null);
            default:
                throw new APIException(monriError.message, requestId, rCode, null);
        }
    }

    private static MonriResponse requestData(
            String url,
            Map<String, Object> params)
            throws AuthenticationException, InvalidRequestException,
            APIConnectionException, CardException, APIException {

        String originalDNSCacheTTL = null;
        boolean allowedToSetTTL = true;

        try {
            originalDNSCacheTTL = java.security.Security
                    .getProperty(DNS_CACHE_TTL_PROPERTY_NAME);
            // disable DNS cache
            java.security.Security
                    .setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "0");
        } catch (SecurityException se) {
            allowedToSetTTL = false;
        }

        MonriResponse response = getMonriResponse(url, params);

        int rCode = response.getResponseCode();
        String rBody = response.getResponseBody();

        String requestId = null;
        Map<String, List<String>> headers = response.getResponseHeaders();
        List<String> requestIdList = headers == null ? null : headers.get("Request-Id");
        if (requestIdList != null && requestIdList.size() > 0) {
            requestId = requestIdList.get(0);
        }

        if (rCode < 200 || rCode >= 300) {
            handleAPIError(rBody, rCode, requestId);
        }

        if (allowedToSetTTL) {
            if (originalDNSCacheTTL == null) {
                // value unspecified by implementation
                // DNS_CACHE_TTL_PROPERTY_NAME of -1 = cache forever
                java.security.Security.setProperty(
                        DNS_CACHE_TTL_PROPERTY_NAME, "-1");
            } else {
                java.security.Security.setProperty(
                        DNS_CACHE_TTL_PROPERTY_NAME, originalDNSCacheTTL);
            }
        }
        return response;
    }

    @Nullable
    private static Token requestToken(
            String url,
            Map<String, Object> params)
            throws AuthenticationException, InvalidRequestException,
            APIConnectionException, CardException, APIException {
        MonriResponse response = requestData(url, params);
        return Token.fromString(response.getResponseBody());
    }

}
