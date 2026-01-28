package com.monri.android

import java.net.HttpURLConnection

data class HttpsResponse(
    @JvmField val httpCode: Int,
    @JvmField val body: String
) {
    fun isSuccess(): Boolean {
        return httpCode >= HttpURLConnection.HTTP_OK && httpCode < HttpURLConnection.HTTP_MULT_CHOICE
    }
}