package com.monri.android

open class UrlProvider(baseUrl: String) {
    val baseUrl: String = if (baseUrl.endsWith(BASE_URL_TERMINATION_CHAR)) {
        baseUrl
    } else {
        "$baseUrl$BASE_URL_TERMINATION_CHAR"
    }

    companion object {
        private const val BASE_URL_TERMINATION_CHAR = '/'
    }
}