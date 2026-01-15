package com.monri.android

import org.json.JSONObject
import java.net.HttpURLConnection

internal class HttpsClientProxyImpl(): HttpsClientProxy {

    companion object {
        private const val RESPONSE_ERROR_MESSAGE_KEY = "message"
    }

    private val httpsClient = HttpsClient()

    override fun <T> doPostRequest(url: String, headers: Map<String, String>, body: String?, useChunkedStreamingMode: Boolean, jsonResponseMapper: JsonResponseMapper<T>): MonriHttpResult<T> {
        val request = HttpsRequest.Post(url, headers, body, useChunkedStreamingMode)

        return doHttpsRequest(request, jsonResponseMapper)
    }

    override fun <T> doGetRequest(url: String, headers: Map<String, String>, jsonResponseMapper: JsonResponseMapper<T>): MonriHttpResult<T> {
        val request = HttpsRequest.Get(url, headers)

        return doHttpsRequest(request, jsonResponseMapper)
    }

    override fun <T> doDeleteRequest(url: String, headers: Map<String, String>, jsonResponseMapper: JsonResponseMapper<T>): MonriHttpResult<T> {
        val request = HttpsRequest.Delete(url, headers)

        return doHttpsRequest(request, jsonResponseMapper)
    }

    override fun <T> doHttpsRequest(httpsRequest: HttpsRequest, jsonResponseMapper: JsonResponseMapper<T>): MonriHttpResult<T> {
        val response = when (httpsRequest) {
            is HttpsRequest.Get -> httpsClient.httpsGET(httpsRequest)
            is HttpsRequest.Delete -> httpsClient.httpsDELETE(httpsRequest)
            is HttpsRequest.Post -> httpsClient.httpsPOST(httpsRequest)
        }
        val responseJsonObject = JSONObject(response.body)

        return if (response.httpCode >= HttpURLConnection.HTTP_OK && response.httpCode < HttpURLConnection.HTTP_MULT_CHOICE) {
            MonriHttpResult.success(jsonResponseMapper.map(responseJsonObject), response.httpCode)
        } else {
            val errorMessage = if (responseJsonObject.has(RESPONSE_ERROR_MESSAGE_KEY)) responseJsonObject.getString(RESPONSE_ERROR_MESSAGE_KEY) else response.body
            MonriHttpResult.failed(MonriHttpException.create(errorMessage, MonriHttpExceptionCode.REQUEST_FAILED))
        }
    }
}