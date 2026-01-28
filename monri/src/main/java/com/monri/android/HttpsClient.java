package com.monri.android;

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
    private static final char NEWLINE_CHAR = '\n';

    protected HttpsResponse httpsPOST(
            final HttpsRequest.Post httpRequest
    ) throws IOException {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = createHttpURLConnection(httpRequest, true, false);

            if (httpRequest.body != null) {
                if (httpRequest.useChunkedStreamingMode) {
                    urlConnection.setChunkedStreamingMode(0);
                } else {
                    final int contentLength = httpRequest.body.getBytes(StandardCharsets.UTF_8).length;
                    urlConnection.setFixedLengthStreamingMode(contentLength);
                    urlConnection.addRequestProperty(CONTENT_LENGTH_HEADER, String.valueOf(contentLength));
                    urlConnection.addRequestProperty(CONTENT_TYPE_HEADER, APPLICATION_JSON_CONTENT_TYPE);
                }

                writeToOutputStream(urlConnection, httpRequest.body);
            }

            return readResponseFromInputStream(urlConnection);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    protected HttpsResponse httpsGET(final HttpsRequest.Get httpsRequest) throws IOException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createHttpURLConnection(httpsRequest, false, true);

            return readResponseFromInputStream(urlConnection);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    protected HttpsResponse httpsDELETE(final HttpsRequest.Delete httpsRequest) throws IOException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createHttpURLConnection(httpsRequest, false, true);

            return readResponseFromInputStream(urlConnection);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private HttpURLConnection createHttpURLConnection(
            final HttpsRequest httpsRequest,
            final boolean doOutput,
            final boolean useCaches
    ) throws IOException {
        final URL url = new URL(httpsRequest.getUrl());
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(httpsRequest.getTypeName());

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(doOutput);
        urlConnection.setUseCaches(useCaches);

        addHeadersToConnection(urlConnection, httpsRequest.getHeaders());

        return urlConnection;
    }

    protected void addHeadersToConnection(final HttpURLConnection urlConnection, final Map<String, String> headers) {
        for (final String key : headers.keySet()) {
            urlConnection.setRequestProperty(key, headers.get(key));
        }
    }

    private static void writeToOutputStream(final HttpURLConnection urlConnection, final String body) throws IOException {
        try (final OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream())) {
            wr.write(body);
            wr.flush();
        }
    }

    private static HttpsResponse readResponseFromInputStream(final HttpURLConnection urlConnection) throws IOException {
        try {
            final int responseCode = urlConnection.getResponseCode();
            final InputStream inputStream;
            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                inputStream = urlConnection.getInputStream();
            } else {
                inputStream = urlConnection.getErrorStream();
            }

            final InputStream in = new BufferedInputStream(inputStream);
            final BufferedReader r = new BufferedReader(new InputStreamReader(in));
            final StringBuilder stringResponse = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                stringResponse.append(line).append(NEWLINE_CHAR);
            }

            return new HttpsResponse(responseCode, stringResponse.toString());
        } finally {
            urlConnection.disconnect();
        }
    }
}
