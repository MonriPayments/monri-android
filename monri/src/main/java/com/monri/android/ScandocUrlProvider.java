package com.monri.android;

class ScanDocUrlProvider {
    private final String baseUrl;
    private static final String BASE_URL_TERMINATION_CHAR = "/";
    private static final String AUTHENTICATION_ENDPOINT = "authenticate";
    private static final String AUTHENTICATION_REFRESH_ENDPOINT = "authenticate/refresh";
    private static final String EXTRACTION_ENDPOINT = "extraction";
    private static final String VALIDATION_ENDPOINT = "validation";

    protected ScanDocUrlProvider(final String baseUrl) {
        this.baseUrl = baseUrl.endsWith(BASE_URL_TERMINATION_CHAR) ? baseUrl : baseUrl + BASE_URL_TERMINATION_CHAR;
    }

    protected String getAuthenticationUrl() {
        return baseUrl + AUTHENTICATION_ENDPOINT;
    }

    protected String getExtractionUrl() {
        return baseUrl + EXTRACTION_ENDPOINT;
    }

    protected String getValidationUrl() {
        return baseUrl + VALIDATION_ENDPOINT;
    }

    protected String getAuthenticateRefreshUrl() {
        return baseUrl + getAuthenticateRefreshUrl();
    }
}
