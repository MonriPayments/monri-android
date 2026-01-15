package com.monri.android

open class UrlProvider(var baseUrl: String) {

    companion object {
        private const val BASE_URL_TERMINATION_CHAR = '/'
    }

    init {
        if (!baseUrl.endsWith(BASE_URL_TERMINATION_CHAR)) {
            baseUrl += BASE_URL_TERMINATION_CHAR
        }
    }
}