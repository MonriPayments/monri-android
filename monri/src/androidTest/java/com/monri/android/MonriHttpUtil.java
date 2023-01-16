package com.monri.android;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

class MonriHttpUtil {

    private static HttpURLConnection createHttpURLConnection(
            final String endpoint,
            final MonriHttpMethod monriHttpMethod,
            final Map<String, String> headers
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

        return urlConnection;
    }


    public static MonriHttpResult<JSONObject> httpsGET(
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

    public static MonriHttpResult<JSONObject> httpsPOST(
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
}
