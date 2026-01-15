package com.monri.android

internal class ScanDocUrlProvider(baseUrl: String): UrlProvider(baseUrl) {

    companion object {
        private const val AUTHENTICATION_BASE_URL = "https://api.scandoc.ai/ks/"
        private const val AUTHENTICATION_ENDPOINT = "authenticate/"
        private const val AUTHENTICATION_REFRESH_ENDPOINT = "authenticate/refresh"
        private const val EXTRACTION_ENDPOINT = "extraction/"
        private const val VALIDATION_ENDPOINT = "validation/"
    }

    val authenticationUrl: String
        get() = AUTHENTICATION_BASE_URL + AUTHENTICATION_ENDPOINT

    val extractionUrl: String
        get() = baseUrl + EXTRACTION_ENDPOINT

    val validationUrl: String
        get() = baseUrl + VALIDATION_ENDPOINT

    val authenticateRefreshUrl: String
        get() = AUTHENTICATION_BASE_URL + AUTHENTICATION_REFRESH_ENDPOINT
}
